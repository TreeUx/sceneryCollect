package com.bx.touristsinfo.model;

import lombok.Data;

import java.util.Date;

/**
 * @Description TODO
 * @Author Breach
 * @Date 2019/1/8
 * @Version V1.0
 **/
@Data
public class BxMerchant {
    private int id;
    /*省*/
    private String province;
    /*市*/
    private String city;
    /*区*/
    private String district;
    /*名称*/
    private String merName;
    /*详细地址*/
    private String merAddress;
    /*中心点坐标*/
    private String merCentral;
    /*入口坐标*/
    private String merEntrance;
    /*出口坐标*/
    private String merExit;
    /*双向出入口坐标*/
    private String merDuplex;
    /*服务开始时间*/
    private Date merBegining;
    /*服务结束时间*/
    private Date merMoment;
    /*最佳时长*/
    private int merBest;
    /*单品介绍说明*/
    private String merIntroduce;
    /*国家*/
    private String state;
    /*删除状态*/
    private int delState;
    /*创建时间*/
    private Date merTime;
    /*时区*/
    private String timezone;
    /*夏令时间偏移秒数*/
    private int dstOffset;
    /*坐标点位置时间协调世界编移秒数*/
    private int rawOffset;
    /*门票信息*/
    private String ticketInfo;
    /*交通信息*/
    private String trafficInfo;
    /*商品类型*/
    private int merType;

}
