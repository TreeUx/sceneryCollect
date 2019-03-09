package com.bx.touristsinfo.controller;

import com.alibaba.fastjson.JSONObject;
import com.bx.touristsinfo.services.DetailParkInfoService;
import com.bx.touristsinfo.utils.RequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 获取时区信息
 * @Author Breach
 * @Date 2018/12/19
 * @Version V1.0
 **/
@RestController
@RequestMapping("/bx")
public class TimeZoneController {
    @Autowired
    DetailParkInfoService detailParkInfoService;
    private static final String PREFIX_URL = "http://api.map.baidu.com/timezone/v1?location=";
    private static String SUFFIX_URL = "&timestamp=0&ak=TyZ0AadANQFCotrdzl3wazPGMWw5cn2R";

    @RequestMapping("/timezone")
    public Map<String, Object> getTimezoneInfos() {
        String url = "";
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> locaList = detailParkInfoService.getLocationInfo();
        for (int i = 0 ; i < locaList.size(); i++) {
            int id = Integer.parseInt(locaList.get(i).get("id").toString());//id
            String location = locaList.get(i).get("com_central").toString();//坐标
            if(location != "" && location != null && location.length() > 10) {
                System.out.println(id);
                if(id < 4000) {
                    int index = location.indexOf(",");
                    int len = location.length();
                    location = location.substring(index+1, len) + "," + location.substring(0, index);
                }
                url = PREFIX_URL + location + SUFFIX_URL;
                System.out.println(url);
                JSONObject ja = RequestUtils.sendPost(url);
                if("0".equals(ja.get("status").toString())) {
                    String time_zone_id = ja.get("timezone_id").toString();//时区id
                    String dst_offset = ja.get("dst_offset").toString();//夏令时偏移秒数
                    String raw_offset = ja.get("raw_offset").toString();//协调世界时偏移秒数
                    Map<String, Object> para = new HashMap<>();
                    para.put("id", id);
                    para.put("time_zone_id", time_zone_id);
                    para.put("dst_offset", dst_offset);
                    para.put("raw_offset", raw_offset);
                    int num = detailParkInfoService.updateTimezoneInfo(para);
                    if(num != 0) {
                        result.put("status", "success");
                    } else {
                        result.put("status", "error");
                    }
                } else {
                    continue;
                }
            } else {
                continue;
            }
        }
        System.out.println("执行完毕");
        return result;
    }
}
