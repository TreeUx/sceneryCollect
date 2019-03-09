package com.bx.touristsinfo.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bx.touristsinfo.common.MapConstant;

import java.util.Map;

public class MapResultUtils {

    /**
      * @Author Breach
      * @Description 根据不同的需求获取不同的请求地址
      * @Date 2018/11/2
      * @Param param 请求中的各种参数
      * @return java.lang.String
      */
    public static String getRequestUrl(Map<String, Object> param) {
        boolean cl = true;
        //请求前缀
        String prefixUrl = MapConstant.BD_PREFIX_URL;
        //请求后缀
        String suffixUrl = MapConstant.BD_SUFFIX_URL;
        //请求后缀ak
        String backUrl = MapConstant.BD_OUTPUT_AND_AK;
        //请求参数query
        String query = MapConstant.BD_QUERY;
        //请求参数region
        String region = MapConstant.BD_REGION;
        //请求参数city_limit
        String city_limit = MapConstant.BD_CITY_LIMIT;
        //请求参数scope
        String scope = MapConstant.BD_SCOPE;
        //请求参数tap
        String tap = MapConstant.BD_TAG;
        //请求参数page_size
        String page_size = MapConstant.BD_PAGE_SIZE;
        //请求参数page_num
        String page_num = MapConstant.BD_PAGE_NUM;
        //AK
        String server_ak = MapConstant.BROWSER_AK;
        String url = prefixUrl + suffixUrl + query + param.get("query") + region + param.get("region") + city_limit + cl;
        if(param.get("scope") != "undefined" && !"undefined".equals(param.get("scope")) && param.get("scope") != null) {
            url += scope + param.get("scope");
        }
        if(param.get("tag") != "undefined" && !"undefined".equals(param.get("tag")) && param.get("tag") != null) {
            url += tap + param.get("tag");
        }
        if(param.get("page_size") != "undefined" && !"undefined".equals(param.get("page_size")) && param.get("page_size") != null) {
            url += page_size + param.get("page_size");
        }
        if(param.get("page_num") != "undefined" && !"undefined".equals(param.get("page_num")) && param.get("page_num") != null) {
            url += page_num + param.get("page_num");
        }

        url += backUrl + server_ak;
        System.out.println(url);
        return url;
    }

    /**
     * @Author Breach
     * @Description 根据省市区域获取百度地图信息
     * @Date 2018/11/1
     * @Param param 请求中的各种参数
     * @return void
     */
    public static JSONArray getMapResult(Map<String, Object> param) {
//        JSONObject jot = new JSONObject();
        String url = getRequestUrl(param);
        System.out.println(url);
        JSONObject jo = RequestUtils.sendPost(url);
        JSONArray results = (JSONArray) jo.get("results");
        return results;
    }


}

  