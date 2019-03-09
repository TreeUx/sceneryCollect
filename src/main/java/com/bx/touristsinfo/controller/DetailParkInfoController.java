package com.bx.touristsinfo.controller;

import com.alibaba.fastjson.JSONObject;
import com.bx.touristsinfo.common.HanyupinyinHelper;
import com.bx.touristsinfo.common.MapConstant;
import com.bx.touristsinfo.common.TouristsCommon;
import com.bx.touristsinfo.model.TouristDetail;
import com.bx.touristsinfo.services.DetailParkInfoService;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 获取全球景点详情信息
 * @Author Breach
 * @Date 2018/12/11
 * @Version V1.0
 **/
@RestController
@RequestMapping("/bx")
public class DetailParkInfoController {
    @Autowired
    DetailParkInfoService detailParkInfoService;

    @RequestMapping("/detail")
    public Map<String, Object> getDetailInfo() {
        Map<String, Object> result = new HashMap<>();
        List<String> parkNameList = detailParkInfoService.findParkNameInfo();
        for(int i = 0; i < parkNameList.size(); i++) {
//            String suffixUrl = "伦敦";//测试数据
            String suffixUrl = parkNameList.get(i)
                    .replace("省", "")
                    .replace("市", "")
                    .replace("区", "")
                    .replace("县", "");
            suffixUrl = new HanyupinyinHelper().toHanyuPinyin(suffixUrl);//将景区名称汉语转换为英文全拼
            Document doc = new TouristsCommon().getHtmlInfo(suffixUrl);//获取网页html页面的body
            TouristDetail touristDetail = getLYInfo(doc);
            int num = detailParkInfoService.saveDetailInfo(touristDetail);//保存数据到tourist_detail表中
            if(num != 0) {
                result.put("status", "success");
                result.put("msg", "操作成功");
            } else {
                result.put("status", "error");
                result.put("msg", "操作失败");
            }
        }
        return result;
    }

    /**
      * @Author Breach
      * @Description 解析页面获取数据
      * @Date 2018/12/11
      * @Param doc
      * @return com.alibaba.fastjson.JSONObject
      */
    public TouristDetail getLYInfo(Document doc) {
        TouristDetail touristDetail = new TouristDetail();
        String imgs = "";//图片拼接
        Elements a_s = doc.body().getElementsByClass("dest-crumbs").get(0).select("a");//标题处
        Elements li_s = doc.body().getElementById("J_pic-slider").select("li");//图片处
        int a_size = a_s.size();
        int li_size = li_s.size();
        for(int i = 0; i < li_size; i++) {
            String img = li_s.get(i).select("img").attr("src");
            int imgLenth = img.length();
            int index = img.indexOf("src=");
            img = img.substring(index + 4, imgLenth);
            imgs += img + (i==li_size-1?"":",");//图片
        }
        String nation = "";//国家
        String province = "";//省
        String city = "";//市
        String area = "";//区县
        String name = "";//景点名称
        nation = a_s.get(2).text().trim();
        switch (a_size) {
            case 4:
                province = a_s.get(3).text().trim();
                break;
            case 5:
                province = a_s.get(3).text().trim();
                city = a_s.get(4).text().trim();
                break;
            case 6:
                province = a_s.get(3).text().trim();
                city = a_s.get(4).text().trim();
                area = a_s.get(5).text().trim();
                break;
            case 7:
                province = a_s.get(3).text().trim();
                city = a_s.get(4).text().trim();
                area = a_s.get(5).text().trim();
                name = a_s.get(6).text().trim();
                break;
        }

        doc.getElementsByClass("remark-count").remove();//删除不需要采集的a标签text元素
        String main_score = doc.getElementsByClass("main-score").text().toString();//评分
        String jouneryRank = doc.body().getElementsByClass("point-rank").text();//景点排名
        String visa_difficulty = "";//签证难易度
        System.out.println(doc.getElementsByClass("main-intro").select("span").hasClass("main-out"));
        if(doc.getElementsByClass("main-intro").select("span").hasClass("main-out")) {
            visa_difficulty = doc.getElementsByClass("main-out").text().toString().replace("签证难度：", "").trim();//签证难易度
        }
        String data = doc.body().select("script").toString();
        int startSub = data.indexOf("ext:{");
        int endSub = data.indexOf("});", startSub);
        JSONObject dataJo = JSONObject.parseObject(data.substring(startSub+4,endSub).trim().toString());//获取存放描述的信息集合
        String impression = dataJo.get("impression").toString().trim();//大家印象
        String more_desc = dataJo.get("more_desc").toString().trim();//更多描述
        String map_info = dataJo.get("map_info").toString().trim();//地图坐标
        String address = dataJo.get("address").toString().trim();//地址
        String phone = dataJo.get("phone").toString().trim();//联系电话
        String web_site = "";
        if(dataJo.containsKey("website")) {
            web_site = dataJo.get("website").toString().trim();//官网
        }
        String avg_cost = "";
        if(dataJo.containsKey("avg_cost")) {
            avg_cost = dataJo.get("avg_cost").toString().trim();//人均消费
        }
        String going_count = "";
        if(dataJo.containsKey("going_count")) {
            going_count = dataJo.get("going_count").toString().trim();//访问人数
        }

        String friendshipTips = "";//友情贴士
        if(doc.body().getElementsByClass("main-point-guide-wrap").toString().length() != 0) {
            friendshipTips = doc.body().getElementsByClass("main-point-guide-wrap").select(".list-content").get(1).text().trim();
        }
        int scen_start = data.indexOf("define('open_time_desc',{text:\"\"});");
        int scen_start_len = "define('open_time_desc',{text:\"\"});".trim().length();
        int scen_temp_len = data.indexOf("define('open_time_desc',{", scen_start + scen_start_len);
        int scen_end = data.indexOf("});", scen_temp_len);
        String scenStr = data.substring(scen_start + scen_start_len, scen_end + 4).trim();

        int ticket_index = scenStr.indexOf("define('ticket_info',{text:");//门票下标
        ticket_index = ticket_index + "define('ticket_info',{text:".length();
        int traffic_index = scenStr.indexOf("define('traffic',{text:");//交通下标
        traffic_index = traffic_index + "define('traffic',{text:".length();
        int bestvisittime_index = scenStr.indexOf("define('bestvisittime',{text:");//游玩时间下标
        bestvisittime_index = bestvisittime_index + "define('bestvisittime',{text:".length();
        int besttime_index = scenStr.indexOf("define('besttime',{text:");//最佳季节下标
        besttime_index = besttime_index + "define('besttime',{text:".length();
        int open_time_desc_index = scenStr.indexOf("define('open_time_desc',{text:");//开放时间下标
        open_time_desc_index = open_time_desc_index + "define('open_time_desc',{text:".length();

        String ticket_info = scenStr.substring(ticket_index, scenStr.indexOf("});", ticket_index)).trim();//门票信息
        ticket_info = "null".equals(ticket_info) ? "" : ticket_info.substring(1, ticket_info.length() - 1);
        JSONObject ticketJo = JSONObject.parseObject("{\"ticket_info\":\""+ ticket_info + "\"}");
        String trafficInfo = scenStr.substring(traffic_index, scenStr.indexOf("});", traffic_index)).trim();//交通出行路线
        trafficInfo = "null".equals(trafficInfo) ? "" : trafficInfo.substring(1, trafficInfo.length() - 1);
        JSONObject trafficJo = JSONObject.parseObject("{\"trafficInfo\":\""+ trafficInfo + "\"}");
        String suggestPlayTime = scenStr.substring(bestvisittime_index, scenStr.indexOf("});", bestvisittime_index));//建议游玩时间
        suggestPlayTime = "null".equals(suggestPlayTime ) ? "" : suggestPlayTime.substring(1, suggestPlayTime.length() - 1);
        JSONObject suggestJo = JSONObject.parseObject("{\"suggestPlayTime\":\""+ suggestPlayTime + "\"}");
        String bestSeason = scenStr.substring(besttime_index, scenStr.indexOf("});", besttime_index));//最佳季节
        bestSeason = "null".equals(bestSeason) ? "" : bestSeason.substring(1, bestSeason.length() - 1);
        JSONObject seasonJo = JSONObject.parseObject("{\"bestSeason\":\""+ bestSeason + "\"}");//防止单斜杠被转译成双斜杠
        String open_time_desc = scenStr.substring(open_time_desc_index, scenStr.indexOf("});", open_time_desc_index));//开放时间
        open_time_desc = "null".equals(open_time_desc) ? "" : open_time_desc.substring(1, open_time_desc.length() - 1);
        JSONObject openJo = JSONObject.parseObject("{\"open_time_desc\":\""+ open_time_desc + "\"}");
//        jo.put("nation", nation);//国家
        touristDetail.setNation(nation);//国家
//        jo.put("province", province);//省
        touristDetail.setProvince(province);//省
//        jo.put("city", city);//市
        touristDetail.setCity(city);//市
//        jo.put("area", area);//区县
        touristDetail.setArea(area);//区县
//        jo.put("name", name);//景点名称
        touristDetail.setName(name);//景点名称
//        jo.put("main_score", main_score);//评分
        touristDetail.setMain_score(main_score);//评分
//        jo.put("impression", impression);//大家印象评价
        touristDetail.setImpression(impression);//大家印象
//        dataJo.put("detail_images", imgs);//详情页图片
        touristDetail.setDetail_images(imgs);//详情页图片
//        jo.put("more_desc", more_desc);//更多描述
        touristDetail.setMore_desc(more_desc);//更多描述
//        jo.put("location", map_info);//地图坐标
        touristDetail.setLocation(map_info);//地图坐标
//        jo.put("traffic_info", trafficJo.get("trafficInfo"));//交通出行方式
        touristDetail.setTraffic_info(trafficJo.get("trafficInfo").toString());//交通出行方式
//        jo.put("best_season", seasonJo.get("bestSeason"));//最佳季节
        touristDetail.setBest_season(seasonJo.get("bestSeason").toString());//最佳季节
//        jo.put("suggest_play_time", suggestJo.get("suggestPlayTime"));//建议游玩时间
        touristDetail.setSuggest_play_time(suggestJo.get("suggestPlayTime").toString());//建议游玩时间
//        jo.put("ticketInfo", ticketJo.get("ticket_info"));//门票信息
        touristDetail.setTicketInfo(ticketJo.get("ticket_info").toString());//门票信息
//        jo.put("open_time", openJo.get("open_time_desc"));//开放时间
        touristDetail.setOpen_time(openJo.get("open_time_desc").toString());//开放时间
//        jo.put("address", address);//景点地址
        touristDetail.setAddress(address);//景点地址
//        jo.put("avg_cost", avg_cost);//评分
        touristDetail.setAvg_cost(avg_cost);//评分
//        jo.put("going_count", going_count);//历史游玩人数
        touristDetail.setGoing_count(Integer.parseInt(going_count));//历史游玩人数
//        jo.put("jounery_rank", jouneryRank);//景点排名
        touristDetail.setJounery_rank(jouneryRank);//景点排名
//        jo.put("phone", phone);//联系电话
        touristDetail.setPhone(phone);//联系电话
//        jo.put("web_site", web_site);//官网
        touristDetail.setWeb_site(web_site);//官网
//        jo.put("visa_difficulty", visa_difficulty);//签证难度
        touristDetail.setVisa_difficulty(visa_difficulty);//签证难易度
//        jo.put("friendship_tips", friendshipTips);//友情贴士
        touristDetail.setFriendship_tips(friendshipTips);//友情提示
        System.out.println(touristDetail);
        System.out.println("*************** 分割线 *****************");

        return touristDetail;
    }

    public static void main(String[] args) {
        String suffixUrl = "伦敦";//测试数据（如：望江木屋温泉）
        suffixUrl = new HanyupinyinHelper().toHanyuPinyin(suffixUrl);
        System.out.println(suffixUrl);
        Document doc = new TouristsCommon().getHtmlInfo(suffixUrl);
        System.out.println(doc);
        TouristDetail touristDetail = new DetailParkInfoController().getLYInfo(doc);
    }
}
