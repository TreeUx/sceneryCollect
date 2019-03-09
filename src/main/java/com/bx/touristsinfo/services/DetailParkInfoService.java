package com.bx.touristsinfo.services;

import com.bx.touristsinfo.model.TouristDetail;

import java.util.List;
import java.util.Map;

/**
 * @Description TODO
 * @Author Breach
 * @Date 2018/12/11
 * @Version V1.0
 **/
public interface DetailParkInfoService {
    List<String> findParkNameInfo();

    int saveDetailInfo(TouristDetail touristDetail);

    List<Map<String, Object>> getLocationInfo();

    int updateTimezoneInfo(Map<String, Object> para);

    List<Map<String, Object>> findCitys();

    int updateCityLocationInfos(List<Map<String, Object>> cityList);
}
