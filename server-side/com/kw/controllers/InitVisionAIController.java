package com.kw.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.cloud.vision.v1.Product;
import com.kw.beans.ProductSearchBean;
import com.kw.beans.ProductSearchResultBean;
import com.kw.constants.Constant;
import com.kw.services.StorageServiceInterface;
import com.kw.services.impl.CloudStorageServices;
import com.kw.services.impl.ProductsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kw.services.impl.VisionAIServices;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Scope("session")
public class InitVisionAIController {
	@Autowired
	VisionAIServices visionService;

	@Autowired
	CloudStorageServices cloudStorageService;

	@Autowired
	StorageServiceInterface storageServiceInterface;

	@Autowired
	ProductsService productService;
	/*
		Already executed
	 */
	//@GetMapping("/v1/createProductSet")
	@ResponseBody
	public String CreateProductSet(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
//		System.out.println("GOOGLE_APPLICATION_CREDENTIALS = " + System.getenv("GOOGLE_APPLICATION_CREDENTIALS"));
//		System.out.println("GOOGLE_APPLICATION_CREDENTIALS = " + System.getProperty("GOOGLE_APPLICATION_CREDENTIALS"));
		System.out.println(Constant.GCP_Region_Name.ASIA_EAST1.getValue());
		try {
			visionService.createProductSet(Constant.projectId, Constant.GCP_Region_Name.ASIA_EAST1.getValue(), Constant.productSetId,Constant.projectSetDisplayName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		 * 
		 * Forwarding to error page from request [/v1/createProductSet] due to exception [io.grpc.StatusRuntimeException: ALREADY_EXISTS: ProductSet with ID 'Prj_Jason_FYP_Product_Set' already exists.]
		 */
		return "Create Product Set Successfully";
	}

	@GetMapping("/v1/testCreateProduct")
	@ResponseBody
	public String TestCreateProduct(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String productId = String.format("light_2_test_%s","7e7a330d-a861-4977-b77e-7df33241c4eb");
		String productDisplayName = "light_2_test";
		String desc = productId;
		//String productCat = "Light Bulbs";


		try {
			visionService.createProduct(Constant.projectId,Constant.GCP_Region_Name.ASIA_EAST1.getValue(), productId, productDisplayName,Constant.ProductCategory.HOME_GOODS_V2.getValue(),desc,null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		Product Id: projects/seismic-aloe-340714/locations/asia-east1/products/light_2_test_7e7a330d-a861-4977-b77e-7df33241c4eb
		Product Display Name: light_2_test
		Product Category: homegoods-v2
		 */
		return "Create Product Successfully";
	}

	@GetMapping("/v1/testAddProduct2ProductSet")
	@ResponseBody
	public String TestAddProduct2ProductSet(HttpSession session, HttpServletRequest request, HttpServletResponse response) {

		String productId = String.format("light_2_test_%s","7e7a330d-a861-4977-b77e-7df33241c4eb");
		String productDisplayName = "light_2_test";
		//String productCat = "Light Bulbs";


		try {
			visionService.addProductToProductSet(Constant.projectId,Constant.GCP_Region_Name.ASIA_EAST1.getValue(), productId,Constant.productSetId);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		Product Id: projects/seismic-aloe-340714/locations/asia-east1/products/light_2_test_7e7a330d-a861-4977-b77e-7df33241c4eb
		Product Display Name: light_2_test
		Product Category: homegoods-v2
		 */
		return "Added product to the product set Successfully!!";
	}

	@GetMapping("/v1/testCreateReferenceImage")
	@ResponseBody
	public String TestCreateReferenceImage(HttpSession session, HttpServletRequest request, HttpServletResponse response) {

		String productId = String.format("light_2_test_%s","7e7a330d-a861-4977-b77e-7df33241c4eb");
		String productDisplayName = "light_2_test";

		String image_referenceId = String.format("image_%s","36f58047-902e-473f-b812-4a3c23253f17");
		//Google Cloud Storage image gs url
		String image_gsUri = "gs://kw_fyp_storage/light_2_test.jpg";

		// url: https://storage.cloud.google.com/kw_fyp_storage/light_2_test.jpg
		try {
			visionService.createReferenceImage(Constant.projectId,Constant.GCP_Region_Name.ASIA_EAST1.getValue(), productId,image_referenceId,image_gsUri);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		Reference image id: image_36f58047-902e-473f-b812-4a3c23253f17
		Reference image name: projects/seismic-aloe-340714/locations/asia-east1/products/light_2_test_7e7a330d-a861-4977-b77e-7df33241c4eb/referenceImages/image_36f58047-902e-473f-b812-4a3c23253f17
		Reference image uri: gs://kw_fyp_storage/light_2_test.jpg
		 */
		return "Create Reference Image Successfully!!";
	}

	@GetMapping("/v1/testUpdateProductLabels")
	@ResponseBody
	public String TestUpdateProductLabels(HttpSession session, HttpServletRequest request, HttpServletResponse response) {

		//String productId = String.format("light_2_test_%s","7e7a330d-a861-4977-b77e-7df33241c4eb");
		String productId = String.format("light_sample_1.jpg_%s", "69fa8943-ae81-4464-a401-2c1c0b22c8fd");
		String productDisplayName = "light_2_test";

		String image_referenceId = String.format("image_%s","36f58047-902e-473f-b812-4a3c23253f17");
		//Google Cloud Storage image gs url
		String image_gsUri = "gs://kw_fyp_storage/light_2_test.jpg";

		// url: https://storage.cloud.google.com/kw_fyp_storage/light_2_test.jpg

		//public url
		//https://storage.googleapis.com/kw_fyp_storage/light_2_test.jpg
		String img_public_url = "https://storage.googleapis.com/kw_fyp_storage/light_2_test.jpg";
		List<Product.KeyValue> productLabelsLst = new ArrayList<Product.KeyValue>();

		Product.KeyValue productLbl = Product.KeyValue.newBuilder()
				.setKey("pid")
				.setValue(productId)
				.build();

		productLabelsLst.add(productLbl);

		productLbl = Product.KeyValue.newBuilder()
				.setKey("public_url")
				.setValue(img_public_url)
				.build();

		productLabelsLst.add(productLbl);

		String desc = "69fa8943-ae81-4464-a401-2c1c0b22c8fd";
		try {
			//visionService.updateProductLabels(Constant.projectId,Constant.GCP_Region_Name.ASIA_EAST1.getValue(), productId,productLabelsLst);
			
			visionService.updateProductLabels(Constant.projectId,Constant.GCP_Region_Name.ASIA_EAST1.getValue(), productId,desc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*			Updated product labels:
			pid: light_2_test_7e7a330d-a861-4977-b77e-7df33241c4eb
			public_url: https://storage.googleapis.com/kw_fyp_storage/light_2_test.jpg
			*/

		return "Create Reference Image Successfully!!";
	}

	@GetMapping("/v1/testGetSimilarProductsFile")
	@ResponseBody
	public List<?> TestGetSimilarProductsFile(HttpSession session, HttpServletRequest request, HttpServletResponse response) {

		String productId = String.format("light_2_test_%s","7e7a330d-a861-4977-b77e-7df33241c4eb");
		String productDisplayName = "light_2_test";

		String file_path_1 = "C:\\Users\\Yui_KK\\Downloads\\light_2_search_test_1.JPG";
		String file_path_2 = "C:\\Users\\Yui_KK\\Downloads\\light_2_search_test_2.JPG";
		String file_path_3 = "C:\\Users\\Yui_KK\\Downloads\\square_led_light_search_test.jpg";
		List<?> resultLst = null;
		List<ProductSearchBean> searchResultsLst = null;
		try {
			resultLst = visionService.testGetSimilarProductsFile(Constant.projectId,Constant.GCP_Region_Name.ASIA_EAST1.getValue(), Constant.productSetId,Constant.ProductCategory.HOME_GOODS_V2.getValue(), file_path_1,"");
			searchResultsLst = productService.searchProductByPhoto((List<ProductSearchBean>) resultLst);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		Similar Products:

			Product name: projects/seismic-aloe-340714/locations/asia-east1/products/6f1fb0ba-fca5-4086-a783-368395343134
			Product display name: square_led_light.jpg
			Product description: 6f1fb0ba-fca5-4086-a783-368395343134
			Score(Confidence): 1.0
			Image name: projects/seismic-aloe-340714/locations/asia-east1/products/6f1fb0ba-fca5-4086-a783-368395343134/referenceImages/image_6f1fb0ba-fca5-4086-a783-368395343134
			Product Category: homegoods-v2
			
			Product name: projects/seismic-aloe-340714/locations/asia-east1/products/light_2_test_7e7a330d-a861-4977-b77e-7df33241c4eb
			Product display name: light_2_test
			Product description: 
			Score(Confidence): 0.2189811
			Image name: projects/seismic-aloe-340714/locations/asia-east1/products/light_2_test_7e7a330d-a861-4977-b77e-7df33241c4eb/referenceImages/image_36f58047-902e-473f-b812-4a3c23253f17
			Product Category: homegoods-v2
			Key:pid ,Value: light_2_test_7e7a330d-a861-4977-b77e-7df33241c4eb
			Key:public_url ,Value: https://storage.googleapis.com/kw_fyp_storage/light_2_test.jpg
			
			Product name: projects/seismic-aloe-340714/locations/asia-east1/products/light_sample_1.jpg_69fa8943-ae81-4464-a401-2c1c0b22c8fd
			Product display name: light_sample_1.jpg
			Product description: 
			Score(Confidence): 0.21155518
			Image name: projects/seismic-aloe-340714/locations/asia-east1/products/light_sample_1.jpg_69fa8943-ae81-4464-a401-2c1c0b22c8fd/referenceImages/image_light_sample_1.jpg_69fa8943-ae81-4464-a401-2c1c0b22c8fd
			Product Category: homegoods-v2
			Key:act_filename ,Value: light_sample_1.jpg

		*/
		return searchResultsLst;
	}


	@PostMapping("/v1/testUploadImg2CloudStorage")
	public String handleFileUpload(@RequestParam("file") MultipartFile file,
								   RedirectAttributes redirectAttributes) {



		try {
			InputStream fis = file.getInputStream();


			//storageServiceInterface.store(file);
			cloudStorageService.uploadPhotoObject(Constant.projectId,Constant.cloud_storage_bucket_name,file.getOriginalFilename(),fis);
			System.out.println("ttt");


		}catch (Exception ex){
			ex.printStackTrace();
		}
//		for(MultipartFile fileItem : file){
//			storageServiceInterface.store(fileItem);
//		}

		redirectAttributes.addFlashAttribute("message",
				"You successfully uploaded " + String.format("%s\r\n%s\r\n",file.getOriginalFilename(),file.getOriginalFilename()) + "!");

		return "redirect:/";
	}


	@GetMapping("/v1/testVisionAiAllinOneAPI")
	@ResponseBody
	public String testVisionAiAllinOneAPI(HttpSession session, HttpServletRequest request, HttpServletResponse response) {

		String productId = String.format("light_sample_1.jpg_%s","69fa8943-ae81-4464-a401-2c1c0b22c8fd");
		String productDisplayName = "light_sample_1.jpg";
		String actFileName = "light_sample_1.jpg";

		//String file_path_1 = "C:\\Users\\Yui_KK\\Downloads\\light_2_search_test_1.JPG";
		//String file_path_2 = "C:\\Users\\Yui_KK\\Downloads\\light_2_search_test_2.JPG";

		String image_referenceId = String.format("image_%s",productId);
		//Google Cloud Storage image gs url

		//e.g. gs://kw_fyp_storage/light_2_test.jpg
		String image_gsUri = String.format("%s%s/%s",Constant.cloud_storage_gsurl_prefix,Constant.cloud_storage_bucket_name,actFileName);

		//https://storage.googleapis.com/kw_fyp_storage/light_2_test.jpg
		List<Product.KeyValue> productLabelsLst = new ArrayList<Product.KeyValue>();

		Product.KeyValue productLbl = Product.KeyValue.newBuilder()
				.setKey("act_filename")
				.setValue(actFileName)
				.build();

		productLabelsLst.add(productLbl);

		boolean is_created_product_success , is_added_2_productSet_success, is_created_ref_image_success;
		is_created_product_success = is_added_2_productSet_success = is_created_ref_image_success = false;
		
		String desc = productId;
//		boolean is_added_2_productSet_success;
//		boolean is_created_ref_image_success;

		try {
			//0. Upload image to cloud storage

			//1. create product
			visionService.createProduct(Constant.projectId,Constant.GCP_Region_Name.ASIA_EAST1.getValue(), productId, productDisplayName,Constant.ProductCategory.HOME_GOODS_V2.getValue(),desc,productLabelsLst);
			is_created_product_success = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			//2. Add a Product to a Product Set
			visionService.addProductToProductSet(Constant.projectId,Constant.GCP_Region_Name.ASIA_EAST1.getValue(), productId,Constant.productSetId);
			is_added_2_productSet_success = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			//3. Create a Product's Reference Image
			visionService.createReferenceImage(Constant.projectId,Constant.GCP_Region_Name.ASIA_EAST1.getValue(), productId,image_referenceId,image_gsUri);
			is_created_ref_image_success = true;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Create Product Success = " + is_created_product_success);
		System.out.println("Add 2 ProductSet Success = " + is_added_2_productSet_success);
		System.out.println("Create Ref Image Success = " + is_created_ref_image_success);
		return "Search image results!!";

		/*
		path=D:\java_workspace\KW_FYP\target\ upload-dir\
		Product Id: projects/seismic-aloe-340714/locations/asia-east1/products/light_sample_1.jpg_69fa8943-ae81-4464-a401-2c1c0b22c8fd
		Product Display Name: light_sample_1.jpg
		Product Category: homegoods-v2
		Product added to product set.
		Reference image id: image_light_sample_1.jpg_69fa8943-ae81-4464-a401-2c1c0b22c8fd
		Reference image name: projects/seismic-aloe-340714/locations/asia-east1/products/light_sample_1.jpg_69fa8943-ae81-4464-a401-2c1c0b22c8fd/referenceImages/image_light_sample_1.jpg_69fa8943-ae81-4464-a401-2c1c0b22c8fd
		Reference image uri: gs://kw_fyp_storage/light_sample_1.jpg
		Create Product Success = true
		Add 2 ProductSet Success = true
		Create Ref Image Success = true

		 */
	}
	
	
	@GetMapping("/v1/testDetectImgObj")
	@ResponseBody
	public List<?> TestDetectImageObjet(HttpSession session, HttpServletRequest request, HttpServletResponse response) {

		String productId = String.format("light_2_test_%s","7e7a330d-a861-4977-b77e-7df33241c4eb");
		String productDisplayName = "light_2_test";

		String file_path_1 = "C:\\Users\\Yui_KK\\Downloads\\light_sample_1.jpg";
		String file_path_2 = "C:\\Users\\Yui_KK\\Downloads\\light_2_test.jpg";
		String file_path_3 = "C:\\Users\\Yui_KK\\Downloads\\square_led_light.jpg";
		String file_path_4 = "C:\\Users\\Yui_KK\\Downloads\\a49645ba-a026-457c-b9f7-b4c9fa1a86acLego_71040_2.jpg";
		List<?> resultLst = null;
		try {
			resultLst = visionService.detectLocalizedObjects(file_path_4);
			//resultLst = visionService.detectLocalizedObjects(file_path_1);
			//searchResultsLst = productService.searchProductByPhoto((List<ProductSearchBean>) resultLst);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*
		 * light_sample_1.jpg
		 * Object name: Lighting
			Confidence: 0.92330253
			Normalized Vertices:
			- (0.2320084, 0.12553783)
			- (0.7708972, 0.12553783)
			- (0.7708972, 0.39630806)
			- (0.2320084, 0.39630806)
			Object name: Window blind
			Confidence: 0.6699689
			Normalized Vertices:
			- (9.742126E-4, 0.4547857)
			- (0.17311229, 0.4547857)
			- (0.17311229, 0.69144464)
			- (9.742126E-4, 0.69144464)
			
			light_2_test.jpg
			Object name: Lighting
			Confidence: 0.9098411
			Normalized Vertices:
			- (0.12784551, 0.13309672)
			- (0.87169605, 0.13309672)
			- (0.87169605, 0.5600543)
			- (0.12784551, 0.5600543)
			
			
			square_led_light.jpg
			Object name: Lighting
			Confidence: 0.9349861
			Normalized Vertices:
			- (0.12730354, 0.069043316)
			- (0.88605756, 0.069043316)
			- (0.88605756, 0.83242774)
			- (0.12730354, 0.83242774)

		 */
		
		return resultLst;
	}
	
	
	@GetMapping("/v3/testSuggestList")
	@ResponseBody
	public Object testSuggestList(HttpSession session, HttpServletRequest request, HttpServletResponse response) {

		String productId = String.format("light_2_test_%s","7e7a330d-a861-4977-b77e-7df33241c4eb");
		String productDisplayName = "light_2_test";

		String file_path_1 = "C:\\Users\\Yui_KK\\Downloads\\light_2_search_test_1.JPG";
		String file_path_2 = "C:\\Users\\Yui_KK\\Downloads\\light_2_search_test_2.JPG";
		String file_path_3 = "C:\\Users\\Yui_KK\\Downloads\\square_led_light_search_test.jpg";
		//String file_path_4 = "C:\\Users\\Yui_KK\\Downloads\\test_suggest_list_led.jpg";
		//String file_path_5 = "C:\\Users\\Yui_KK\\Downloads\\tomica-pokemon-quest-series-1-pic-1.jpg";
		
		
		List<?> resultLst = null;
		List<ProductSearchBean> detectedObjLst = null;
		List<?> suggestedLst = null;
		List<ProductSearchBean> searchResultsLst = null;
		ProductSearchResultBean resultsBean = new  ProductSearchResultBean();
		try {
			resultLst = visionService.testGetSimilarProductsFile(Constant.projectId,Constant.GCP_Region_Name.ASIA_EAST1.getValue(), Constant.productSetId,Constant.ProductCategory.HOME_GOODS_V2.getValue(), file_path_1,"");
			System.out.println("resultLst = " + resultLst);
			detectedObjLst = (List<ProductSearchBean>) visionService.detectLocalizedObjects(file_path_1);
			
			searchResultsLst = productService.searchProductByPhoto((List<ProductSearchBean>) resultLst);
			
			System.out.println("searchResultsLst = " + searchResultsLst);
			suggestedLst = productService.findSuggestedProductByPhoto(searchResultsLst.stream().map(obj -> obj.getId()).collect(Collectors.toList()), detectedObjLst.stream().map(o -> o.getObject_name()).collect(Collectors.toList()));
			System.out.println("suggestedLst = " + suggestedLst);
			resultsBean.setSearch_results(searchResultsLst);
			resultsBean.setSuggested_results(suggestedLst);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return resultsBean;
	}
	
	@GetMapping("/v3/testListReferenceImage")
	@ResponseBody
	public Object testListReferenceImage(HttpSession session, HttpServletRequest request, HttpServletResponse response) {

		String productId1 = String.format("6f1fb0ba-fca5-4086-a783-368395343134");
		String productId2 = String.format("light_2_test_%s","7e7a330d-a861-4977-b77e-7df33241c4eb");
		String productId3 = String.format("light_sample_1.jpg_%s","69fa8943-ae81-4464-a401-2c1c0b22c8fd");
		String productDisplayName = "light_2_test";
		
		try {
			System.out.println("product id 1= " + productId1);
			visionService.listReferenceImagesOfProduct(Constant.projectId, Constant.GCP_Region_Name.ASIA_EAST1.getValue(), productId1);
			System.out.println("product id 2= " + productId2);
			visionService.listReferenceImagesOfProduct(Constant.projectId, Constant.GCP_Region_Name.ASIA_EAST1.getValue(), productId2);
			System.out.println("product id 3= " + productId3);
			visionService.listReferenceImagesOfProduct(Constant.projectId, Constant.GCP_Region_Name.ASIA_EAST1.getValue(), productId3);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		/*
		Similar Products:

			Product name: projects/seismic-aloe-340714/locations/asia-east1/products/6f1fb0ba-fca5-4086-a783-368395343134
			Product display name: square_led_light.jpg
			Product description: 6f1fb0ba-fca5-4086-a783-368395343134
			Score(Confidence): 1.0
			Image name: projects/seismic-aloe-340714/locations/asia-east1/products/6f1fb0ba-fca5-4086-a783-368395343134/referenceImages/image_6f1fb0ba-fca5-4086-a783-368395343134
			Product Category: homegoods-v2
			
			Product name: projects/seismic-aloe-340714/locations/asia-east1/products/light_2_test_7e7a330d-a861-4977-b77e-7df33241c4eb
			Product display name: light_2_test
			Product description: 
			Score(Confidence): 0.2189811
			Image name: projects/seismic-aloe-340714/locations/asia-east1/products/light_2_test_7e7a330d-a861-4977-b77e-7df33241c4eb/referenceImages/image_36f58047-902e-473f-b812-4a3c23253f17
			Product Category: homegoods-v2
			Key:pid ,Value: light_2_test_7e7a330d-a861-4977-b77e-7df33241c4eb
			Key:public_url ,Value: https://storage.googleapis.com/kw_fyp_storage/light_2_test.jpg
			
			Product name: projects/seismic-aloe-340714/locations/asia-east1/products/light_sample_1.jpg_69fa8943-ae81-4464-a401-2c1c0b22c8fd
			Product display name: light_sample_1.jpg
			Product description: 
			Score(Confidence): 0.21155518
			Image name: projects/seismic-aloe-340714/locations/asia-east1/products/light_sample_1.jpg_69fa8943-ae81-4464-a401-2c1c0b22c8fd/referenceImages/image_light_sample_1.jpg_69fa8943-ae81-4464-a401-2c1c0b22c8fd
			Product Category: homegoods-v2
			Key:act_filename ,Value: light_sample_1.jpg

		*/
		return null;
	}
}
