package com.kw.services.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.kw.CategorySingleton;
import com.kw.beans.CategoryBean;
import com.kw.beans.CloudStorageBean;
import com.kw.beans.ProductImageBean;
import com.kw.beans.ProductSearchBean;
import com.kw.constants.Constant;
import com.kw.firebase.FirebaseService;
import com.kw.models.CategoryEntity;
import com.kw.models.ProductEntity;
import com.kw.services.StorageServiceInterface;

//@Component
@Service
public class ProductsService {

	@Autowired
	FirebaseService firebaseService;

	@Autowired
	VisionAIServices visionService;

	@Autowired
	CloudStorageServices cloudStorageService;
	
	@Autowired
	CategorySingleton categorySingleton;
	
	 @Autowired
	 StorageServiceInterface storageServiceInterface;
	
	public boolean saveProduct(ProductImageBean productBean) {
		Boolean result = false;
		ProductEntity productEntity = new ProductEntity();
		String uuid = UUID.randomUUID().toString();
		productEntity.setId(uuid);
		productEntity.setProductDesc(productBean.getProduct_desc());
		productEntity.setProductName(productBean.getProduct_name());
		productEntity.setMarked_price(productBean.getMarked_price());
		productEntity.setSelling_price(productBean.getSelling_price());
		productEntity.setSold(0);
		productEntity.setStock(productBean.getStocks());
		productEntity.setCategory_id(productBean.getCategory_id());
		productEntity.setUpdatedDt(new Date());
		productEntity.setCreatedDt(new Date());

		String ori_filename = productBean.getFile().getOriginalFilename();

		String filename = String.format("%s_%s", uuid, ori_filename);
		try {
			// 0. Upload image to cloud storage

			CloudStorageBean storageBean = cloudStorageService.uploadPhotoObject(Constant.projectId,
					Constant.cloud_storage_bucket_name, filename, productBean.getFis());

			productEntity.setProductThumb(storageBean.getPub_url());
			productEntity.setGsutil_uri(storageBean.getGsutil_uri());
			productEntity.setIs_upload2_cloud_storage(true);
			System.out.println("Upload photo to cloud storage success = " + true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String productDisplayName = ori_filename;
		String productId = uuid;
		String desc = productId;
		try {

			// 1. create product
			visionService.createProduct(Constant.projectId, Constant.GCP_Region_Name.ASIA_EAST1.getValue(), productId,
					productDisplayName, Constant.ProductCategory.HOME_GOODS_V2.getValue(), desc, null);
			productEntity.setIs_created_product(true);
			System.out.println("Create Product Success = " + true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			// 2. Add a Product to a Product Set
			visionService.addProductToProductSet(Constant.projectId, Constant.GCP_Region_Name.ASIA_EAST1.getValue(),
					productId, Constant.productSetId);
			productEntity.setIs_added_2_productset(true);
			System.out.println("Add 2 ProductSet Success = " + true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String image_referenceId = String.format("image_%s", productId);
		String image_gsUri = productEntity.getGsutil_uri();
		try {
			// 3. Create a Product's Reference Image
			visionService.createReferenceImage(Constant.projectId, Constant.GCP_Region_Name.ASIA_EAST1.getValue(),
					productId, image_referenceId, image_gsUri);
			productEntity.setIs_created_refer_img(true);
			productEntity.setRef_image_id(image_referenceId);
			System.out.println("Create Ref Image Success = " + true);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	
		try {
			//detect image object
			List<ProductSearchBean> objectLst =  (List<ProductSearchBean>) visionService.detectLocalizedObjects(productBean.getFilePathStr());
			List<String> objInfoLst = new ArrayList<String>();
			for(ProductSearchBean bean : objectLst) {
				
				if(bean.getScore() >= Constant.Img_detect_object_confidence_threshold) {
					objInfoLst.add(bean.getObject_name());
					objInfoLst.add(bean.getScore().toString());
				}
			}
			
			productEntity.setDetectedObject(objInfoLst);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result = firebaseService.addProduct(productEntity);

		return result;
	}
	
	public boolean updateProduct(ProductImageBean productBean) {
		Boolean result = false;
		ProductSearchBean serverRecord = this.getProductById(productBean.getId());
		ProductEntity productEntity = new ProductEntity();
		
		//compare different between server record and client side bean
		if(!serverRecord.getProductDesc().equals(productBean.getProduct_desc())) {
			productEntity.setProductDesc(productBean.getProduct_desc());
		}
		
		if(!serverRecord.getProductName().equals(productBean.getProduct_name())) {
			productEntity.setProductName(productBean.getProduct_name());
		}
		
		if(serverRecord.getMarked_price() != productBean.getMarked_price()) {
			productEntity.setMarked_price(productBean.getMarked_price());
		}
		
		if(serverRecord.getSelling_price() != productBean.getSelling_price()) {
			productEntity.setSelling_price(productBean.getSelling_price());
		}
		
		if(serverRecord.getStock() != productBean.getStocks()) {
			productEntity.setStock(productBean.getStocks());
		}
		
		if(!serverRecord.getCategory_id().equals(productBean.getCategory_id())) {
			productEntity.setCategory_id(productBean.getCategory_id());
		}
		
		productEntity.setId(productBean.getId());
		
		
		if(!serverRecord.getGsutil_uri().equals(productBean.getFile_id())){
			//if the image changed, delete the old object & upload it to cloud storage
			
			try {
				//0. delete the old object 
				String OldObjName = serverRecord.getGsutil_uri().substring(serverRecord.getGsutil_uri().lastIndexOf('/') + 1);
				cloudStorageService.deleteObject(Constant.projectId, Constant.cloud_storage_bucket_name, OldObjName);
			
			}catch(Exception ex) {
				
			}
			
			
			// upload the new to cloud storage
			String ori_filename = productBean.getFile().getOriginalFilename();

			String filename = String.format("%s_%s", serverRecord.getId(), ori_filename);
			try {
				//1. Upload image to cloud storage

				CloudStorageBean storageBean = cloudStorageService.uploadPhotoObject(Constant.projectId,
						Constant.cloud_storage_bucket_name, filename, productBean.getFis());

				productEntity.setProductThumb(storageBean.getPub_url());
				productEntity.setGsutil_uri(storageBean.getGsutil_uri());
				productEntity.setIs_upload2_cloud_storage(true);
				System.out.println("Upload photo to cloud storage success = " + true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//2. delete image reference
			try {
				visionService.deleteReferenceImage(Constant.projectId, Constant.GCP_Region_Name.ASIA_EAST1.getValue(), serverRecord.getId(), serverRecord.getRef_image_id());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			String productId = productEntity.getId();
			String image_referenceId = String.format("image_%s", productId);
			String image_gsUri = productEntity.getGsutil_uri();
			try {
				// 3. Create a Product's Reference Image
				visionService.createReferenceImage(Constant.projectId, Constant.GCP_Region_Name.ASIA_EAST1.getValue(),
						productId, image_referenceId, image_gsUri);
				productEntity.setIs_created_refer_img(true);
				productEntity.setRef_image_id(image_referenceId);
				System.out.println("Create Ref Image Success = " + true);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
        	String filePath = storageServiceInterface.store(productBean.getFile());
        	productBean.setFilePathStr(filePath);
        	
			try {
				//detect image object
				List<ProductSearchBean> objectLst =  (List<ProductSearchBean>) visionService.detectLocalizedObjects(productBean.getFilePathStr());
				List<String> objInfoLst = new ArrayList<String>();
				for(ProductSearchBean bean : objectLst) {
					
					if(bean.getScore() >= Constant.Img_detect_object_confidence_threshold) {
						objInfoLst.add(bean.getObject_name());
						objInfoLst.add(bean.getScore().toString());
					}
				}
				
				productEntity.setDetectedObject(objInfoLst);
				System.out.println("objInfoLst = " + objInfoLst);
				System.out.println("Detected Object Success = " + true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}

		result = firebaseService.updateProduct(productEntity);

		return result;
	}

	public List<ProductSearchBean> searchProductByPhoto(List<ProductSearchBean> visionResultLst) {
		List<ProductEntity> resultLst = new ArrayList<ProductEntity>();
		Map<String,ProductSearchBean> idsMap = new HashMap<String,ProductSearchBean>();
		  for (ProductSearchBean bean : visionResultLst) {
		  
			  if(bean.getScore() > Constant.Photo_search_confidence_threshold) {
				  idsMap.put(bean.getId(), bean);
			  } 
		  }
		 
		  // 3. convert HashMap Keys to ArrayList
	        List<String> idsLst = idsMap.keySet().stream().collect(Collectors.toCollection(ArrayList::new));
	        if(idsLst.size() > 0 ) {
	        	resultLst = firebaseService.findProductByIds(idsLst);
	        }
	        Map<String, CategoryBean> catMap = categorySingleton.getCatMap();
	        for(ProductEntity entity : resultLst) {
	        	ProductSearchBean bean =  idsMap.get(entity.getId());
	        	bean.setProductThumb(entity.getProductThumb());
	        	bean.setMarked_price(entity.getMarked_price());
	        	bean.setSelling_price(entity.getSelling_price());
	        	bean.setProductDesc(entity.getProductDesc());
	        	bean.setProductName(entity.getProductName());
	        	bean.setSold(entity.getSold());
	        	bean.setStock(entity.getStock());
	        	bean.setCreatedDt(entity.getCreatedDt());
	        	bean.setUpdatedDt(entity.getUpdatedDt());
	        	bean.setCategory_id(entity.getCategory_id());
	        	bean.setCategory_name(catMap.get(bean.getCategory_id()).getCategory_name());
	        	
	        }
	        
	      return idsMap.values().stream().collect(Collectors.toCollection(ArrayList::new));
	}
	
	public ProductSearchBean getProductById(String id) {


		 

		
        ProductEntity entity = firebaseService.findProductById(id);
        Map<String, CategoryBean> catMap = categorySingleton.getCatMap();
        

        	ProductSearchBean bean = null;
        	if(null != entity) {
	        	bean = new ProductSearchBean();
	        	bean.setId(entity.getId());
	        	bean.setProductThumb(entity.getProductThumb());
	        	bean.setMarked_price(entity.getMarked_price());
	        	bean.setSelling_price(entity.getSelling_price());
	        	bean.setProductDesc(entity.getProductDesc());
	        	bean.setProductName(entity.getProductName());
	        	bean.setSold(entity.getSold());
	        	bean.setStock(entity.getStock());
	        	bean.setCreatedDt(entity.getCreatedDt());
	        	bean.setUpdatedDt(entity.getUpdatedDt());
	        	bean.setCategory_id(entity.getCategory_id());
	        	bean.setCategory_name(catMap.get(bean.getCategory_id()).getCategory_name());
	        	bean.setGsutil_uri(entity.getGsutil_uri());
	        	bean.setRef_image_id(entity.getRef_image_id());

        	}
        
      return bean;
}
	
	public List<ProductSearchBean> getAllProducts() {


		 
			List<ProductSearchBean> beanLst = new ArrayList<ProductSearchBean>();
			
	        List<ProductEntity> resultLst = firebaseService.findAllProducts();
	        Map<String, CategoryBean> catMap = categorySingleton.getCatMap();
	        
	        for(ProductEntity entity : resultLst) {
	        	ProductSearchBean bean = new ProductSearchBean();
	        	bean.setId(entity.getId());
	        	bean.setProductThumb(entity.getProductThumb());
	        	bean.setMarked_price(entity.getMarked_price());
	        	bean.setSelling_price(entity.getSelling_price());
	        	bean.setProductDesc(entity.getProductDesc());
	        	bean.setProductName(entity.getProductName());
	        	bean.setSold(entity.getSold());
	        	bean.setStock(entity.getStock());
	        	bean.setCreatedDt(entity.getCreatedDt());
	        	bean.setUpdatedDt(entity.getUpdatedDt());
	        	bean.setCategory_id(entity.getCategory_id());
	        	bean.setCategory_name(catMap.get(bean.getCategory_id()).getCategory_name());
	        	bean.setGsutil_uri(entity.getGsutil_uri());
	        	bean.setRef_image_id(entity.getRef_image_id());
	        	beanLst.add(bean);
	        }
	        
	      return beanLst;
	}
	
	public List<CategoryBean> getAllCategory(){
		
		List<CategoryBean> beanLst = new ArrayList<CategoryBean>();
		
        List<CategoryEntity> resultLst = firebaseService.findAllCategory();
        for(CategoryEntity entity : resultLst) {
        	CategoryBean bean = new CategoryBean();
        	bean.setId(entity.getId());
        	bean.setCategory_name(entity.getCategory_name());
        	bean.setCreatedDt(entity.getCreatedDt());
        	bean.setIsActive(entity.getActv_ind());
        	beanLst.add(bean);
        }
        
        System.out.println(beanLst.size());
      return beanLst;
		
	}
	
	public List<ProductSearchBean> findSuggestedProductByPhoto(List<String> idsLst, List<String> keywordsLst) {


		 
			List<ProductSearchBean> beanLst = new ArrayList<ProductSearchBean>();
	        List<ProductEntity> resultLst = firebaseService.findSuggestedProducts(idsLst,keywordsLst);
	        //System.out.println("resultLst = " + resultLst.size());
	        Map<String, CategoryBean> catMap = categorySingleton.getCatMap();
	        for(ProductEntity entity : resultLst) {
	        	ProductSearchBean bean =  new ProductSearchBean();
	        	bean.setId(entity.getId());
	        	bean.setProductThumb(entity.getProductThumb());
	        	bean.setMarked_price(entity.getMarked_price());
	        	bean.setSelling_price(entity.getSelling_price());
	        	bean.setProductDesc(entity.getProductDesc());
	        	bean.setProductName(entity.getProductName());
	        	bean.setSold(entity.getSold());
	        	bean.setStock(entity.getStock());
	        	bean.setCreatedDt(entity.getCreatedDt());
	        	bean.setUpdatedDt(entity.getUpdatedDt());
	        	bean.setCategory_id(entity.getCategory_id());
	        	bean.setCategory_name(catMap.get(bean.getCategory_id()).getCategory_name());
	        	bean.setObjLst(entity.getDetectedObject());
	        	beanLst.add(bean);
	        }
	        
	      return beanLst;
	}
}
