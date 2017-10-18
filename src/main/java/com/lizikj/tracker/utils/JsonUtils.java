package com.lizikj.tracker.utils;

import java.io.IOException;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

public final class JsonUtils {

    /**
     * 把json文本parse成JsonObject
     *
     * @param json
     * @return JSONObject
     * @author lijundong
     * @date 2017年6月24日 下午3:50:49
     */
    public static JSONObject toJsonObject(String json) {
        return JSONObject.parseObject(json);
    }

    /**
     * 把对象转成JsonObject
     *
     * @param object
     * @return JSONObject
     * @author lijundong
     * @date 2017年6月24日 下午3:51:01
     */
    public static JSONObject toJsonObject(Object object) {
        String json = toJSONString(object);
        return toJsonObject(json);
    }

    /**
     * 把对象转为字符串
     *
     * @param object
     * @return String
     * @author lijundong
     * @date 2017年6月24日 下午3:50:30
     */
    public static String toJSONString(Object object) {
        if (null == object)
            return null;
        return JSONObject.toJSONString(object);
    }

    /**
     * 把json文本parse成JsonArray
     */
    public static JSONArray toJsonArray(String json) {
        return JSONArray.parseArray(json);
    }

    /**
     * 把JSON文本parse为JavaBean
     *
     * @return
     * @throws IOException
     */
    public static <T> T parseObject(String json, TypeReference<T> typeReference) {
        if (json == null)
            return null;
        return JSONObject.parseObject(json, typeReference);
    }

    /**
     * 把Object对象parse为JavaBean
     *
     * @return
     * @throws IOException
     */
    public static <T> T parseObject(Object object, TypeReference<T> typeReference) {
        if (object == null)
            return null;
        return JSONObject.parseObject(object.toString(), typeReference);
    }
    
    /**
     * 根据key获取值
     * @param json
     * @param key
     * @return String
     * @author lijundong
     * @date 2017年8月2日 下午6:06:13
     */
    public static String getValue(String json, String key){
    	JSONObject jsonObject = toJsonObject(json);
    	return jsonObject.getString(key);
    }
}
