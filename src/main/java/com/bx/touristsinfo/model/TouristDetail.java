package com.bx.touristsinfo.model;

import lombok.Data;

import javax.persistence.*;

/**
 * @Description 景点详情表
 * @Author Breach
 * @Date 2018/12/11
 * @Version V1.0
 **/
@Data
@Entity
public class TouristDetail {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    /*交通出行信息*/
    @Column(columnDefinition = "TEXT")//设置字段类型为text类型
    private String traffic_info;
    /*景点地址*/
    private String address;
    /*国家*/
    private String nation;
    /*省*/
    private String province;
    /*城市*/
    private String city;
    /*区县*/
    private String area;
    /*景点名称*/
    private String name;
    /*人均消费*/
    private String avg_cost;
    /*最佳游玩时间*/
    private String best_season;
    /*建议游玩时间*/
    private String suggest_play_time;
    /*开放时间*/
    private String open_time;
    /*景区排名*/
    private String jounery_rank;
    /*友情提示*/
    private String friendship_tips;
    /*更多介绍*/
    @Column(columnDefinition = "TEXT")//设置字段类型为text类型
    private String more_desc;
    /*联系电话*/
    private String phone;
    /*地图坐标信息*/
    private String location;
    /*综合评分*/
    private String main_score;
    /*大家印象描述*/
    @Column(columnDefinition = "TEXT")
    private String impression;
    /*详情页面图片*/
    @Column(columnDefinition = "TEXT")
    private String detail_images;
    /*门票信息*/
    private String ticketInfo;
    /*历史游玩人数*/
    private int going_count;
    /*签证难易度*/
    private String visa_difficulty;
    /*官网*/
    private String web_site;
}
