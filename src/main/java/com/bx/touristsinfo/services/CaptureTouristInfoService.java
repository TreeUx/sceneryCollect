package com.bx.touristsinfo.services;

import com.bx.touristsinfo.model.BxMerchant;

import java.util.List;
import java.util.Map;

/**
 * @Description TODO
 * @Author Breach
 * @Date 2018/12/10
 * @Version V1.0
 **/
public interface CaptureTouristInfoService {

    int findCountByUid(String name);

    Boolean addBdTouristsInfo(BxMerchant bmc);

    List<Map<String, Object>> queryCityInfo(int id);

    List<Map<String, Object>> findParkInfos();

    int updateNaviLocaByUid(Map<String, Object> para);

    String queryId(String id);

    List<Map<String, Object>> queryTimezoneInfo(String cityId);

    List<Map<String, Object>> queryGlobalCityInfo();

    List<Map<String, Object>> queryIdByCity(String city);

    List<Map<String, Object>> queryInfoByParentid(int id);

    String queryCityNameById(String region);

    List<Map<String, Object>> findCountByName(Map<String, Object> paras);

    int udateSceneryInfo(BxMerchant bmc);

    int checkPsw(Map<String, Object> para);

    int updateSceneryInfo(Map<String, Object> paras);
}
