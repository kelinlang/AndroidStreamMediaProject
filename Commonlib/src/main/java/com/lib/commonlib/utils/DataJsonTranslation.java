package com.lib.commonlib.utils;



import com.third.google.gson.Gson;
import com.third.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class DataJsonTranslation {


    /**
     * java对象转换成json字符串..
     *
     * @param object the Object
     * @return json字符串 string
     */
    public static String objectToJson(Object object) {

        String jsonString = null;
        Gson gson = new Gson();
        try {
            jsonString = gson.toJson(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonString;
    }

    /**
     * Json字符串转换成java对象.
     *
     * @param json        the json
     * @param classOfJson the class of json
     * @return the object
     */
    public static Object jsonToObject(String json, Class classOfJson) {
        Gson gson = new Gson();
        Object obj = null;
        try {
            obj = gson.fromJson(json, classOfJson);
        } catch (JsonSyntaxException e) {
            MLog.e("Exception " + e);
        }

        return obj;
    }

    /**
     * 将Json对象转换成键值映射表
     *
     * @param jsonString json对象
     * @return Map对象 map
     */
    public static Map<String, String> toMap(String jsonString) {

        Map<String, String> result = new HashMap<>();
        JSONObject jsonObject = null;
        String key = null;
        String value = null;

        try
        {
            jsonObject = new JSONObject(jsonString);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return result;
        }

        Iterator iterator = jsonObject.keys();

        while (iterator.hasNext())
        {
            key = (String) iterator.next();
            try
            {
                value = jsonObject.getString(key);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
                value = "";
            }
            result.put(key, value);
        }
        return result;

    }
}
