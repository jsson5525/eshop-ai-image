package com.kw;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.kw.beans.CategoryBean;
import com.kw.services.impl.ProductsService;

@Component
@Scope("singleton")
public class CategorySingleton {

	private static CategorySingleton instance;

	@Lazy
    @Autowired
    ProductsService productService = new ProductsService() ;
    
    private List<CategoryBean> catLst = null;
    
    private Map<String, CategoryBean> catMap = null;
    
    
	private CategorySingleton() {

	}
	
	
//	public static CategorySingleton getInstance() {
//		if(null == instance) {
//			instance = new CategorySingleton();
//		}
//		return instance;
//	}

	public List<CategoryBean> getCatLst() {
		if(catLst == null) {
			catLst = productService.getAllCategory();
			catMap = catLst.stream().collect(Collectors.toMap(x -> x.getId(), x -> x));
		}
		return catLst;
	}


	public Map<String, CategoryBean> getCatMap() {
		if(catLst == null) {
			catLst = productService.getAllCategory();
			catMap = catLst.stream().collect(Collectors.toMap(x -> x.getId(), x -> x));
		}
		return catMap;
	}

}
