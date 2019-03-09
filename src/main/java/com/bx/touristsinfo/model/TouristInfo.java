package com.bx.touristsinfo.model;


import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Id;

import javax.persistence.*;






/**
 * @Description TODO
 * @Author Breach
 * @Date 2018/11/15
 * @Version V1.0
 **/
@Data
//@Entity
public class TouristInfo {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    /*省*/
    private String province;
    /*市*/
    private String city;
    /*区*/
    private String regionName;
    /*名称*/
    private String name;
    /*详细地址*/
    private String address;
    /*中心点坐标*/
    private String location;
    /*出入口坐标*/
    private String naviLocation;
    /*地点uid*/
    private String uid;
    /*街景图uid*/
    private String streetId;
    /*电话号码*/
    private String phone;
    /*类型*/
    private String type;
    /*英文类型*/
    private String tag;
    /*详情页*/
    private String detailUrl;
    /*人均价格*/
    private String price;
    /*总体评分*/
    private String overallRating;
    /*父类id*/
    private int regionId;

}


