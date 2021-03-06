/*
 * Copyright (c) 2014,KJFrameForAndroid Open Source Project,张涛.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.louding.frame.http;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;
import org.json.JSONException;
import org.json.JSONObject;

import com.louding.frame.utils.FormatUtils;
import com.louding.frame.utils.StringUtils;

/**
 * Http请求中参数集<br>
 * <b>创建时间</b> 2014-8-7
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.3
 */
public class HttpParams {
//    private ConcurrentHashMap<String, String> urlParams;
	
	private ConcurrentHashMap<String, Object> urlParams;
	
    public ConcurrentHashMap<String, FileWrapper> fileWraps;

    Map cookieNameDict = new Hashtable();
    //Map<Object, Object> cookieNameDict = new Hashtable<Object, Object>;
    
    
    private void init(int i) {
    	
        
        urlParams = new ConcurrentHashMap<String, Object>(8);
        fileWraps = new ConcurrentHashMap<String, FileWrapper>(i);
    }

    private void init() {
        init(4);
    }

    /**
     * 构造器
     * 
     * <br>
     * <b>说明</b> 为提高效率默认使用4个文件作为List大小
     */
    public HttpParams() {
        init();
    }
    
    private Object baseServerData;
    
    /**
     * 构造器
     * 
     * <br>
     * <b>说明</b> 为提高效率默认使用4个文件作为List大小
     */
    public HttpParams(Object baseServerData) {
        init();
        this.baseServerData = baseServerData;
    }

    /**
     * 构造器
     * 
     * @param i
     *            Http请求参数中文件的数量
     */
    public HttpParams(int i) {
        init(i);
    }

    public boolean haveFile() {
        return (fileWraps.size() != 0);
    }

    public void put(String key, Object value) {
        if (key != null && value != null) {
            urlParams.put(key, value);
        } else {
            throw new RuntimeException("key or value is NULL");
        }
    }
    

    public void put(String key, byte[] file) {
        put(key, new ByteArrayInputStream(file));
    }

    public void put(String key, File file) throws FileNotFoundException {
        put(key, new FileInputStream(file), file.getName());
    }

    public void put(String key, InputStream value) {
        put(key, value, "fileName");
    }

    public void put(String key, InputStream value, String fileName) {
        if (key != null && value != null) {
            fileWraps.put(key, new FileWrapper(value, fileName, null));
        } else {
            throw new RuntimeException("key or value is NULL");
        }

    }

    public void remove(String key) {
        urlParams.remove(key);
        fileWraps.remove(key);
    }

    /*********************** httpClient method ************************************/

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (ConcurrentHashMap.Entry<String, Object> entry : urlParams
                .entrySet()) {
            if (result.length() > 0) {
                result.append("&");
            }
            result.append(entry.getKey());
            result.append("=");
            result.append(entry.getValue());
        }
        for (ConcurrentHashMap.Entry<String, FileWrapper> entry : fileWraps
                .entrySet()) {
            if (result.length() > 0) {
                result.append("&");
            }
            result.append(entry.getKey());
            result.append("=");
            result.append("FILE");
        }
        return result.toString();
    }

    /**
     * 获取参数集
     */
    public HttpEntity getEntity() {
        HttpEntity entity = null;
//        if (!fileWraps.isEmpty()) {
//            MultipartEntity multipartEntity = new MultipartEntity();
//            for (ConcurrentHashMap.Entry<String, String> entry : urlParams
//                    .entrySet()) {
//                multipartEntity.addPart(entry.getKey(), entry.getValue());
//            }
//            int currentIndex = 0;
//            int lastIndex = fileWraps.entrySet().size() - 1;
//            for (ConcurrentHashMap.Entry<String, FileWrapper> entry : fileWraps
//                    .entrySet()) {
//                FileWrapper file = entry.getValue();
//                if (file.inputStream != null) {
//                    boolean isLast = currentIndex == lastIndex;
//                    if (file.contentType != null) {
//                        multipartEntity.addPart(entry.getKey(), file.fileName,
//                                file.inputStream, file.contentType, isLast);
//                    } else {
//                        multipartEntity.addPart(entry.getKey(), file.fileName,
//                                file.inputStream, isLast);
//                    }
//                }
//                currentIndex++;
//            }
//            entity = multipartEntity;
//        } else {
//            try {
//            	
//                entity = new UrlEncodedFormEntity(getParamsList(), "UTF-8");
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//        }
        return entity;
    }

    /**
     * String的参数集，如果参数仅有String而没有File时，为了效率你应该使用KJStringParams
     */
//    protected List<BasicNameValuePair> getParamsList() {
//        List<BasicNameValuePair> lparams = new LinkedList<BasicNameValuePair>();
//        for (ConcurrentHashMap.Entry<String, String> entry : urlParams
//                .entrySet()) {
//            lparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
//        }
//        return lparams;
//    }

//    public String getParamString() {
//        return URLEncodedUtils.format(getParamsList(), "UTF-8");
//    }
    
    public String toJsonString(){
    	if (null != baseServerData) {
    		return FormatUtils.getJson(baseServerData);
    	}
		Enumeration<String> em = urlParams.keys();
		JSONObject param = new JSONObject();
		while(em.hasMoreElements()){
			String s = em.nextElement();
			try {
				Object o = urlParams.get(s);
						
				if(o instanceof Integer){
					param.put(s, Integer.parseInt(o.toString()));
				}else if(o instanceof String){
					//param.put(s, new String(o.toString().getBytes(), "UTF-8"));
					param.put(s,o.toString());
				}
			} catch (JSONException e) {
				e.printStackTrace();
				System.out.println("map转json时格式错误。");
			} 
		}
    		return param.toString();
    }

    /**
     * 封装一个文件参数
     * 
     * @author kymjs(kymjs123@gmail.com)
     */
    public static class FileWrapper {
        public InputStream inputStream;
        public String fileName;
        public String contentType;

        public FileWrapper(InputStream inputStream, String fileName,
                String contentType) {
            this.inputStream = inputStream;
            this.contentType = contentType;
            if (StringUtils.isEmpty(fileName)) {
                this.fileName = "nofilename";
            } else {
                this.fileName = fileName;
            }
        }
    }
}
