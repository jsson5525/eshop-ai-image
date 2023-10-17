package com.kw.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kw.CategorySingleton;
import com.kw.beans.CategoryBean;
import com.kw.beans.DatatableBean;
import com.kw.beans.ProductImageBean;
import com.kw.beans.ProductSearchBean;
import com.kw.beans.ResponseBean;
import com.kw.constants.Constant;
import com.kw.services.StorageServiceInterface;
import com.kw.services.impl.ProductsService;
import com.kw.utils.JsonUtils;

@Controller
public class ProductsController {
	 @Autowired
	 StorageServiceInterface storageServiceInterface;
	 
	@Autowired
	ProductsService productService;
	
	@Autowired
	CategorySingleton categorySingleton;
	
    @GetMapping("/newProduct")
    public String loginPage(Model model) {
        model.addAttribute(Constant.Model_Key_Menu_Light_Id,Constant.Menu_Product);
        model.addAttribute(Constant.Menu_Active_Id,Constant.Menu_Product_Act);
        model.addAttribute(Constant.Model_Key_Submenu_Light_Id,Constant.Submenu_New_Product);
        
        List<CategoryBean> catLst = categorySingleton.getCatLst();
        model.addAttribute("catLst",JsonUtils.objectToJson(catLst));
        return "products/new_product";
    }
    
    @GetMapping("/productList")
    public String go2ProductList(Model model) throws JsonProcessingException {
        model.addAttribute(Constant.Model_Key_Menu_Light_Id,Constant.Menu_Product);
        model.addAttribute(Constant.Menu_Active_Id,Constant.Menu_Product_Act);
        model.addAttribute(Constant.Model_Key_Submenu_Light_Id,Constant.Submenu_Product_List);
        
        List<ProductSearchBean> beanLst =  productService.getAllProducts();
        DatatableBean dt = new DatatableBean();
        dt.setData(beanLst);
        model.addAttribute("dt",JsonUtils.objectToJson(dt));
        
        List<CategoryBean> catLst = categorySingleton.getCatLst();
        model.addAttribute("catLst",JsonUtils.objectToJson(catLst));
        
        
        return "products/product_list";
    }
    
    
    @GetMapping("/testproductList")
    @ResponseBody
    public Object testgo2ProductList(Model model) throws JsonProcessingException {
        model.addAttribute(Constant.Model_Key_Menu_Light_Id,Constant.Menu_Product);
        model.addAttribute(Constant.Menu_Active_Id,Constant.Menu_Product_Act);
        model.addAttribute(Constant.Model_Key_Submenu_Light_Id,Constant.Submenu_Product_List);
        
        List<ProductSearchBean> beanLst =  productService.getAllProducts();
        DatatableBean dt = new DatatableBean();
        dt.setData(beanLst);
        
        ObjectMapper objectMapper = new ObjectMapper();
        
        model.addAttribute("dt",objectMapper.writeValueAsString(dt));
        return dt;
    }
    
    
    
    @PostMapping("/v1/uploadProductImg.do")
    @ResponseBody
    public ResponseBean doUploadProductImg(@RequestParam("file") MultipartFile file, HttpServletRequest request,HttpServletResponse response) throws IOException {
    	InputStream fis = null;
		try {
			 fis = file.getInputStream();
		}catch(Exception ex) {
			ex.printStackTrace();
		}
    	
		ResponseBean result = new ResponseBean();
		if(null != fis) {
			HttpSession session = request.getSession();
			String uuid = UUID.randomUUID().toString();
			ProductImageBean productImg = new ProductImageBean();
			productImg.setFis(fis);
			productImg.setFile(file);
			productImg.setFile_id(uuid);
			session.setAttribute(uuid, productImg);
			
			
			result.setFile_id(uuid);
			result.setStatus(HttpStatus.OK.value());
			
		}else {
			result.setStatus(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS.value());
			response.sendError(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS.value());
		}
		
		return result;
    }
    
    @PostMapping("/v1/uploadProductImg4Edit.do")
    @ResponseBody
    public ResponseBean doUploadProductImg4Edit(@RequestParam("file") MultipartFile file, HttpServletRequest request,HttpServletResponse response) throws IOException {
    	InputStream fis = null;
		try {
			 fis = file.getInputStream();
		}catch(Exception ex) {
			ex.printStackTrace();
		}
    	
		ResponseBean result = new ResponseBean();
		if(null != fis) {
			HttpSession session = request.getSession();
			String uuid = UUID.randomUUID().toString();
			ProductImageBean productImg = new ProductImageBean();
			productImg.setFis(fis);
			productImg.setFile(file);
			productImg.setFile_id(uuid);
			session.setAttribute(uuid, productImg);
			
			
			result.setFile_id(uuid);
			result.setStatus(HttpStatus.OK.value());
			
		}else {
			result.setStatus(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS.value());
			response.sendError(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS.value());
		}
		
		return result;
    }
    
    @PostMapping("/createProduct.do")
    public String doCreateProduct(@RequestParam Map<String,String> allParams,Model model, HttpServletRequest request,HttpServletResponse response) throws IOException {
    	System.out.println("param are " + allParams.entrySet());
    	
    	if(null == allParams && allParams.size() == 0) {
            model.addAttribute(Constant.Alert_type,Constant.Alert_type_error);
            model.addAttribute(Constant.Alert_msg,"Missing parameters!");
    		
    	}else {
    		
    		Boolean result = false;

    		
        	HttpSession session = request.getSession();
        	
        	ProductImageBean productImgBean = (ProductImageBean) session.getAttribute(allParams.get("file_id"));
        	String filePath = storageServiceInterface.store(productImgBean.getFile());
        	productImgBean.setFilePathStr(filePath);
        	productImgBean.setFile_id(allParams.get("file_id"));
        	productImgBean.setProduct_name(allParams.get("product_name"));
        	productImgBean.setProduct_desc(allParams.get("product_desc"));
        	productImgBean.setMarked_price(Double.parseDouble(allParams.get("marked_price")));
        	productImgBean.setSelling_price(Double.parseDouble(allParams.get("selling_price")));
        	productImgBean.setStocks(Integer.parseInt(allParams.get("stocks")));
        	productImgBean.setCategory_id(allParams.get("category"));
        	
        	if(!categorySingleton.getCatMap().containsKey(productImgBean.getCategory_id())) {
        		result = false;
        	}else {
            	result = productService.saveProduct(productImgBean);
            	
            	//System.out.println("param are " + productImgBean.getFile().getOriginalFilename());
        		
            	
                model.addAttribute(Constant.Model_Key_Menu_Light_Id,Constant.Menu_Product);
                model.addAttribute(Constant.Menu_Active_Id,Constant.Menu_Product_Act);
                model.addAttribute(Constant.Model_Key_Submenu_Light_Id,Constant.Submenu_New_Product);
        	}

            
            if(result) {
	            model.addAttribute(Constant.Alert_type,Constant.Alert_type_success);
	            model.addAttribute(Constant.Alert_msg,"Create product success!");
            }else {
            	model.addAttribute(Constant.Alert_type,Constant.Alert_type_error);
 	            model.addAttribute(Constant.Alert_msg,"Create product failed!");
 	            List<CategoryBean> catLst = categorySingleton.getCatLst();
 	            model.addAttribute("catLst",JsonUtils.objectToJson(catLst));
            }
    	}

        List<CategoryBean> catLst = categorySingleton.getCatLst();
        model.addAttribute("catLst",JsonUtils.objectToJson(catLst));
        return "products/new_product";
    	
    }


    @PostMapping("/updateProduct.do")
    public RedirectView doUpdateProduct(@RequestParam Map<String,String> allParams,Model model, HttpServletRequest request,HttpServletResponse response) throws IOException {
    	System.out.println("param are " + allParams.entrySet());
    	
    	if(null == allParams && allParams.size() == 0) {
            model.addAttribute(Constant.Alert_type,Constant.Alert_type_error);
            model.addAttribute(Constant.Alert_msg,"Missing parameters!");
    		
    	}else {
    		
    		Boolean result = false;
    		
        	HttpSession session = request.getSession();

        	ProductImageBean productImgBean = null;
        	if(null != session.getAttribute(allParams.get("file_id")) ) {
        		productImgBean = (ProductImageBean) session.getAttribute(allParams.get("file_id"));
        	}else {
        		productImgBean = new ProductImageBean();
        	}
        	 
        	
        	productImgBean.setId(allParams.get("rid"));
        	productImgBean.setFile_id(allParams.get("file_id"));
        	productImgBean.setProduct_name(allParams.get("product_name"));
        	productImgBean.setProduct_desc(allParams.get("product_desc"));
        	productImgBean.setMarked_price(Double.parseDouble(allParams.get("marked_price")));
        	productImgBean.setSelling_price(Double.parseDouble(allParams.get("selling_price")));
        	productImgBean.setStocks(Integer.parseInt(allParams.get("stocks")));
        	productImgBean.setCategory_id(allParams.get("category"));
        	
        	if(!categorySingleton.getCatMap().containsKey(productImgBean.getCategory_id())) {
        		result = false;
        	}else {
            	result = productService.updateProduct(productImgBean);
            	
            	//System.out.println("param are " + productImgBean.getFile().getOriginalFilename());
        		
            	
                model.addAttribute(Constant.Model_Key_Menu_Light_Id,Constant.Menu_Product);
                model.addAttribute(Constant.Menu_Active_Id,Constant.Menu_Product_Act);
                model.addAttribute(Constant.Model_Key_Submenu_Light_Id,Constant.Submenu_New_Product);
        	}

            
            if(result) {
	            model.addAttribute(Constant.Alert_type,Constant.Alert_type_success);
	            model.addAttribute(Constant.Alert_msg,"Create product success!");
            }else {
            	model.addAttribute(Constant.Alert_type,Constant.Alert_type_error);
 	            model.addAttribute(Constant.Alert_msg,"Create product failed!");
 	            List<CategoryBean> catLst = categorySingleton.getCatLst();
 	            model.addAttribute("catLst",JsonUtils.objectToJson(catLst));
            }
    	}

    	
    	/***
    	 * 
    	 * 
    	 * 
    	 * param are [rid=f19ec507-d954-432c-8203-5f59e0d495c5, file_id=bf9557d1-0008-4937-9e01-1df662748bcb, product_name=LEGO 71040 The Disney Castle, product_desc=LEGO 71040 The Disney Castle, marked_price=3200, selling_price=3049, stocks=10, category=7kMVhstoPvxtbTCM5Ykb]
Object f19ec507-d954-432c-8203-5f59e0d495c5_Lego_71040.jpg was deleted from kw_fyp_storage
File uploaded to bucket kw_fyp_storage as f19ec507-d954-432c-8203-5f59e0d495c5_Lego_71040_2.jpg
Public URL: https://storage.googleapis.com/kw_fyp_storage/f19ec507-d954-432c-8203-5f59e0d495c5_Lego_71040_2.jpg
Gsuri: gs://kw_fyp_storage/f19ec507-d954-432c-8203-5f59e0d495c5_Lego_71040_2.jpg
Upload photo to cloud storage success = true
Reference image deleted from product.
Reference image id: image_f19ec507-d954-432c-8203-5f59e0d495c5
Reference image name: projects/seismic-aloe-340714/locations/asia-east1/products/f19ec507-d954-432c-8203-5f59e0d495c5/referenceImages/image_f19ec507-d954-432c-8203-5f59e0d495c5
Reference image uri: gs://kw_fyp_storage/f19ec507-d954-432c-8203-5f59e0d495c5_Lego_71040_2.jpg
Create Ref Image Success = true
    	 * 
    	 * java.util.concurrent.ExecutionException: com.google.api.gax.rpc.NotFoundException: io.grpc.StatusRuntimeException: NOT_FOUND: No document to update: projects/seismic-aloe-340714/databases/(default)/documents/products/f19ec507-d954-432c-8203-5f59e0d495c5
	at com.google.common.util.concurrent.AbstractFuture.getDoneValue(AbstractFuture.java:588)
	at com.google.common.util.concurrent.AbstractFuture.get(AbstractFuture.java:567)
	at com.google.common.util.concurrent.FluentFuture$TrustedFuture.get(FluentFuture.java:92)
	at com.google.common.util.concurrent.ForwardingFuture.get(ForwardingFuture.java:66)
	at com.kw.firebase.FirebaseService.updateProduct(FirebaseService.java:145)
	at com.kw.services.impl.ProductsService.updateProduct(ProductsService.java:260)
	at com.kw.controllers.ProductsController.doUpdateProduct(ProductsController.java:252)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    	 * 
    	 */
    	//return "products/productList";
    	return new RedirectView("productList");
    	
    }
    
    
    
    @GetMapping("/v1/testSuggestedProductList")
    @ResponseBody
    public Object testSuggestedProductList(Model model) throws JsonProcessingException {
      
        
        List<ProductSearchBean> beanLst =  productService.findSuggestedProductByPhoto(Arrays.asList("7e7a330d-a861-4977-b77e-7df33241c4eb"),Arrays.asList("Window blind"));
 
        return beanLst;
    }
    
    @GetMapping("/v1/testFindProductById")
    @ResponseBody
    public Object testFindProductById(Model model) throws JsonProcessingException {
      
        
        ProductSearchBean bean =  productService.getProductById("light_2_test_7e7a330d-a861-4977-b77e-7df33241c4eb");
 
        return bean;
    }
}
