package com.bx.touristsinfo.services.Impl;

import com.bx.touristsinfo.dao.CaptureTouristInfoMapper;
import com.bx.touristsinfo.model.BxMerchant;
import com.bx.touristsinfo.services.CaptureTouristInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Description TODO
 * @Author Breach
 * @Date 2018/12/10
 * @Version V1.0
 **/
@Service
public class CaptureTouristInfoServiceImpl implements CaptureTouristInfoService {
    @Autowired
    CaptureTouristInfoMapper captureTouristInfoMapper;

    /**
     * @Author Breach
     * @Description 查询全球城市信息
     * @Date 2018/12/10
     * @Param
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     */
    @Override
    public List<Map<String, Object>> queryCityInfo(int id) {
        return captureTouristInfoMapper.queryCityInfo(id);
    }

    /**
      * @Author Breach
      * @Description 查询所有景点数据
      * @Date 2018/12/10
      * @Param
      * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
      */
    @Override
    public List<Map<String, Object>> findParkInfos() {
        return captureTouristInfoMapper.findParkInfos();
    }

    /**
      * @Author Breach
      * @Description 修改景区出入口数据
      * @Date 2018/12/10
      * @Param para
      * @return int
      */
    @Override
    public int updateNaviLocaByUid(Map<String, Object> para) {
        return captureTouristInfoMapper.updateNaviLocaByUid(para);
    }

    /**
      * @Author Breach
      * @Description 查询省市区id
      * @Date 2019/1/10
      * @Param paras
      * @return java.util.List<java.lang.String>
      */
    @Override
    public String queryId(String id) {
        return captureTouristInfoMapper.queryId(id);
    }

    @Override
    public List<Map<String, Object>> queryTimezoneInfo(String cityId) {
        return captureTouristInfoMapper.queryTimezoneInfo(cityId);
    }



    @Override
    public int findCountByUid(String name) {
        return captureTouristInfoMapper.findCountByUid(name);
    }

    /**
      * @Author Breach
      * @Description 数据库中插入全球景点等数据
      * @Date 2018/12/10
      * @Param tv
      * @return java.lang.Boolean
      */
    @Override
    public Boolean addBdTouristsInfo(BxMerchant bmc) {
        return captureTouristInfoMapper.addBdTouristsInfo(bmc);
    }

    /**
      * @Author Breach
      * @Description 采集全球数据
      * @Date 2019/1/14
      * @Param bmc
      * @return java.lang.Boolean
      */
    @Override
    public List<Map<String, Object>> queryGlobalCityInfo() {
        return captureTouristInfoMapper.queryGlobalCityInfo();
    }

    @Override
    public List<Map<String, Object>> queryIdByCity(String city) {
        return captureTouristInfoMapper.queryIdByCity(city);
    }

    /**
      * @Author Breach
      * @Description  根据父类id查询相应数据
      * @Date 2019/1/14
      * @Param parentid
      * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
      */
    @Override
    public List<Map<String, Object>> queryInfoByParentid(int id) {
        return captureTouristInfoMapper.queryInfoByParentid(id);
    }

    @Override
    public String queryCityNameById(String region) {
        return captureTouristInfoMapper.queryCityNameById(region);
    }

    /**
      * @Author Breach
      * @Description 根据景点名称查询景点是否存在
      * @Date 2019/1/18
      * @Param merName
      * @return int
      */
    @Override
    public List<Map<String, Object>> findCountByName(Map<String, Object> paras) {
        return captureTouristInfoMapper.findCountByName(paras);
    }

    /**
      * @Author Breach
      * @Description 修改景点数据
      * @Date 2019/1/19
      * @Param bmc
      * @return int
      */
    @Override
    public int udateSceneryInfo(BxMerchant bmc) {
        return captureTouristInfoMapper.udateSceneryInfo(bmc);
    }

    @Override
    public int checkPsw(Map<String, Object> para) {
        return captureTouristInfoMapper.checkPsw(para);
    }


}
