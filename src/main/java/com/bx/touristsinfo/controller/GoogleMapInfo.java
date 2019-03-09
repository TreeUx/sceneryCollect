package com.bx.touristsinfo.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bx.touristsinfo.services.DetailParkInfoService;
import com.bx.touristsinfo.utils.RequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description TODO
 * @Author Breach
 * @Date 2018/12/19
 * @Version V1.0
 **/
@RestController
@RequestMapping("/bx")
public class GoogleMapInfo {
    private static final String PREFIX_URL = "https://maps.googleapis.com/maps/api/geocode/json?address=";
    private static final String SUFFIX_URL = "&key=AIzaSyDFNA6uiTeujVvWsfXJ6l_MYNp7Np5rhgw";

    @Autowired
    DetailParkInfoService detailParkInfoService;

    /**
      * @Author Breach
      * @Description 临时方法（查找国外地址的坐标点）
      * @Date 2018/12/19
      * @Param
      * @return void
      */
    @RequestMapping("/central")
    public Map<String, Object> setLocationInfo() {
        String url = "";
        JSONArray results = null;
        JSONObject geometry = null;
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> cityList = new ArrayList<>();
        List<Map<String, Object>> list = new ArrayList<>();
        list = detailParkInfoService.findCitys();
        for (int j = 0; j < list.size(); j++) {
            int id = Integer.parseInt(list.get(j).get("id").toString());
            String area_name = list.get(j).get("area_name").toString();//获取城市名称
            url = PREFIX_URL + area_name + SUFFIX_URL;
            JSONObject jo = RequestUtils.sendPost(url);
            results = JSONArray.parseArray(jo.get("results").toString());
            System.out.println("开始");
            for(int i = 0; i < results.size(); i++) {
                JSONObject jot = JSONObject.parseObject(results.get(i).toString());
                if(jot.containsKey("geometry")) {
                    geometry = JSONObject.parseObject(jot.get("geometry").toString());
                    String com_central = geometry.get("location").toString();//坐标
                    JSONObject locaJo = JSONObject.parseObject(com_central);
                    BigDecimal lngBd = new BigDecimal(locaJo.get("lng").toString()).setScale(6, BigDecimal.ROUND_HALF_UP);
                    BigDecimal latBd = new BigDecimal(locaJo.get("lat").toString()).setScale(6, BigDecimal.ROUND_HALF_UP);
                    com_central = latBd + "," +lngBd;//中心点坐标
                    Map<String, Object> para = new HashMap<>();
                    para.put("id", id);
                    para.put("com_central", com_central);
                    System.out.println(para);
                    cityList.add(para);
                } else {
//                    geometry = JSONObject.parseObject("");
                    Map<String, Object> para = new HashMap<>();
                    para.put("id", id);
                    para.put("com_central", "");
                    System.out.println(para);
                    cityList.add(para);
                }

            }
            if(!cityList.isEmpty() && cityList != null) {
                int num = detailParkInfoService.updateCityLocationInfos(cityList);//修改bx_region数据库坐标为空的值
                if(num != 0) {
                    result.put("status", "success");
                } else {
                    result.put("status", "error");
                }
            }

            System.out.println("结束");
        }
        return result;
    }
}
