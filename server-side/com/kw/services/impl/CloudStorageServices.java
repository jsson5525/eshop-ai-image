package com.kw.services.impl;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.kw.beans.CloudStorageBean;
import com.kw.constants.Constant;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Pattern;

@Service
public class CloudStorageServices {

    public CloudStorageBean uploadPhotoObject(
            String projectId, String bucketName, String objectName, InputStream fis) throws IOException {
        // The ID of your GCP project
        // String projectId = "your-project-id";

        // The ID of your GCS bucket
        // String bucketName = "your-unique-bucket-name";

        // The ID of your GCS object
        // String objectName = "your-object-name";

        // The path to your file to upload
        // String filePath = "path/to/your/file"

        String mimeType = null;

        String jpgStandard = "(.jpg|.jpeg|.jpe|.jif|.jfif)";
        if (objectName.contains(".png")){
            mimeType = Constant.ImageType_MIME.IMAGE_PNG.getValue();
        }else if  (objectName.contains(".gif")){
            mimeType = Constant.ImageType_MIME.IMAGE_GIF.getValue();
        }else if(Pattern.compile(jpgStandard).matcher(objectName).find()){
            mimeType= Constant.ImageType_MIME.IMAGE_JPEG.getValue();
        }

        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        BlobId blobId = BlobId.of(bucketName, objectName);
        BlobInfo blobInfo = null;
        if(null != mimeType){
            blobInfo = BlobInfo.newBuilder(blobId).setContentType(mimeType).build();
        }else{
            blobInfo = BlobInfo.newBuilder(blobId).build();
        }
        //storage.create(blobInfo, Files.readAllBytes(Paths.get(filePath)));

        storage.createFrom(blobInfo,fis);


        /*
        File uploaded to bucket kw_fyp_storage as light_sample_1.jpg
        Public URL: https://storage.googleapis.com/kw_fyp_storage/light_sample_1.jpg
        Gsuri: gs://kw_fyp_storage/light_sample_1.jpg
        */

        System.out.println(
                "File uploaded to bucket " + bucketName + " as " + objectName);
        System.out.println(String.format("Public URL: %s%s/%s", Constant.public_url_domain,Constant.cloud_storage_bucket_name,objectName));
        System.out.println(String.format("Gsuri: %s%s/%s", Constant.cloud_storage_gsurl_prefix,Constant.cloud_storage_bucket_name,objectName));
        
        CloudStorageBean result = new CloudStorageBean();
        result.setPub_url(String.format("%s%s/%s", Constant.public_url_domain,Constant.cloud_storage_bucket_name,objectName));
        result.setGsutil_uri(String.format("%s%s/%s", Constant.cloud_storage_gsurl_prefix,Constant.cloud_storage_bucket_name,objectName));
        return result;
    }
    
    public void deleteObject(String projectId, String bucketName, String objectName) {
        // The ID of your GCP project
        // String projectId = "your-project-id";

        // The ID of your GCS bucket
        // String bucketName = "your-unique-bucket-name";

        // The ID of your GCS object
        // String objectName = "your-object-name";

        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        storage.delete(bucketName, objectName);

        System.out.println("Object " + objectName + " was deleted from " + bucketName);
      }

}
