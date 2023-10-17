package com.kw.controllers;

import com.kw.configs.ConfigsProperties;
import com.kw.firebase.FirebaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.vision.v1.BatchOperationMetadata;
import com.google.cloud.vision.v1.CreateProductSetRequest;
import com.google.cloud.vision.v1.ImportProductSetsGcsSource;
import com.google.cloud.vision.v1.ImportProductSetsGcsSource.Builder;
import com.google.cloud.vision.v1.ImportProductSetsInputConfig;
import com.google.cloud.vision.v1.ImportProductSetsResponse;
import com.google.cloud.vision.v1.ProductSearchClient;
import com.google.cloud.vision.v1.ProductSet;
import com.google.cloud.vision.v1.ReferenceImage;


import java.io.IOException;
import java.io.PrintStream;
import javax.swing.JPanel;

@Controller
public class HiWorldController {

	@Autowired
	FirebaseService firebaseService;
	
    @Autowired
    ConfigsProperties configsProperties;
//	@RequestMapping("/v1/HiWorld")
//	public String HiWorldApi() {
//		return "Hi fucking HK";
//	}
//	
//	@RequestMapping("/v2/HiWorld")
//	public String HiWorldApi_v2() {
//		return "Hi fucking HK";
//	}

    @RequestMapping("/v1/HiWorld")
    public String HiWorldApi() {
        System.out.println("path　= " +configsProperties.FILE_UPLOAD_DIR);
        firebaseService.testGetProduct();
        return "Hi fucking HK";
    }

    @GetMapping("test")
    public String HiWorldApi_v2() {

        return "test";
    }

    @PostMapping("/v3/HiWorld")
    public String HiWorldApi_v3() {
        return "Hi fucking HK v2";
    }


}