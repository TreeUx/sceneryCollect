package com.bx.touristsinfo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bx.touristsinfo.model.BxMerchant;
import com.bx.touristsinfo.services.CaptureTouristInfoService;
import com.bx.touristsinfo.utils.MapResultUtils;
import com.bx.touristsinfo.utils.ReadTextUtils;
import com.bx.touristsinfo.utils.RequestUtils;
import com.spreada.utils.chinese.ZHConverter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description TODO
 * @Author Breach
 * @Date 2018/12/10
 * @Version V1.0
 **/
@Controller
@Log4j2
@RequestMapping("/")
public class CaptureTouristInfoController {
//    public static String typePath = "D:\\tourist\\oneLevelFile.txt";//一级行业分类列表（刀片机）
//    public static String tagPath = "D:\\tourist\\twoLevelFile.txt";//二级行业分类表名列表（刀片机）
    public static String typePath = "/deploy/production/bx-map/file-path/oneLevelFile.txt";//linux服务器
    public static String tagPath = "/deploy/production/bx-map/file-path/twoLevelFile-Linux.txt";//linux服务器
//    public static String typePath = "F:\\district\\next\\oneLevelFile.txt";//一级行业分类列表
//    public static String tagPath = "F:\\district\\next\\twoLevelFile.txt";//二级行业分类表名列表
    @Autowired
    CaptureTouristInfoService captureTouristInfoService;

    /**
     * @return java.lang.String
     * @Author Breach
     * @Description 用户登录
     * @Date 2019/2/15
     * @Param model
     */
    @RequestMapping("/login")
    public String login(Model model) {
        model.addAttribute("title", "用户登录");
        return "login";
    }

    /**
     * @return java.util.Map<java.lang.String   ,   java.lang.Object>
     * @Author Breach
     * @Description 校验用户名密码
     * @Date 2019/2/15
     * @Param
     */
    @RequestMapping("/check")
    @ResponseBody
    public Map<String, Object> checkInfo(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> para = new HashMap<>();
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        para.put("username", username);
        para.put("password", password);
        int num = captureTouristInfoService.checkPsw(para); //校验用户名密码
        if (num != 0) {
            result.put("status", "success");
            result.put("msg", "登录成功");
        } else {
            result.put("status", "error");
            result.put("msg", "登录失败");
        }
        return result;
    }

    /**
     * @return java.lang.String
     * @Author Breach
     * @Description 页面
     * @Date 2019/1/14
     * @Param
     */
    @RequestMapping("/index")
    public String index(Model model) {
        model.addAttribute("name", "景点数据采集");
        return "index";
    }

    /**
     * @Author Breach
     * @Description 获取BD全球数据
     * @Date 2018/12/10
     * @Param
     */
    @RequestMapping("/captureSceneryInfo")
    @ResponseBody
    public Map<String, Object> startCaptureInfos(HttpServletRequest request) throws ParseException {
        String state = request.getParameter("state");
        String province = request.getParameter("province");
        String city = request.getParameter("city");
        String capture_type = request.getParameter("type");
//        city = "%" + city + "%";
//        List<Map<String, Object>> idList = captureTouristInfoService.queryIdByCity(city); //查询输入的城市的id和父id
        Boolean result = true;
        int num = 0;
        List<Map<String, Object>> list = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        JSONArray jaType = ReadTextUtils.readText(typePath);//一级行业分类
        JSONArray jaTag = ReadTextUtils.readText(tagPath);//二级行业分类
        Map<String, Object> ResultMap = new HashMap<>();

        list = captureTouristInfoService.queryCityInfo(Integer.parseInt(city));//查询出指定城市数据

        /*if ("global".equals(capture_type)) {
            list = captureTouristInfoService.queryGlobalCityInfo();//查询出全球城市数据
        } else {
            if(!idList.isEmpty() && idList != null) {
                int id = Integer.parseInt(idList.get(0).get("id").toString());
                int parentid = Integer.parseInt(idList.get(0).get("parentid").toString());
                if(33 != parentid) {
                    list = captureTouristInfoService.queryInfoByParentid(id);//查询出指定城市数据
                } else {
                    list = captureTouristInfoService.queryCityInfo(id);//查询出指定城市数据
                }
            }
        }*/
        JSONArray jaRegion = JSONArray.parseArray(JSON.toJSONString(list));//将list转为JSONArray
        Map<String, Object> param = new HashMap<>();
        JSONArray jas = new JSONArray();//存放读取的数据
        String tags = "";
        String uid = "";//地区uid
        BxMerchant bmc = new BxMerchant();
        param.put("scope", 2);
        param.put("page_size", 20);//一页查询的数据条数
        for (int i = 0; i < jaType.size(); i++) {//旅游一级菜单信息
            String tag = jaType.get(i).toString();//（获取一级行业分类名称）【如：美食、酒店、购物等】
            param.put("tag", tag);
            String twoLevelName = jaTag.get(i).toString();
            JSONArray ja = ReadTextUtils.readText(twoLevelName);//二级菜单下的子菜单
            for (int k = 0; k < jaRegion.size(); k++) {//区域
                String region = JSONObject.parseObject(jaRegion.get(k).toString()).get("area_name").toString();//搜索条件
                //父类ID
                int parentid = Integer.parseInt(JSONObject.parseObject(jaRegion.get(k).toString()).get("id").toString());
                param.put("region", region);//北京市、广州市
//                for (int j = 0; j < jaTag.size(); j++) {//旅游二级菜单
                for (int t = 0; t < ja.size(); t++) {
                    param.put("query", ja.get(t).toString());
                    for (int m = 0; m < 20; m++) {//请求分页参数
                        param.put("page_num", m);
                        jas = MapResultUtils.getMapResult(param);
                        if (jas != null && !jas.isEmpty()) {
                            System.out.println(jas);
                            for (int n = 0; n < jas.size(); n++) {
                                JSONObject jt = JSONObject.parseObject(jas.get(n).toString());
                                if (jt.containsKey("detail_info")) {
                                    //请求结果中的详情信息
                                    JSONObject detailInfoJo = JSONObject.parseObject(JSONObject.parseObject(jas.get(n).toString()).get("detail_info").toString());
                                    String provinceId = jt.get("province").toString();
                                    String cityId = jt.get("city").toString();
                                    String districtId = jt.get("area").toString();
                                    Map<String, Object> paras = new HashMap<>();
                                    provinceId = captureTouristInfoService.queryId(provinceId);//查询省市区id
                                    bmc.setProvince(provinceId);//省
                                    cityId = captureTouristInfoService.queryId(cityId);//查询省市区id
                                    bmc.setCity(cityId);//市
                                    districtId = captureTouristInfoService.queryId(districtId);//查询省市区id
                                    bmc.setDistrict(districtId);//区
                                    bmc.setState(state); //国家
//                                    bmc.setProvince(province); //省
//                                    bmc.setCity(city); //市
                                    bmc.setMerTime(new Date());
                                    bmc.setMerName(ZHConverter.convert(jt.get("name").toString(), ZHConverter.SIMPLIFIED));//名称
                                    city = ((city == "" || city == null) ? ((provinceId == "" || provinceId == null) ? state : provinceId) : city);
                                    //查询时区
                                    List<Map<String, Object>> timezoneList = captureTouristInfoService.queryTimezoneInfo(city);
                                    int rawOffset = 0;
                                    int dstOffset = 0;
                                    String timezoneId = null;
                                    if (timezoneList.size() != 0) {
                                        timezoneId = timezoneList.get(0).get("timezoneId").toString();
                                        if (timezoneList.get(0).get("rawOffset") != "" && timezoneList.get(0).get("rawOffset") != null) {
                                            rawOffset = Integer.parseInt(timezoneList.get(0).get("rawOffset").toString());
                                            dstOffset = Integer.parseInt(timezoneList.get(0).get("dstOffset").toString());
                                        }
                                    }
                                    bmc.setTimezone(timezoneId);
                                    bmc.setRawOffset(rawOffset);
                                    bmc.setDstOffset(dstOffset);
                                    if (jt.containsKey("address")) {
                                        bmc.setMerAddress(jt.get("address").toString());//地址
                                    } else {
                                        bmc.setMerAddress("");//地址
                                    }
                                    JSONObject location = JSONObject.parseObject(jt.get("location").toString());//中心点坐标
                                    BigDecimal lng = new BigDecimal(location.get("lng").toString()).setScale(6, BigDecimal.ROUND_HALF_UP);
                                    BigDecimal lat = new BigDecimal(location.get("lat").toString()).setScale(6, BigDecimal.ROUND_HALF_UP);
                                    bmc.setMerCentral(lng + "," + lat);//中心点坐标
                                    if (detailInfoJo.containsKey("navi_location")) {
                                        JSONObject navi_location = JSONObject.parseObject(detailInfoJo.get("navi_location").toString());//中心点坐标
                                        BigDecimal navi_lng = new BigDecimal(navi_location.get("lng").toString()).setScale(6, BigDecimal.ROUND_HALF_UP);
                                        BigDecimal navi_lat = new BigDecimal(navi_location.get("lat").toString()).setScale(6, BigDecimal.ROUND_HALF_UP);
                                        bmc.setMerDuplex(navi_lng + "," + navi_lat);//出入口坐标
                                    } else {
                                        bmc.setMerDuplex(lng + "," + lat);//出入口坐标
                                    }

                                    if (detailInfoJo.containsKey("type")) {
                                        String type = detailInfoJo.get("type").toString();
                                        if ("cater".equals(type)) {
                                            bmc.setMerType(1);//类型type吃
                                        } else if ("hotel".equals(type)) {
                                            bmc.setMerType(2);//住
                                            bmc.setMerBegining(sdf.parse("00:00"));
                                            bmc.setMerMoment(sdf.parse("23:59"));
                                        } else if (tags.contains("公交") || tags.contains("地铁") || tags.contains("飞机")) {
                                            bmc.setMerType(3);//行
                                            bmc.setMerBegining(sdf.parse("00:00"));
                                            bmc.setMerMoment(sdf.parse("23:59"));
                                        } else if ("scope".equals(type)) {
                                            bmc.setMerType(4);//游
                                            bmc.setMerBegining(sdf.parse("08:00"));
                                            bmc.setMerMoment(sdf.parse("20:00"));
                                        } else if ("life".equals(type)) {
                                            bmc.setMerType(5);//娱
                                        } else if ("shopping".equals(type)) {
                                            bmc.setMerType(6);//购
                                        } else {
                                            bmc.setMerType(7);//其他
                                        }
                                    } else {
                                        bmc.setMerType(-1);//类型type
                                    }
//                                        Map<String, Object> paras = new HashMap<>();
//                                        paras.put("name", jt.get("name").toString());
                                    String name = jt.get("name").toString();//名称

                                    result = captureTouristInfoService.addBdTouristsInfo(bmc);//插入数据
                                    if (result) {
                                        ResultMap.put("status", "success");
                                        ResultMap.put("msg", "数据插入成功");
                                    } else {
                                        ResultMap.put("status", "error");
                                        ResultMap.put("msg", "数据插入失败");
                                    }
//                                        } else {
//                                            System.out.println("此条数据已经存在");
//                                            continue;
//                                        }
                                } else {
                                    continue;
                                }
                                num++;
                                if (num == 100000) {
                                    Thread thread = Thread.currentThread();
                                    try {
                                        System.out.println("*********");
                                        System.out.println("进入休眠");
                                        System.out.println("*********");
                                        thread.sleep(60000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    num = 0;
                                }
                            }
                        } else {
                            System.out.println("无数据跳出");
                            break;
                        }
                    }
                }
//                }
            }
        }
        System.out.println("*************");
        System.out.println("数据采集完毕");
        System.out.println("*************");
        /*//查询所有景点数据
        List<Map<String, Object>> parkList = captureTouristInfoService.findParkInfos();
        if (!parkList.isEmpty() && parkList != null) {
            getParkDetailInfo(parkList);
        }
        System.out.println("*************");
        System.out.println("出入口坐标修改完毕");
        System.out.println("*************");*/
        return ResultMap;
    }

    /**
     * @return
     * @Author Breach
     * @Description 录入景区信息(根据输入的景点名称)
     * @Date 2019/1/17
     * @Param null
     */
    @RequestMapping("/captureParkInfo")
    @ResponseBody
    public Map<String, Object> captureParkInfo(HttpServletRequest request) throws ParseException {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> param = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        BxMerchant bmc = new BxMerchant(); //商家表实体类
        String navi_location = "";
        String merName = ""; //景点名称
        String state = request.getParameter("state"); //国家
        String province = request.getParameter("province"); //省
        String region = request.getParameter("city"); //市
        String cityId = region; //
        bmc.setCity(region);//市
        String query = request.getParameter("scenery_name"); //景区名称
        if (region != "" && region != null && region != "undefined") {
            region = captureTouristInfoService.queryCityNameById(region); //查询城市名称
        } else { //如果城市为空，则根据景点名称直接扫描
            region = captureTouristInfoService.queryCityNameById(province);
        }
        String mer_introduce = request.getParameter("merIntroduce"); //单品介绍
        param.put("region", region);
        param.put("query", query);
        param.put("city_limit", true);
        param.put("scope", 2);
        param.put("page_size", 1);
        param.put("page_num", 0);
        String url = MapResultUtils.getRequestUrl(param);//单个请求地址
        JSONObject jo = RequestUtils.sendPost(url);
        System.out.println(jo);
        if (!jo.isEmpty()) {
            if (jo.containsKey("results")) {
                JSONArray results = JSONArray.parseArray(jo.get("results").toString());
                System.out.println(results);
                if (!results.isEmpty()) {
                    //父类uid
                    String parentUid = JSONObject.parseObject(results.get(0).toString()).getString("uid");
                    merName = JSONObject.parseObject(results.get(0).toString()).getString("name"); //景点名称
                    String merAddress = JSONObject.parseObject(results.get(0).toString()).getString("address"); //详细地址
//                    province = JSONObject.parseObject(results.get(0).toString()).getString("province"); //省
//                    String city = JSONObject.parseObject(results.get(0).toString()).getString("city"); //市
                    String district = JSONObject.parseObject(results.get(0).toString()).getString("area"); //区

                    if (JSONObject.parseObject(results.get(0).toString()).containsKey("detail_info")) {
                        JSONObject detailInfo = JSONObject.parseObject(JSONObject.parseObject(results.get(0).toString()).get("detail_info").toString());
                        if (detailInfo.size() != 0 && !detailInfo.isEmpty()) {
                            if (detailInfo.containsKey("navi_location")) {
                                JSONObject navi_loca = JSONObject.parseObject(detailInfo.get("navi_location").toString());
                                navi_location = new BigDecimal(navi_loca.get("lng").toString()).setScale(6, BigDecimal.ROUND_HALF_UP)
                                        + "," + new BigDecimal(navi_loca.get("lat").toString()).setScale(6, BigDecimal.ROUND_HALF_UP); //出入口坐标
                            }

                            if (detailInfo.containsKey("type")) {
                                String type = detailInfo.get("type").toString();
                                if ("cater".equals(type)) {
                                    bmc.setMerType(1);//类型type吃
                                } else if ("hotel".equals(type)) {
                                    bmc.setMerType(2);//住
                                    bmc.setMerBegining(sdf.parse("00:00"));
                                    bmc.setMerMoment(sdf.parse("23:59"));
                                } else if ("scope".equals(type)) {
                                    bmc.setMerType(4);//游
                                    bmc.setMerBegining(sdf.parse("08:00"));
                                    bmc.setMerMoment(sdf.parse("20:00"));
                                } else if ("life".equals(type)) {
                                    bmc.setMerType(5);//娱
                                } else if ("shopping".equals(type)) {
                                    bmc.setMerType(6);//购
                                }
                            } else {
                                bmc.setMerType(-1);//类型type
                            }
                        }
                        cityId = ((cityId == "" || cityId == null) ? province : cityId);
                        //查询时区
                        List<Map<String, Object>> timezoneLists = captureTouristInfoService.queryTimezoneInfo(cityId);
                        int rawOffset = 0;
                        int dstOffset = 0;
                        String timezoneId = null;
                        if (timezoneLists.size() != 0 && !timezoneLists.isEmpty()) {
                            timezoneId = timezoneLists.get(0).get("timezoneId").toString();
                            if (timezoneLists.get(0).get("rawOffset") != "" && timezoneLists.get(0).get("rawOffset") != null) {
                                rawOffset = Integer.parseInt(timezoneLists.get(0).get("rawOffset").toString());
                                dstOffset = Integer.parseInt(timezoneLists.get(0).get("dstOffset").toString());
                            }
                        }
                        bmc.setTimezone(timezoneId);
                        bmc.setRawOffset(rawOffset);
                        bmc.setDstOffset(dstOffset);

                        bmc.setMerDuplex(navi_location); //多出入口坐标
                        bmc.setMerCentral(navi_location); //中心点坐标
                        bmc.setMerName(merName); //景点名称
                        bmc.setMerAddress(merAddress); //详细地址
                        bmc.setState(state); //国家
                        bmc.setProvince(province);//省
                        bmc.setMerBegining(sdf.parse("08:00")); //服务起始时间
                        bmc.setMerMoment(sdf.parse("20:00")); //服务结束时间

                        Map<String, Object> para = new HashMap<>();
                        para.put("state", state);
                        para.put("province", province);
                        para.put("merName", merName);
                        List<Map<String, Object>> sceneryLists = captureTouristInfoService.findCountByName(para);
                        if (sceneryLists.isEmpty() && sceneryLists.size() == 0) { //查询景点是否已经添加
                            Boolean bl = captureTouristInfoService.addBdTouristsInfo(bmc); //添加景点信息
                            if (bl) {
                                result.put("status", "success");
                                result.put("msg", "保存景点信息成功");
                            } else {
                                result.put("status", "error");
                                result.put("msg", "保存景点信息失败");
                            }
                        } else {
                            result.put("status", "fail");
                            result.put("msg", merName + "已存在，无需重复添加");
                            result.put("data", sceneryLists);
                        }
                    }
                }
            }
        } else {
            result.put("status", "error");
            result.put("msg", "未扫描到该景点数据");
            System.out.println("无数据，跳过");
        }
        System.out.println("*****************************");
        System.out.println(merName + "景点数据采集完毕");
        System.out.println("*****************************");
        return result;
    }

    /**
     * @return java.util.Map<java.lang.String   ,   java.lang.Object>
     * @Author Breach
     * @Description 查询自动添加的景点信息
     * @Date 2019/1/18
     * @Param request
     */
    @RequestMapping("/queryAddSceneryInfo")
    @ResponseBody
    public Map<String, Object> queryAddSceneryInfo(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> paras = new HashMap<>();
        String merName = request.getParameter("merName");
        merName = "%" + merName + "%";
        String state = request.getParameter("state");
        String province = request.getParameter("province");
        paras.put("state", state);
        paras.put("province", province);
        paras.put("merName", merName);
        List<Map<String, Object>> sceneryLists = captureTouristInfoService.findCountByName(paras);
        if (!sceneryLists.isEmpty() && sceneryLists.size() != 0) {
            result.put("status", "success");
            result.put("msg", "查询成功");
            result.put("data", sceneryLists);
        } else {
            result.put("status", "error");
            result.put("msg", "查询失败");
        }
        return result;
    }

    /**
     * @return java.util.Map<java.lang.String   ,   java.lang.Object>
     * @Author Breach
     * @Description 手动添加或者修改景点数据
     * @Date 2019/1/18
     * @Param
     */
    @RequestMapping("/addOrUpdateSceneryInfo")
    @ResponseBody
    public Map<String, Object> addOrUpdateSceneryInfo(HttpServletRequest request) throws ParseException {
        Map<String, Object> result = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        BxMerchant bmc = new BxMerchant();
        String merchantId = request.getParameter("merchantId");
        String merDuplex = ""; //双向出入口
        if (merchantId != "" && merchantId != null) {
            int merId = Integer.parseInt(merchantId);
            bmc.setId(merId); //景点Id
        }
        String[] mer_duplex = request.getParameterValues("mer_duplex"); //出入口坐标
        String merName = request.getParameter("scenery_name_add");
        String state = request.getParameter("state");
        String province = request.getParameter("province");
        String city = request.getParameter("city");
        String merAddress = request.getParameter("mer_address");
        String merCentral = request.getParameter("mer_central"); //中心点坐标
        Date merBegining = sdf.parse(request.getParameter("scenery_start_time"));
        Date merMoment = sdf.parse(request.getParameter("scenery_end_time"));
        String bestTime = request.getParameter("mer_best");
        int merBest = 0;
        if (bestTime != "" && bestTime != null) {
            merBest = Integer.parseInt(bestTime);
        }
        String ticketInfo = request.getParameter("ticket_info");
        String trafficInfo = request.getParameter("traffic_info");
        int merType = Integer.parseInt(request.getParameter("mer_type"));
        String merIntroduce = request.getParameter("mer_introduce");

        bmc.setMerName(merName); //景点名称
        bmc.setState(state); //国家
        bmc.setProvince(province); //省
        bmc.setCity(("null".equals(city) || city == null) ? province : city); //城市
        bmc.setMerAddress(merAddress); //详细地址
        if (mer_duplex != null && mer_duplex.length != 0) {
            for (int i = 0; i < mer_duplex.length; i++) {
                merDuplex += (mer_duplex[i] + " ");
            }
        }
        bmc.setMerDuplex(merDuplex);//双向出入口
        bmc.setMerCentral(merCentral); //中心点坐标
        bmc.setMerBegining(merBegining); //服务开始时间
        bmc.setMerMoment(merMoment); //服务结束时间
        bmc.setMerBest(merBest); //最佳游玩时间
        bmc.setTicketInfo(ticketInfo); //门票信息
        bmc.setTrafficInfo(trafficInfo); //交通信息
        bmc.setMerType(merType); //商品类型
        bmc.setMerIntroduce(merIntroduce); //商品介绍
        if (merchantId == "") { //保存新景点数据
            //查询时区
            List<Map<String, Object>> timezoneLists = captureTouristInfoService.queryTimezoneInfo(city);
            int rawOffset = 0;
            int dstOffset = 0;
            String timezoneId = null;
            if (timezoneLists.size() != 0 && !timezoneLists.isEmpty()) {
                timezoneId = timezoneLists.get(0).get("timezoneId").toString();
                if (timezoneLists.get(0).get("rawOffset") != "" && timezoneLists.get(0).get("rawOffset") != null) {
                    rawOffset = Integer.parseInt(timezoneLists.get(0).get("rawOffset").toString());
                    dstOffset = Integer.parseInt(timezoneLists.get(0).get("dstOffset").toString());
                }
            }
            bmc.setTimezone(timezoneId);
            bmc.setRawOffset(rawOffset);
            bmc.setDstOffset(dstOffset);

            boolean bl = captureTouristInfoService.addBdTouristsInfo(bmc);
            if (bl) {
                result.put("status", "success");
                result.put("msg", "保存成功");
            } else {
                result.put("status", "error");
                result.put("msg", "保存失败");
            }
        } else { //修改已有的景点数据
            int num = captureTouristInfoService.udateSceneryInfo(bmc);
            if (num > 0) {
                result.put("status", "success");
                result.put("msg", "修改成功");
            } else {
                result.put("status", "error");
                result.put("msg", "修改失败");
            }
        }

        return result;
    }

    /**
     * @return void
     * @Author Breach
     * @Description 修改景点出入口坐标
     * @Date 2019/1/17
     * @Param list
     */
/*    public void getParkDetailInfo(List<Map<String, Object>> list) {
        JSONArray ja = new JSONArray();
        ja = JSONArray.parseArray(JSON.toJSONString(list));
        String navi_location = "";
        Map<String, Object> param = new HashMap<>();
        param.put("scope", 2);
        param.put("page_size", 1);
        param.put("page_num", 0);
        for (int i = 0; i < ja.size(); i++) {
            String region = JSONObject.parseObject(ja.get(i).toString()).get("city").toString();
            String query = JSONObject.parseObject(ja.get(i).toString()).get("name").toString();
            param.put("region", region);
            param.put("query", query);
            String url = MapResultUtils.getRequestUrl(param);//单个请求地址
            System.out.println(url);
            JSONObject jo = RequestUtils.sendPost(url);
            if (!jo.isEmpty()) {
                if (jo.containsKey("results")) {

                    JSONArray results = JSONArray.parseArray(jo.get("results").toString());
                    if (!results.isEmpty()) {
                        //父类uid
                        String parentUid = JSONObject.parseObject(results.get(0).toString()).getString("uid");
                        if (JSONObject.parseObject(results.get(0).toString()).containsKey("detail_info")) {
                            JSONArray children = JSONArray.parseArray(JSONObject.parseObject(JSONObject.parseObject(results.get(0).toString()).get("detail_info").toString()).get("children").toString());
                            System.out.println(children);
                            for (int j = 0; j < children.size(); j++) {
                                JSONArray tempJa = new JSONArray();
                                JSONObject child = JSONObject.parseObject(children.get(j).toString());
                                JSONObject locationJo = JSONObject.parseObject(child.get("location").toString());
                                if (j < children.size()) {
                                    navi_location += locationJo.get("lng") + "," + locationJo.get("lat") + " ";//拼接后的出入口坐标
                                } else {
                                    navi_location += locationJo.get("lng") + "," + locationJo.get("lat");//拼接后的出入口坐标
                                }
                            }
                            Map<String, Object> para = new HashMap<>();
                            para.put("navi_location", navi_location);
                            para.put("uid", parentUid);
                            int num = captureTouristInfoService.updateNaviLocaByUid(para);
                        }
                    }
                } else {
                    continue;
                }
            } else {
                System.out.println("无数据，跳过");
            }
        }
    }*/
    public static void main(String[] args) {
        /*List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> para = new HashMap<>();
        para.put("name", "xxx");
        para.put("age", "18");
        list.add(para);
        System.out.println(list);
        System.out.println(JSON.toJSONString(list));
        JSONArray ja = JSONArray.parseArray(JSON.toJSONString(list));
        System.out.println(ja);*/

        System.out.println(ZHConverter.convert("哼,想让我簡單!沒门", ZHConverter.SIMPLIFIED));//繁体转简体
        System.out.println(ZHConverter.convert("羅利老馬路", ZHConverter.SIMPLIFIED));//繁体转简体
        System.out.println(ZHConverter.convert("哼,想让我简单!没门", ZHConverter.SIMPLIFIED));
        System.out.println(ZHConverter.convert("哼,想让我简单!没门", ZHConverter.TRADITIONAL));//简体转繁体
    }

}
