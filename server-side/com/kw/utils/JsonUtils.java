package com.kw.utils;


import java.io.IOException;
import java.util.List;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {
	private static final String TAG = "JsonUtils";
	//private static final Logger log = Logger.getLogger(JsonUtils.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * <p>Title: pojoToJson</p>
     * <p>Description: Object to json string</p>
     * @param data
     * @return
     */
    public static String objectToJson(Object data) {
    	try {
    			
			String string = MAPPER.writeValueAsString(data);
			return string;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
    	return null;
    }
    
    public static <T> List<T> jsonToListObj(String pJstr, Class<T> pBeanType){
    	   List<T> objLst = null;
		try {
			JavaType classType = MAPPER.getTypeFactory().constructParametricType(List.class, pBeanType);
			objLst = MAPPER.readValue(pJstr, classType);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

    	   return objLst;
    }
    
    /**
     * 
     * @param jsonData json数据
     * @param json to Oject
     * @return
     */
    public static <T> T jsonToObject(String jsonData, Class<T> beanType) {
        try {
            T t = MAPPER.readValue(jsonData, beanType);
            return t;
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return null;
    }
}
