package com.kw.services.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.api.client.util.IOUtils;
import com.google.cloud.vision.v1.*;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.protobuf.ByteString;
import com.google.protobuf.FieldMask;
import com.kw.beans.ProductSearchBean;
import com.kw.configs.ConfigsProperties;
import com.kw.constants.Constant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VisionAIServices {

	@Autowired
    ConfigsProperties configsProperties;
	
	/**
	 * Run it once only Create a product set
	 *
	 * @param projectId             - Id of the project.
	 * @param computeRegion         - Region name.
	 * @param productSetId          - Id of the product set.
	 * @param productSetDisplayName - Display name of the product set.
	 * @throws IOException - on I/O errors.
	 */
	public void createProductSet(String projectId, String computeRegion, String productSetId,
			String productSetDisplayName) throws IOException {
		try (ProductSearchClient client = ProductSearchClient.create()) {

			// A resource that represents Google Cloud Platform location.
			String formattedParent = ProductSearchClient.formatLocationName(projectId, computeRegion);

			// Create a product set with the product set specification in the region.
			ProductSet myProductSet = ProductSet.newBuilder().setDisplayName(productSetDisplayName).build();
			CreateProductSetRequest request = CreateProductSetRequest.newBuilder().setParent(formattedParent)
					.setProductSet(myProductSet).setProductSetId(productSetId).build();
			ProductSet productSet = client.createProductSet(request);

			// Display the product set information
			System.out.println(String.format("Product set name: %s", productSet.getName()));
			System.out.println(String.format("Product set display name: %s", productSet.getDisplayName()));

		}
	}

	/**
	 * Create one product.
	 *
	 * @param projectId          - Id of the project.
	 * @param computeRegion      - Region name.
	 * @param productId          - Id of the product.
	 * @param productDisplayName - Display name of the product.
	 * @param productCategory    - Category of the product.
	 * @throws IOException - on I/O errors.
	 */
	public void createProduct(String projectId, String computeRegion, String productId, String productDisplayName,
			String productCategory, String productDesc, Iterable<Product.KeyValue> productLabelsLst)
			throws IOException {
		try (ProductSearchClient client = ProductSearchClient.create()) {

			// A resource that represents Google Cloud Platform location.
			String formattedParent = ProductSearchClient.formatLocationName(projectId, computeRegion);
			// Create a product with the product specification in the region.
			// Multiple labels are also supported.

			Product myProduct;
			if (null != productLabelsLst) {
				myProduct = Product.newBuilder().setName(productId).setDisplayName(productDisplayName)
						.setProductCategory(productCategory).setDescription(productDesc)
						.addAllProductLabels(productLabelsLst).build();
			} else {
				myProduct = Product.newBuilder().setName(productId).setDisplayName(productDisplayName)
						.setProductCategory(productCategory).setDescription(productDesc).build();
			}

			Product product = client.createProduct(formattedParent, myProduct, productId);
			// Display the product information
			System.out.println(String.format("Product Id: %s", product.getName()));
			System.out.println(String.format("Product Display Name: %s", product.getDisplayName()));
			System.out.println(String.format("Product Category: %s", product.getProductCategory()));
		}
	}

	/**
	 * Add a product to a product set.
	 *
	 * @param projectId     - Id of the project.
	 * @param computeRegion - Region name.
	 * @param productId     - Id of the product.
	 * @param productSetId  - Id of the product set.
	 * @throws IOException - on I/O errors.
	 */
	public void addProductToProductSet(String projectId, String computeRegion, String productId, String productSetId)
			throws IOException {
		try (ProductSearchClient client = ProductSearchClient.create()) {

			// Get the full path of the product set.
			String formattedName = ProductSearchClient.formatProductSetName(projectId, computeRegion, productSetId);

			// Get the full path of the product.
			String productPath = ProductName.of(projectId, computeRegion, productId).toString();

			// Add the product to the product set.
			client.addProductToProductSet(formattedName, productPath);

			System.out.println(String.format("Product added to product set."));
		}
	}

	/**
	 * Create a reference image.
	 *
	 * @param projectId        - Id of the project.
	 * @param computeRegion    - Region name.
	 * @param productId        - Id of the product.
	 * @param referenceImageId - Id of the image.
	 * @param gcsUri           - Google Cloud Storage path of the input image.
	 * @throws IOException - on I/O errors.
	 */
	public void createReferenceImage(String projectId, String computeRegion, String productId, String referenceImageId,
			String gcsUri) throws IOException {
		try (ProductSearchClient client = ProductSearchClient.create()) {

			// Get the full path of the product.
			String formattedParent = ProductSearchClient.formatProductName(projectId, computeRegion, productId);
			// Create a reference image.
			ReferenceImage referenceImage = ReferenceImage.newBuilder().setUri(gcsUri).build();
			ReferenceImage image = client.createReferenceImage(formattedParent, referenceImage, referenceImageId);
			// Display the reference image information.
			System.out.println(String.format("Reference image id: %s", referenceImageId));
			System.out.println(String.format("Reference image name: %s", image.getName()));
			System.out.println(String.format("Reference image uri: %s", image.getUri()));

		}
	}

	/**
	 * Search similar products to image in local file.
	 *
	 * @param projectId       - Id of the project.
	 * @param computeRegion   - Region name.
	 * @param productSetId    - Id of the product set.
	 * @param productCategory - Category of the product.
	 * @param filePath        - Local file path of the image to be searched
	 * @param filter          - Condition to be applied on the labels. Example for
	 *                        filter: (color = red OR color = blue) AND style = kids
	 *                        It will search on all products with the following
	 *                        labels: color:red AND style:kids color:blue AND
	 *                        style:kids
	 * @throws IOException - on I/O errors.
	 */
	public List<ProductSearchBean> getSimilarProductsFile(String projectId, String computeRegion, String productSetId,
			String productCategory, InputStream fis, String filter) throws IOException {

		List<ProductSearchBean> resultLst = null;

		try (ImageAnnotatorClient queryImageClient = ImageAnnotatorClient.create()) {

			// Get the full path of the product set.
			String productSetPath = ProductSearchClient.formatProductSetName(projectId, computeRegion, productSetId);

			// Read the image as a stream of bytes.
			byte[] content = new byte[fis.available()];
			fis.read(content);

			// File imgPath = new File(filePath);
			// byte[] content = Files.readAllBytes(imgPath.toPath());

			// Create annotate image request along with product search feature.
			Feature featuresElement = Feature.newBuilder().setType(Feature.Type.PRODUCT_SEARCH).build();
			// The input image can be a HTTPS link or Raw image bytes.
			// Example:
			// To use HTTP link replace with below code
			// ImageSource source = ImageSource.newBuilder().setImageUri(imageUri).build();
			// Image image = Image.newBuilder().setSource(source).build();
			Image image = Image.newBuilder().setContent(ByteString.copyFrom(content)).build();
			ImageContext imageContext = ImageContext.newBuilder().setProductSearchParams(ProductSearchParams
					.newBuilder().setProductSet(productSetPath).addProductCategories(productCategory).setFilter(filter))
					.build();

			AnnotateImageRequest annotateImageRequest = AnnotateImageRequest.newBuilder().addFeatures(featuresElement)
					.setImage(image).setImageContext(imageContext).build();
			List<AnnotateImageRequest> requests = Arrays.asList(annotateImageRequest);

			// Search products similar to the image.
			BatchAnnotateImagesResponse response = queryImageClient.batchAnnotateImages(requests);

			List<ProductSearchResults.Result> similarProducts = response.getResponses(0).getProductSearchResults()
					.getResultsList();
			System.out.println("Similar Products: ");

			resultLst = new ArrayList<ProductSearchBean>();

			for (ProductSearchResults.Result product : similarProducts) {

				ProductSearchBean bean = new ProductSearchBean();

				if (null != product.getProduct().getDescription()) {
					bean.setId(product.getProduct().getDescription());
				}

				bean.setScore(product.getScore());
				bean.setId(product.getProduct().getName().substring(product.getProduct().getName().lastIndexOf('/') + 1));
				bean.setRef_image_id(product.getImage().substring(product.getImage().lastIndexOf('/') + 1));
				bean.setGcp_productname(product.getProduct().getName());
				resultLst.add(bean);
				System.out.println(String.format("\nProduct id: %s", bean.getId()));
				System.out.println(String.format("\nProduct name: %s", product.getProduct().getName()));
				System.out.println(String.format("Product display name: %s", product.getProduct().getDisplayName()));
				System.out.println(String.format("Product description: %s", product.getProduct().getDescription()));
				System.out.println(String.format("Score(Confidence): %s", product.getScore()));
				System.out.println(String.format("Image name: %s", product.getImage()));
				System.out.println(String.format("Image id: %s", bean.getRef_image_id()));
				System.out.println(String.format("Product Category: %s", product.getProduct().getProductCategory()));
				List<Product.KeyValue> productLblLst = product.getProduct().getProductLabelsList();

				if (null != productLblLst) {
					for (Product.KeyValue productLbl : productLblLst) {
						System.out.println(
								String.format("Key:%s ,Value: %s", productLbl.getKey(), productLbl.getValue()));
					}
				}
			}

		}

		return resultLst;
	}

	/**
	 * For testing purpose - Search similar products to image in local file.
	 *
	 * @param projectId       - Id of the project.
	 * @param computeRegion   - Region name.
	 * @param productSetId    - Id of the product set.
	 * @param productCategory - Category of the product.
	 * @param filePath        - Local file path of the image to be searched
	 * @param filter          - Condition to be applied on the labels. Example for
	 *                        filter: (color = red OR color = blue) AND style = kids
	 *                        It will search on all products with the following
	 *                        labels: color:red AND style:kids color:blue AND
	 *                        style:kids
	 * @throws IOException - on I/O errors.
	 */
	public List<ProductSearchBean> testGetSimilarProductsFile(String projectId, String computeRegion,
			String productSetId, String productCategory, String filePath, String filter) throws IOException {

		List<ProductSearchBean> resultLst = null;

		try (ImageAnnotatorClient queryImageClient = ImageAnnotatorClient.create()) {

			// Get the full path of the product set.
			String productSetPath = ProductSearchClient.formatProductSetName(projectId, computeRegion, productSetId);

			// Read the image as a stream of bytes.
			File imgPath = new File(filePath);
			byte[] content = Files.readAllBytes(imgPath.toPath());

			// Create annotate image request along with product search feature.
			Feature featuresElement = Feature.newBuilder().setType(Feature.Type.PRODUCT_SEARCH).build();
			// The input image can be a HTTPS link or Raw image bytes.
			// Example:
			// To use HTTP link replace with below code
			// ImageSource source = ImageSource.newBuilder().setImageUri(imageUri).build();
			// Image image = Image.newBuilder().setSource(source).build();
			Image image = Image.newBuilder().setContent(ByteString.copyFrom(content)).build();
			ImageContext imageContext = ImageContext.newBuilder().setProductSearchParams(ProductSearchParams
					.newBuilder().setProductSet(productSetPath).addProductCategories(productCategory).setFilter(filter))
					.build();

			AnnotateImageRequest annotateImageRequest = AnnotateImageRequest.newBuilder().addFeatures(featuresElement)
					.setImage(image).setImageContext(imageContext).build();
			List<AnnotateImageRequest> requests = Arrays.asList(annotateImageRequest);

			// Search products similar to the image.
			BatchAnnotateImagesResponse response = queryImageClient.batchAnnotateImages(requests);

			List<ProductSearchResults.Result> similarProducts = response.getResponses(0).getProductSearchResults()
					.getResultsList();
			System.out.println("Similar Products: ");

			resultLst = new ArrayList<ProductSearchBean>();

			for (ProductSearchResults.Result product : similarProducts) {

				ProductSearchBean bean = new ProductSearchBean();

				if (null != product.getProduct().getDescription()) {
					bean.setId(product.getProduct().getDescription());
				}

				bean.setScore(product.getScore());

				//bean.setRefImgName(product.getImage());
				bean.setId(product.getProduct().getName().substring(product.getProduct().getName().lastIndexOf('/') + 1));
				bean.setRef_image_id(product.getImage().substring(product.getImage().lastIndexOf('/') + 1));
				bean.setGcp_productname(product.getProduct().getName());
				resultLst.add(bean);
				System.out.println(String.format("\nProduct id: %s", bean.getId()));
				System.out.println(String.format("\nProduct name: %s", product.getProduct().getName()));
				System.out.println(String.format("Product display name: %s", product.getProduct().getDisplayName()));
				System.out.println(String.format("Product description: %s", product.getProduct().getDescription()));
				System.out.println(String.format("Score(Confidence): %s", product.getScore()));
				System.out.println(String.format("Image name: %s", product.getImage()));
				System.out.println(String.format("Image id: %s", bean.getRef_image_id()));
				System.out.println(String.format("Product Category: %s", product.getProduct().getProductCategory()));
				List<Product.KeyValue> productLblLst = product.getProduct().getProductLabelsList();

				if (null != productLblLst) {
					for (Product.KeyValue productLbl : productLblLst) {
						System.out.println(
								String.format("Key:%s ,Value: %s", productLbl.getKey(), productLbl.getValue()));
					}
				}
			}

		}

		return resultLst;
	}

	/**
	 * Update the product labels.
	 *
	 * @param projectId        - Id of the project.
	 * @param computeRegion    - Region name.
	 * @param productId        -Id of the product.
	 * @param productLabelsLst - Labels of the product of List
	 * @throws IOException - on I/O errors.
	 */
	public void updateProductLabels(String projectId, String computeRegion, String productId,
			Iterable<Product.KeyValue> productLabelsLst) throws IOException {
		try (ProductSearchClient client = ProductSearchClient.create()) {

			// Get the full path of the product.
			String formattedName = ProductSearchClient.formatProductName(projectId, computeRegion, productId);

			// Set product name, product labels and product display name.
			// Multiple labels are also supported.
			Product product = Product.newBuilder().setName(formattedName).addAllProductLabels(productLabelsLst).build();

			// Set product update field name.
			FieldMask updateMask = FieldMask.newBuilder().addPaths("product_labels").build();

			// Update the product.
			Product updatedProduct = client.updateProduct(product, updateMask);
			// Display the product information
			System.out.println(String.format("Product name: %s", updatedProduct.getName()));
			System.out.println(String.format("Updated product labels: "));
			for (Product.KeyValue element : updatedProduct.getProductLabelsList()) {
				System.out.println(String.format("%s: %s", element.getKey(), element.getValue()));
			}

			/*
			 * Updated product labels: pid:
			 * light_2_test_7e7a330d-a861-4977-b77e-7df33241c4eb public_url:
			 * https://storage.googleapis.com/kw_fyp_storage/light_2_test.jpg
			 */
		}
	}

	/**
	 * Update the product labels.
	 *
	 * @param projectId        - Id of the project.
	 * @param computeRegion    - Region name.
	 * @param productId        -Id of the product.
	 * @param productLabelsLst - Labels of the product of List
	 * @throws IOException - on I/O errors.
	 */
	public void updateProductLabels(String projectId, String computeRegion, String productId, String desc)
			throws IOException {
		try (ProductSearchClient client = ProductSearchClient.create()) {

			// Get the full path of the product.
			String formattedName = ProductSearchClient.formatProductName(projectId, computeRegion, productId);

			// Set product name, product labels and product display name.
			// Multiple labels are also supported.
			Product product = Product.newBuilder().setName(formattedName).setDescription(desc).build();

			// Set product update field name.
			FieldMask updateMask = FieldMask.newBuilder().addPaths("description").build();

			// Update the product.
			Product updatedProduct = client.updateProduct(product, updateMask);
			// Display the product information
			System.out.println(String.format("Product name: %s", updatedProduct.getName()));
			System.out.println(String.format("Product desc: %s", updatedProduct.getDescription()));
			System.out.println(String.format("Updated product labels: "));
			for (Product.KeyValue element : updatedProduct.getProductLabelsList()) {
				System.out.println(String.format("%s: %s", element.getKey(), element.getValue()));
			}

		}
	}

	/**
	 * Detects localized objects in the specified local image.
	 *
	 * @param filePath The path to the file to perform localized object detection
	 *                 on.
	 * @throws Exception   on errors while closing the client.
	 * @throws IOException on Input/Output errors.
	 */
	public List<?> detectLocalizedObjects(String filePath) throws IOException {
		List<ProductSearchBean> resultLst = null;
		List<AnnotateImageRequest> requests = new ArrayList<>();

		//Get img byte from file path
		File f = new File(filePath);
		ByteString imgBytes = ByteString.readFrom(new FileInputStream(f));
		//ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));
		
		
		//Get img byte from input stream, not working
		//ByteString imgBytes = ByteString.readFrom(fis);

		Image img = Image.newBuilder().setContent(imgBytes).build();
		AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
				.addFeatures(Feature.newBuilder().setType(Type.OBJECT_LOCALIZATION)).setImage(img).build();
		requests.add(request);

		// Initialize client that will be used to send requests. This client only needs
		// to be created
		// once, and can be reused for multiple requests. After completing all of your
		// requests, call
		// the "close" method on the client to safely clean up any remaining background
		// resources.
		try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
			// Perform the request
			BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
			List<AnnotateImageResponse> responses = response.getResponsesList();

			resultLst = new ArrayList<ProductSearchBean>();
		
			// Display the results
			for (AnnotateImageResponse res : responses) {
				for (LocalizedObjectAnnotation entity : res.getLocalizedObjectAnnotationsList()) {
					
					ProductSearchBean bean = new ProductSearchBean();
					bean.setObject_name(entity.getName());
					bean.setScore( entity.getScore());
					
					System.out.println(String.format("name: %s, Score: %f", bean.getObject_name(), bean.getScore()));
					resultLst.add(bean);
					//System.out.format("Object name: %s%n", entity.getName());
					//System.out.format("Confidence: %s%n", entity.getScore());
					//System.out.format("Normalized Vertices:%n");
					//entity.getBoundingPoly().getNormalizedVerticesList()
					//		.forEach(vertex -> System.out.format("- (%s, %s)%n", vertex.getX(), vertex.getY()));
				}
			}
			
//			Path  rootLocation = Paths.get(configsProperties.FILE_UPLOAD_DIR);
//			Path destinationFile = Paths.get(filePath);
//			if (destinationFile.getParent().equals(rootLocation.toAbsolutePath())) {
//				Files.deleteIfExists(destinationFile);
//			}
			
			f.deleteOnExit();
			
			return resultLst;
		}
	}
	
	/**
	 * Detects localized objects in the specified local image.
	 *
	 * @param filePath The path to the file to perform localized object detection
	 *                 on.
	 * @throws Exception   on errors while closing the client.
	 * @throws IOException on Input/Output errors.
	 */
	public List<?> detectLocalizedObjects(InputStream fis) throws IOException {
		List<ProductSearchBean> resultLst = null;
		List<AnnotateImageRequest> requests = new ArrayList<>();

		//Get img byte from file path
		
		File f = File.createTempFile( UUID.randomUUID().toString(),".jpg");
		Files.copy(fis, f.toPath(), StandardCopyOption.REPLACE_EXISTING);
		System.out.println("Temp file created at location: " + f.getAbsolutePath());
		ByteString imgBytes = ByteString.readFrom(new FileInputStream(f));
		//ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));
		
		
		//Get img byte from input stream, not working
		//ByteString imgBytes = ByteString.readFrom(fis);

		Image img = Image.newBuilder().setContent(imgBytes).build();
		AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
				.addFeatures(Feature.newBuilder().setType(Type.OBJECT_LOCALIZATION)).setImage(img).build();
		requests.add(request);

		// Initialize client that will be used to send requests. This client only needs
		// to be created
		// once, and can be reused for multiple requests. After completing all of your
		// requests, call
		// the "close" method on the client to safely clean up any remaining background
		// resources.
		try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
			// Perform the request
			BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
			List<AnnotateImageResponse> responses = response.getResponsesList();

			resultLst = new ArrayList<ProductSearchBean>();
		
			// Display the results
			for (AnnotateImageResponse res : responses) {
				for (LocalizedObjectAnnotation entity : res.getLocalizedObjectAnnotationsList()) {
					
					ProductSearchBean bean = new ProductSearchBean();
					bean.setObject_name(entity.getName());
					bean.setScore( entity.getScore());
					
					System.out.println(String.format("name: %s, Score: %f", bean.getObject_name(), bean.getScore()));
					resultLst.add(bean);
					//System.out.format("Object name: %s%n", entity.getName());
					//System.out.format("Confidence: %s%n", entity.getScore());
					//System.out.format("Normalized Vertices:%n");
					//entity.getBoundingPoly().getNormalizedVerticesList()
					//		.forEach(vertex -> System.out.format("- (%s, %s)%n", vertex.getX(), vertex.getY()));
				}
			}
			
//			Path  rootLocation = Paths.get(configsProperties.FILE_UPLOAD_DIR);
//			Path destinationFile = Paths.get(filePath);
//			if (destinationFile.getParent().equals(rootLocation.toAbsolutePath())) {
//				Files.deleteIfExists(destinationFile);
//			}
			
			f.deleteOnExit();
			
			return resultLst;
		}
	}
	
	/**
	 * List all images in a product.
	 *
	 * @param projectId - Id of the project.
	 * @param computeRegion - Region name.
	 * @param productId - Id of the product.
	 * @throws IOException - on I/O errors.
	 */
	public static void listReferenceImagesOfProduct(
	    String projectId, String computeRegion, String productId) throws IOException {
	  try (ProductSearchClient client = ProductSearchClient.create()) {

	    // Get the full path of the product.
	    String formattedParent =
	        ProductSearchClient.formatProductName(projectId, computeRegion, productId);
	    for (ReferenceImage image : client.listReferenceImages(formattedParent).iterateAll()) {
	      // Display the reference image information.
	      System.out.println(String.format("Reference image name: %s", image.getName()));
	      System.out.println(
	          String.format(
	              "Reference image id: %s",
	              image.getName().substring(image.getName().lastIndexOf('/') + 1)));
	      System.out.println(String.format("Reference image uri: %s", image.getUri()));
	      System.out.println(
	          String.format(
	              "Reference image bounding polygons: %s \n",
	              image.getBoundingPolysList().toString()));
	    }
	  }
	}
	
	/**
	 * Delete a reference image.
	 *
	 * @param projectId - Id of the project.
	 * @param computeRegion - Region name.
	 * @param productId - Id of the product.
	 * @param referenceImageId - Id of the image.
	 * @throws IOException - on I/O errors.
	 */
	public static void deleteReferenceImage(
	    String projectId, String computeRegion, String productId, String referenceImageId)
	    throws IOException {
	  try (ProductSearchClient client = ProductSearchClient.create()) {

	    // Get the full path of the reference image.
	    String formattedName =
	        ImageName.format(projectId, computeRegion, productId, referenceImageId);
	    // Delete the reference image.
	    client.deleteReferenceImage(formattedName);
	    System.out.println("Reference image deleted from product.");
	  }
	}
	
}
