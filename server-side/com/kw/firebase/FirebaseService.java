package com.kw.firebase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.SetOptions;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.kw.models.CategoryEntity;
import com.kw.models.ProductEntity;

@Service
public class FirebaseService {

	
	public void testGetProduct() {
		Firestore db = FirestoreClient.getFirestore();
		
		// asynchronously retrieve all users
		ApiFuture<QuerySnapshot> query = db.collection("products").get();
		// ...
		// query.get() blocks on response
		QuerySnapshot querySnapshot;
		try {
			querySnapshot = query.get();
		
		List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
		System.out.println(documents.size());
		
		ProductEntity productEntity;
		if(null != documents) {
			for (QueryDocumentSnapshot document : documents) {
				productEntity = document.toObject(ProductEntity.class);
				System.out.println(productEntity.getProductName());
				System.out.println(productEntity.getProductDesc());
			}
			
		}
		
	
		
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean addProduct(ProductEntity product) {
		Boolean isSuccess = true;
		Firestore db = FirestoreClient.getFirestore();
		// Add document data with auto-generated id.
/*		Map<String, Object> data = new HashMap<>();
		data.put("name", "Tokyo");
		data.put("country", "Japan");*/
		ApiFuture<DocumentReference> addedDocRef = db.collection("products").add(product);
		try {
			System.out.println("Added document with ID: " + addedDocRef.get().getId());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			isSuccess = false;
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			isSuccess = false;
		}
		
		return isSuccess;
	}
	
	public boolean updateProduct(ProductEntity product) {
		Boolean isSuccess = true;
		Firestore db = FirestoreClient.getFirestore();
		// Add document data with auto-generated id.
/*		Map<String, Object> data = new HashMap<>();
		data.put("name", "Tokyo");
		data.put("country", "Japan");*/
		System.out.println("product id = " + product.getId());
		
		ApiFuture<QuerySnapshot> future = db.collection("products").whereEqualTo("id", product.getId()).get();
		String id = "";
		// future.get() blocks on response
		try {
			List<QueryDocumentSnapshot> documents = future.get().getDocuments();
			
			if(null != documents && documents.size() > 0) {
				id = documents.get(0).getId();
			}
			
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		DocumentReference docRef = db.collection("products").document(id);
		
		System.out.println("docRef.getId() = " + docRef.getId());
		System.out.println("docRef = " + docRef);
		//DocumentReference docRef = db.collection("products").document(product.getId());
		Map<String, Object> updatesMap = new HashMap<>();
		if(null != product.getProductName()) {
			updatesMap.put("productName", product.getProductName());
		}
		
		if(null != product.getProductName()) {
			updatesMap.put("productDesc", product.getProductDesc());
		}
		
		if(null != product.getMarked_price()) {
			updatesMap.put("marked_price", product.getMarked_price());
		}
		
		if(null != product.getSelling_price()) {
			updatesMap.put("selling_price", product.getSelling_price());
		}
		
		if(null != product.getStock()) {
			updatesMap.put("stock", product.getStock());
		}
		
		if(null != product.getProductThumb()) {
			updatesMap.put("productThumb", product.getProductThumb());
		}
		
		if(null != product.getGsutil_uri()) {
			updatesMap.put("gsutil_uri", product.getGsutil_uri());
		}
		
		if(null != product.getCategory_id()) {
			updatesMap.put("category_id", product.getCategory_id());
		}
		
		if(null != product.getRef_image_id()) {
			updatesMap.put("ref_image_id", product.getRef_image_id());
		}
		
		if(null != product.getDetectedObject()) {
			updatesMap.put("detectedObject", product.getDetectedObject());
		}
		  
		updatesMap.put("updatedDt", new Date());

		System.out.println("updatesMap = " + updatesMap);
		try {
			  //asynchronously update doc
			  ApiFuture<WriteResult> writeResult = docRef.update(updatesMap);
			  // ...
			  System.out.println("Update time : " + writeResult.get().getUpdateTime());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			isSuccess = false;
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			isSuccess = false;
		}
		
		return isSuccess;
	}
	
	public List<ProductEntity> findProductByIds(List<?> idsLst) {
		Firestore db = FirestoreClient.getFirestore();

		List<ProductEntity> resultLst = new ArrayList<ProductEntity>();
		ApiFuture<QuerySnapshot> future = db.collection("products").whereIn("id", (List<String>) idsLst).get();
		// future.get() blocks on response
		try {
			if(null != future && null != future.get().getDocuments() && future.get().getDocuments().size() > 0  ) {
				List<QueryDocumentSnapshot> documents = future.get().getDocuments();
				
				for (DocumentSnapshot document : documents) {
					ProductEntity entity =  document.toObject(ProductEntity.class);
					resultLst.add(entity);
				}
			
			}
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultLst;
		
	}
	
	public List<ProductEntity> findAllProducts() {
		Firestore db = FirestoreClient.getFirestore();

		List<ProductEntity> resultLst = new ArrayList<ProductEntity>();
		ApiFuture<QuerySnapshot> future = db.collection("products").get();
		// future.get() blocks on response
		try {
			List<QueryDocumentSnapshot> documents = future.get().getDocuments();
			
			for (DocumentSnapshot document : documents) {
				ProductEntity entity =  document.toObject(ProductEntity.class);
				resultLst.add(entity);
			}
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultLst;
		
	}
	
	public ProductEntity findProductById(String id) {
		Firestore db = FirestoreClient.getFirestore();

		ProductEntity entity = null;
		ApiFuture<QuerySnapshot> future = db.collection("products").whereEqualTo("id", id).get();
		// future.get() blocks on response
		try {
			List<QueryDocumentSnapshot> documents = future.get().getDocuments();
			
			if(null != documents && documents.size() > 0) {
				entity =  documents.get(0).toObject(ProductEntity.class);
			}
			
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return entity;
		
	}
	
	public List<CategoryEntity> findAllCategory(){
		Firestore db = FirestoreClient.getFirestore();

		List<CategoryEntity> resultLst = new ArrayList<CategoryEntity>();
		ApiFuture<QuerySnapshot> future = db.collection("category").get();
		// future.get() blocks on response
		try {
			List<QueryDocumentSnapshot> documents = future.get().getDocuments();
			
			for (DocumentSnapshot document : documents) {
				CategoryEntity entity =  document.toObject(CategoryEntity.class);
				entity.setId( document.getId());
				resultLst.add(entity);
			}
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return resultLst;
	}
	
	
	public List<ProductEntity> findSuggestedProducts(List<?> idsLst, List<?> keywordsLst) {
		Firestore db = FirestoreClient.getFirestore();

		//System.out.println("test + " + idsLst);
		
		//ApiFuture<QuerySnapshot> future = db.collection("products").whereNotIn("id", (List<String>) idsLst).whereArrayContains("detectedObject", Arrays.asList("Window blind")).get();
		
		
		ApiFuture<QuerySnapshot> future = null;
		
		if(idsLst.size() > 0) {
			future = db.collection("products").whereNotIn("id", (List<String>) idsLst).get();
		}
		
		ApiFuture<QuerySnapshot> keywordsFuture2 = null;
		if(keywordsLst.size() > 0) {
			keywordsFuture2 = db.collection("products").whereArrayContainsAny("detectedObject", keywordsLst).get();
		}
		Map<String, ProductEntity> notInMap = new HashMap<String, ProductEntity>();
		Map<String, ProductEntity> keywordsMap = new HashMap<String, ProductEntity>();
		List<ProductEntity> resultLst = new ArrayList<ProductEntity>();
		
		
		
		try {
//			System.out.println("future = " + future.get().size());
//			System.out.println("keywordsFuture2 = " + keywordsFuture2.get().size());
			if(null != future && null != future.get().getDocuments() && future.get().getDocuments().size() > 0  ) {
			List<QueryDocumentSnapshot> documents = future.get().getDocuments();
			
			
				for (DocumentSnapshot document : documents) {
					ProductEntity entity =  document.toObject(ProductEntity.class);
					notInMap.put(entity.getId(), entity);
				}
			}
			
			if(null != keywordsFuture2 && null != keywordsFuture2.get().getDocuments() && keywordsFuture2.get().getDocuments().size() > 0  ) {
				List<QueryDocumentSnapshot> documents2 = keywordsFuture2.get().getDocuments();
			
				for (DocumentSnapshot document : documents2) {
					ProductEntity entity =  document.toObject(ProductEntity.class);
					keywordsMap.put(entity.getId(), entity);
				}
			}
			
			
//			System.out.println("notInMap = " + notInMap.size());
//			System.out.println("keywordsMap = " + keywordsMap.size());
			
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// ids: 7e7a330d-a861-4977-b77e-7df33241c4eb
		// not in: 69fa8943-ae81-4464-a401-2c1c0b22c8fd / 6f1fb0ba-fca5-4086-a783-368395343134
		// keywords result: 6f1fb0ba-fca5-4086-a783-368395343134
		
		//filter between two map
		
		if(null != notInMap && null != keywordsMap && notInMap.size() > 0 && keywordsMap.size() > 0) {
			for (Map.Entry<String,ProductEntity> entry : keywordsMap.entrySet()) {
				
				if(notInMap.containsKey(entry.getKey())) {
					resultLst.add( entry.getValue());
				}
		    }
		}else {
			resultLst = keywordsMap.values().stream().collect(Collectors.toList());
		}
		
		
		return resultLst;
		
		
	}
	

}
