package com.bx.touristsinfo.dao;

import com.bx.touristsinfo.model.BxMerchant;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @Description TODO
 * @Author Breach
 * @Date 2018/12/10
 * @Version V1.0
 **/
@Mapper
public interface CaptureTouristInfoMapper {

    List<Map<String, Object>> queryCityInfo(int id);

    Boolean addBdTouristsInfo(BxMerchant bmc);

    List<Map<String, Object>> findParkInfos();

    int updateNaviLocaByUid(Map<String, Object> para);

    int findCountByUid(String name);

    String queryId(String id);

    List<Map<String, Object>> queryTimezoneInfo(String cityId);

    List<Map<String, Object>> queryGlobalCityInfo();

    List<Map<String, Object>> queryIdByCity(String city);

    List<Map<String, Object>> queryInfoByParentid(int parentid);

    String queryCityNameById(String region);

    List<Map<String, Object>> findCountByName(Map<String, Object> paras);

    int udateSceneryInfo(BxMerchant bmc);

    int checkPsw(Map<String, Object> para);
}
