package com.kw.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

public class Constant {

	/** GCP settings **/
	//Should be same with Google project ID
	public static final String projectId = "seismic-aloe-340714";


	public static String productSetId = "Prj_Jason_FYP_Product_Set";


	public  static String projectSetDisplayName = "Fyp Product Set";

	public static  String cloud_storage_bucket_name = "kw_fyp_storage";

	//#https://storage.googleapis.com/kw_fyp_storage/light_2_test.jpg
	public static  String public_url_domain = "https://storage.googleapis.com/";
	
	public static Float Photo_search_confidence_threshold = 0.68f;
	
	//confidence threshold for image detection object
	public static Float Img_detect_object_confidence_threshold = 0.50f;

	//#gs://kw_fyp_storage/light_2_test.jpg
	//#gs://<bucket_name>/<file_path_inside_bucket>
	public static  String cloud_storage_gsurl_prefix = "gs://";

	/*
		Region-name is the GCP location that will run your tutorial,
		For example, us-east1. Valid location identifiers are: us-west1, us-east1, europe-west1, and asia-east1 .
	 */

	public static enum GCP_Region_Name{
		ASIA_EAST1("asia-east1");
		
		public String value;
		
		GCP_Region_Name(String value){
			this.value = value;
		}
		
		public String getValue() {
			return this.value;
		}
		
	}

	//region-name is the GCP location that will run your tutorial,
	//for example, us-east1. Valid location identifiers are: us-west1, us-east1, europe-west1, and asia-east1 .
	/*
		product_category
		Immutable. The category for the product identified by the reference image.
		This should be one of "homegoods-v2", "apparel-v2", "toys-v2", "packagedgoods-v1" or "general-v1".
		The legacy categories "homegoods", "apparel", and "toys" are still supported, but these should not be used for new products.

		For the product search:
		The list of product categories to search in. Currently, we only consider the first category, and either "homegoods-v2", "apparel-v2", "toys-v2", "packagedgoods-v1",
		or "general-v1" should be specified.
		The legacy categories "homegoods", "apparel", and "toys" are still supported but will be deprecated.
		For new products, please use "homegoods-v2", "apparel-v2", or "toys-v2" for better product search accuracy.
		It is recommended to migrate existing products to these categories as well.
	 */
	public static enum ProductCategory{
		HOME_GOODS_V2("homegoods-v2"),
		APPAREL_V2("apparel-v2"),
		TOYS_V2("toys-v2"),
		PACKAGED_GOODS_V1("packagedgoods-v1"),
		GENERAL_V1("general-v1");

		public String value;

		ProductCategory(String value){
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}

	}

	public static enum ImageType_MIME{
		IMAGE_JPEG("image/jpeg"),
		IMAGE_PNG("image/png"),
		IMAGE_GIF("image/gif");

		public String value;

		ImageType_MIME(String value){
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}

	}

	/** End of GCP settings **/

	/** Model Key **/
	public static String Model_Key_Menu_Light_Id = "Menu_Light_Id";
	public static String Menu_Active_Id = "Menu_Active_id";
	public static String Model_Key_Submenu_Light_Id = "Submenu_Light_Id";
	//alerts modal
	public static String Alert_type = "Alert_type";
	public static String Alert_msg = "Alert_msg";
	
	/** End of Model Key **/

	/** Model Value **/
	//left menu html element id
	public static String Menu_Dashboard = "menu_dashboard";
	public static String Menu_Dashboard_Act = "menu_dashboard_act";
	public static String Submenu_DashboardV1 = "submenu_dashboard_1";
	
	public static String Menu_Product = "menu_products";
	public static String Menu_Product_Act = "menu_product_act";
	public static String Submenu_New_Product = "submenu_new_product";
	public static String Submenu_Product_List = "submenu_product_list";
	
	
	//alerts modal - html 5
	public static String Alert_type_success = "success";
	public static String Alert_type_info = "info";
	public static String Alert_type_error = "error";
	public static String Alert_type_warning = "warning";
	public static String Alert_type_question = "question";
	//end alerts modal
	/** End of Model Value **/

}
