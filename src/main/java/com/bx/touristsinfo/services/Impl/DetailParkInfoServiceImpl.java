package com.bx.touristsinfo.services.Impl;

import com.bx.touristsinfo.dao.DetailParkInfoMapper;
import com.bx.touristsinfo.model.TouristDetail;
import com.bx.touristsinfo.services.DetailParkInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Description TODO
 * @Author Breach
 * @Date 2018/12/11
 * @Version V1.0
 **/
@Service
public class DetailParkInfoServiceImpl implements DetailParkInfoService {
    @Autowired
    DetailParkInfoMapper detailParkInfoMapper;

    /**
      * @Author Breach
      * @Description 查询所有景区名称
      * @Date 2018/12/11
      * @Param
      * @return java.util.List<java.lang.String>
      */
    @Override
    public List<String> findParkNameInfo() {
        return detailParkInfoMapper.findParkNameInfo();
    }

    /**
      * @Author Breach
      * @Description 保存全球景点数据到tourist_detail表中
      * @Date 2018/12/11
      * @Param touristDetail
      * @return int
      */
    @Override
    public int saveDetailInfo(TouristDetail touristDetail) {
        return detailParkInfoMapper.saveDetailInfo(touristDetail);
    }

    @Override
    public List<Map<String, Object>> getLocationInfo() {
        return detailParkInfoMapper.getLocationInfo();
    }

    @Override
    public int updateTimezoneInfo(Map<String, Object> para) {
        return detailParkInfoMapper.updateTimezoneInfo(para);
    }

    @Override
    public List<Map<String, Object>> findCitys() {
        return detailParkInfoMapper.findCitys();
    }

    @Override
    public int updateCityLocationInfos(List<Map<String, Object>> cityList) {
        return detailParkInfoMapper.updateCityLocationInfos(cityList);
    }
}
