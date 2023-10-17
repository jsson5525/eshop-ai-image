package com.kw.controllers;

import com.kw.beans.ProductSearchBean;
import com.kw.beans.ProductSearchResultBean;
import com.kw.beans.ResponseBean;
import com.kw.constants.Constant;
import com.kw.services.StorageServiceInterface;
import com.kw.services.impl.ProductsService;
import com.kw.services.impl.StorageFileNotFoundException;
import com.kw.services.impl.VisionAIServices;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Scope("session")
public class PhotosSearchController {

    @Autowired
    StorageServiceInterface storageServiceInterface;
    
    @Autowired
    VisionAIServices visionService;
    
    @Autowired
    ProductsService productService;
    
    //@GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageServiceInterface.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/v1/testproductSearch")
    public String handleFileUpload(@RequestParam("file") MultipartFile[] file,
                                   RedirectAttributes redirectAttributes) {


        for(MultipartFile fileItem : file){
            storageServiceInterface.store(fileItem);
        }

        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + String.format("%s\r\n%s\r\n",file[0].getOriginalFilename(),file[1].getOriginalFilename()) + "!");

        return "redirect:/";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
    
    
    @PostMapping("/v1/testuploadfile")
    @ResponseBody
    public ResponseBean testDropzone(@RequestParam("file") MultipartFile file, HttpServletRequest request) {

    	
    	HttpSession session = request.getSession();
 
    	ResponseBean result = new ResponseBean();
    	result.setFile_id("testfileid");
    	result.setStatus(HttpStatus.OK.value());
    	System.out.println(file.getOriginalFilename());
    	
        return result;
    }
    
    
    @PostMapping("/v2/photoSearch.do")
    @ResponseBody
    public List<?> doPhotoSearch(@RequestParam("file") MultipartFile file, HttpServletRequest request) {

    	InputStream fis = null;
    	List<?> resultLst = null;
    	List<ProductSearchBean> searchResultsLst = null;
		try {
			fis = file.getInputStream();
			resultLst = visionService.getSimilarProductsFile(Constant.projectId,Constant.GCP_Region_Name.ASIA_EAST1.getValue(), Constant.productSetId,Constant.ProductCategory.HOME_GOODS_V2.getValue(), fis,"");
			searchResultsLst =  productService.searchProductByPhoto((List<ProductSearchBean>) resultLst);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        return searchResultsLst;
    }
    
    @PostMapping("/v3/photoSearch.do")
    @ResponseBody
    public Object doPhotoSearchV3(@RequestParam("file") MultipartFile file, HttpServletRequest request) {

    	InputStream fis = null;
    	List<?> resultLst = null;
    	List<ProductSearchBean> detectedObjLst = null;
    	List<?> suggestedLst = null;
    	List<ProductSearchBean> searchResultsLst = null;
    	ProductSearchResultBean resultsBean = new  ProductSearchResultBean();
		try {
			String filePath = storageServiceInterface.store(file);
			//System.out.println("filePath = " + filePath);
			fis = file.getInputStream();
			resultLst = visionService.getSimilarProductsFile(Constant.projectId,Constant.GCP_Region_Name.ASIA_EAST1.getValue(), Constant.productSetId,Constant.ProductCategory.HOME_GOODS_V2.getValue(), fis,"");
			detectedObjLst = (List<ProductSearchBean>) visionService.detectLocalizedObjects(filePath);
			//detectedObjLst = (List<ProductSearchBean>) visionService.detectLocalizedObjects(fis);
			
			searchResultsLst =  productService.searchProductByPhoto((List<ProductSearchBean>) resultLst);
			suggestedLst = productService.findSuggestedProductByPhoto(searchResultsLst.stream().map(obj -> obj.getId()).collect(Collectors.toList()), detectedObjLst.stream().map(o -> o.getObject_name()).collect(Collectors.toList()));
			resultsBean.setSearch_results(searchResultsLst);
			resultsBean.setSuggested_results(suggestedLst);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        return resultsBean ;
    }

}
