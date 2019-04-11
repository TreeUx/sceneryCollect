var data;
var map;
/*坐标转换需要的常量 Start*/
var x_PI = 3.14159265358979324 * 3000.0 / 180.0;
var PI = 3.1415926535897932384626;
var a = 6378245.0;
var ee = 0.00669342162296594323;
var transedPointStrs = "" //拼接后的原始坐标
/*坐标转换需要的常量 End*/
$(function () {
    for (var i = 0; i < GlobalCity.length; i++) { //国家
        $("#state").append(
            '<option value="' + GlobalCity[i].id + '">' + GlobalCity[i].value + '</option>'
        )
    }
    for (var i = 1; i < GlobalCity[0].child.length; i++) { //省
        $("#province").append(
            '<option value="' + GlobalCity[0].child[i].id + '">' + GlobalCity[0].child[i].value + '</option>'
        );
    }
    for (var i = 1; i < GlobalCity[0].child[1].child.length; i++) { //省
        $("#city").append(
            '<option value="' + GlobalCity[0].child[1].child[i].id + '">' + GlobalCity[0].child[1].child[i].value + '</option>'
        );
    }

    showMap($("#city").find("option:selected").text()) //初始化展示地图
    $("#state").change(queryProvinceInfo)
    $("#province").change(queryCityInfo)
    $("#city").change(function () {
        var city = $("#city").find("option:selected").text();
        showMap(city)
    })
}) /*主函数*/
/*校验时间 Start*/
function checkStartTime() {
    console.log("开始时间失焦事件")
    var title = "有效性提示"
    var start_time = $("#scenery_start_time").val()
    var end_time = $("#scenery_end_time").val()
    if(!test_7($("#scenery_start_time").val())) {
        msg = "请输入有效时间"
        showWarning(title, msg)
    } else {
        $("#scenery_end_time").val(addMin(start_time, 0))
    }
}
function checkEndTime() {
    console.log("开始时间失焦事件")
    var title = "有效性提示"
    var start_time = $("#scenery_start_time").val()
    var end_time = $("#scenery_end_time").val()
    var ck_res = validateTimePeriod(start_time, end_time);
    if(!test_7($("#scenery_end_time").val())) {
        msg = "请输入有效时间"
        showWarning(title, msg)
    } else if(!ck_res) {
        var start_t = addMin(end_time, 1)
        $("#scenery_start_time").val(start_t)
    }
}
/*校验时间 End*/

//展示地图
function showMap(city) {
    map = new BMap.Map("bx-bdmap", {enableMapClick: false});    // 创建Map实例
    map.centerAndZoom(city, 16);
    map.enableScrollWheelZoom(true);
    /*setTimeout(function () {
        console.log(map)
        console.log(map.getCenter())
        console.log("定位当前地图的中心点坐标为：" + map.getCenter().lng + "," + map.getCenter().lat)
        var marker = new BMap.Marker(new BMap.Point(map.getCenter().lng, map.getCenter().lat))
        map.addOverlay(marker)
    }, 2500)*/

    map.addEventListener("rightclick", function (e) {//设置鼠标右键点击事件
        flag = true
        console.log(e.point.lng + "," + e.point.lat);
        var lng = e.point.lng;
        var lat = e.point.lat;
        var menu = new BMap.ContextMenu(); //右键菜单
        var txtMenuItem = [//右键菜单项目
            {
                text: '添加新景点',
                callback: function () { //返回函数中进行相应逻辑操作
                    console.log('添加新景点');
                    $(".BMap_contextMenu").remove() //删除菜单项
                    var title = "新增景点";
                    addNewSceneryInfos(map, lng, lat, title) //新增景点单元窗口
                }
            },
            {
                text: '开始采集出入口',
                callback: function () {
                    console.log('开始采集出入口');
                    addClick() //添加采集出入口坐标事件
                    $(".BMap_contextMenu").remove() //删除菜单项

                }
            },
            {
                text: '完成出入口采集',
                callback: function () {
                    console.log('完成出入口采集');
                    removeClick() //移除事件
                    $(".BMap_contextMenu").remove() //删除菜单项
                }
            }];//右键菜单
        function clickFun(e) {
            alert("开始采集出入口")
            alert(e.point.lng + "," + e.point.lat)
            console.log(e)
        }

        for (var i = 0; i < txtMenuItem.length; i++) {
            menu.addItem(new BMap.MenuItem(txtMenuItem[i].text, txtMenuItem[i].callback, 100)); //菜单添加项目
        }
        map.addContextMenu(menu)
    });
}

/*添加、移除事件 Start*/
//采集出入口时间
function showInfo(e) {
    console.log(e.point.lng + ", " + e.point.lat);
    var lng = e.point.lng
    var lat = e.point.lat
    $("#addPoints").removeAttr("type"); //展示出入口坐标div
    var content =
        '<div class="row" style="padding: 7px;">' +
        '<div class="form-group">' +
        '<div class="col-sm-3" style="float: left;margin-left: -4px;">' +
        '<label for="mer_duplex_bd" class="control-label">出入口坐标：</label>' +
        '</div>' +
        '<div class="col-sm-9">' +
        '<input type="hidden" id="mer_duplex" name="mer_duplex" value="">' +
        '<input id="mer_duplex_bd" name="mer_duplex_bd" value="' + lng + "," + lat + '" style="margin-left: 10px;">'
        '</div>'
    var pointList = new Array()
    pointList.push(new BMap.Point(lng, lat)) //将百度坐标转换为原始坐标
    transToGps(pointList, "#mer_duplex") //将百度坐标转换为原始坐标
    $("#addPoints").append(content)
}
//添加鼠标点击事件
function addClick() {
    map.addEventListener("click", showInfo);
}
//移除鼠标点击事件
function removeClick() {
    map.removeEventListener("click", showInfo);
}
/*添加、移除事件 End*/


/**
 * @Author Breach
 * @Description 添加新景点函数（弹出框）
 * @Date 2018/12/25
 * @Param null
 * @return
 */
function addNewSceneryInfos(map, lng, lat, title, data, marker) {
    var mer_name = "" //景点名称
    var mer_address = "" //详细地址
    var mer_begining = "" // 服务开始时间
    var mer_moment = "" //服务结束时间
    var mer_best = "" //最佳游玩时间
    var mer_type = 4 //商品类型
    var mer_introduce = "" //单品介绍
    var ticket_info = "" //门票信息
    var traffic_info = "" //交通信息
    var merchantId = "" //景点id
    var mer_central = "" //中心点坐标
    if (data != undefined && data != null && data != "") {
        mer_name = data.mer_name
        mer_address = data.mer_address
        mer_begining = data.mer_begining
        mer_moment = data.mer_moment
        mer_best = (data.mer_best == undefined ? "" : data.mer_best)
        mer_type = data.mer_type
        ticket_info = (data.ticket_info == undefined ? "" : data.ticket_info)
        traffic_info = (data.traffic_info == undefined ? "" : data.traffic_info)
        mer_introduce = (data.mer_introduce == undefined ? "" : data.mer_introduce)
        merchantId = data.id
        mer_central = data.mer_central // 数据库取出的原始坐标
    }
    var pointList = new Array()
    pointList.push(new BMap.Point(lng, lat))
    transToGps(pointList, "#mer_central")
    //右键菜单
    var content = '<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="addModalLabel" style="margin-left: 24px;" aria-hidden="true" >' +
        '<div class="modal-dialog">' +
        '<div class="modal-content">' +
        '<div class="modal-body">' +
        '<form id="addSceneryModalForm" action="" class="form-horizontal">' +
        '<div class="row">' +
        '<div class="row" style="padding: 10px;margin-top:10px;border-top: 1px solid grey;">' +
        '<div class="form-group">' +
        '<div class="col-sm-3" style="float: left;margin-left: 30px;">' +
        '<label for="scenery_name_add" class="control-label"><span  style="color: red;"> * </span>名称：</label>' +
        '</div>' +
        '<div class="col-sm-9">' +
        '<input id="scenery_name_add" name="scenery_name_add" type="text" style="margin-left: 10px;" placeholder="请输入景点名称"/>' +
        '</div>' +
        '</div>' +
        '</div>' +
        '<div class="row" style="padding: 7px;">' +
        '<div class="form-group">' +
        '<div class="col-sm-3" style="float: left;margin-left: 30px;">' +
        '<label for="continents" class="control-label"><span  style="color: red;"> * </span>国家：</label>' +
        '</div>' +
        '<div class="col-sm-3"  style="float: left;margin-left:10px;">' +
        '<select id="state_add" name="state" class="selectpicker" style="min-width: 40px;">' +
        '</select>' +
        '</div>' +
        '<div class="col-sm-3"  style="float: left;margin-left:10px;">' +
        '<select id="province_add" name="province" class="selectpicker" style="min-width: 40px;">' +
        '</select>' +
        '</div>' +
        '<div class="col-sm-3">' +
        '<select id="city_add" name="city" style="margin-left:10px;min-width: 40px;" class="selectpicker" >' +
        '</select>' +
        '</div>' +
        '</div>' +
        '</div>' +
        '<div class="row" style="padding: 7px;">' +
        '<div class="form-group">' +
        '<div class="col-sm-3" style="float: left;">' +
        '<label for="mer_address" class="control-label"><span style="color: red;"> * </span>详细地址：</label>' +
        '</div>' +
        '<div class="col-sm-9">' +
        '<input id="mer_address" name="mer_address" type="text" value="' + mer_address + '" style="margin-left: 10px;min-width: 205px;" placeholder="请输入景区详细地址"/>' +
        '</div>' +
        '</div>' +
        '</div>' +
        '<div class="row" style="padding: 7px;">' +
        '<div class="form-group">' +
        '<div class="col-sm-3" style="float: left;margin-left: -4px;">' +
        '<label for="" class="control-label">地图经纬度：</label>' +
        '</div>' +
        '<input id="merchantId" name="merchantId" type="hidden" value="' + merchantId + '"/>' +
        '<input id="mer_central" name="mer_central" type="hidden" value="' + mer_central + '"/>' +
        '<div class="col-sm-9"><label id="location" style="margin-left: 10px;">' + lng + "," + lat + '</label></div>' +
        '</div>' +
        '</div>' +
        '<div id="addPoints" class="row" type="hidden" style="padding-top: 3px;max-height: 75px;overflow-y: auto;">' +
        '</div>' +
        '<div class="row" style="padding: 7px;">' +
        '<div class="form-group">' +
        '<div class="col-sm-3" style="float: left;">' +
        '<label for="scenery_start_time scenery_end_time" class="control-label"><span  style="color: red;"> * </span>开放时段：</label>' +
        '</div>' +
        '<div class="col-sm-9">' +
        '<input id="scenery_start_time" name="scenery_start_time" onblur="checkStartTime()" type="time" value="' + mer_begining + '" style="width: 70px;margin-left: 10px;"/> ~ &nbsp;&nbsp;&nbsp; ' +
        '<input id="scenery_end_time" name="scenery_end_time" onblur="checkEndTime()" type="time" value="' + mer_moment + '" style="width: 70px;" />' +
        '</div>' +
        '</div>' +
        '</div>' +
        '<div class="row" style="padding: 7px;">' +
        '<div class="form-group" style="margin-left: -31px;">' +
        '<div class="col-sm-3" style="float: left;">' +
        '<label for="mer_best" class="control-label">最佳游玩时间：</label>' +
        '</div>' +
        '<div class="col-sm-9">' +
        '<input id="mer_best" name="mer_best" type="text" style="margin-left: 10px;" value="' + mer_best + '" maxlength="8" placeholder="请输入游玩时间"/>' +
        '</div>' +
        '</div>' +
        '</div>' +
        '<div class="row" style="padding: 7px;">' +
        '<div class="form-group">' +
        '<div class="col-sm-3" style="float: left;">' +
        '<label for="ticket_info" class="control-label">门票信息：</label>' +
        '</div>' +
        '<div class="col-sm-9">' +
        '<input id="ticket_info" name="ticket_info" type="text" value="' + ticket_info + '" style="margin-left: 10px;" placeholder="请输入门票信息"/>' +
        '</div>' +
        '</div>' +
        '</div>' +
        '<div class="row" style="padding: 7px;">' +
        '<div class="form-group">' +
        '<div class="col-sm-3" style="float: left;margin-left: 12px;">' +
        '<label for="traffic_info" class="control-label"> 交通信息：</label>' +
        '</div>' +
        '<div class="col-sm-9">' +
        '<input id="traffic_info" name="traffic_info" type="text" value="' + traffic_info + '" style="margin-left: 10px;" placeholder="请输入交通信息"/>' +
        '</div>' +
        '</div>' +
        '</div>' +
        '<div class="row" style="padding: 7px;">' +
        '<div class="form-group">' +
        '<div class="col-sm-3" style="float: left;">' +
        '<label for="mer_type" class="control-label"><span  style="color: red;"> * </span>商品类型：</label>' +
        '</div>' +
        '<div class="col-sm-9">' +
        '<select id="mer_type" name="mer_type" class="selectpicker" style="margin-left: 10px;">' +
        '<option value="1">吃</option>' +
        '<option value="2">住</option>' +
        '<option value="3">行</option>' +
        '<option value="4" selected>游</option>' +
        '<option value="5">娱</option>' +
        '<option value="6">购</option>' +
        '</select>' +
        '</div>' +
        '</div>' +
        '</div>' +
        '<div class="row" style="padding: 7px;">' +
        '<div class="form-group">' +
        '<div class="col-sm-3" style="float: left;margin-left: 10px;">' +
        '<label for="" class="control-label">介绍说明：</label>' +
        '</div>' +
        '<div class="col-sm-9">' +
        '<textarea id="mer_introduce" name="mer_introduce" rows="3" cols="20" style="margin-left: 10px;">' + mer_introduce + '</textarea>' +
        '</div>' +
        '</div>' +
        '</div>' +
        '</div>' +
        '</form>' +
        '<div class="scenery-box-bt" style="text-align: center;margin-top: 60px;">' +
        '<input id="submitSceneryCollectInfo" type="submit" class="webuploader-container" marker="' + marker + '" clicked="0" value="保存" point="' + lng + "," + lat + '" onclick="submitSceneryInfo(this);">' +
        '</div>' +
        '<div style="clear: both;"></div>' +
        '</div>' +
        '</div>' +
        '</div>' +
        '</div>'
    var infoWindow = new BMap.InfoWindow(content, {
        offset: new BMap.Size(0, 0), //设置弹窗偏移量
        width: 420, //设置弹窗宽度
        height: 620, //取值范围：0, 220 - 730。如果您指定宽度为0，则信息窗口的宽度将按照其内容自动调整
        enableCloseOnClick: false //是否开启点击地图关闭信息窗口（默认开启）
        // title: "新增景点"
    }); //创建信息窗口对象
    infoWindow.setTitle(title)
    var point = new BMap.Point(lng, lat);
    map.openInfoWindow(infoWindow, point);
    // console.log(infoWindow.getTitle())  //返回弹出窗标题
    // console.log(infoWindow.isOpen())

    setTimeout(function () { //监听右键菜单框打开事件
        if (infoWindow.isOpen()) {
            $("#scenery_start_time").val("08:00")
            $("#scenery_end_time").val("20:00")
            if(mer_name != "") {
                $("#scenery_name_add").val(mer_name)
            }
            $("#state_add").append( //初始化添加框中国家下拉选
                '<option value="' + $("#state").val() + '">' + $("#state").find("option:selected").text() + '</option>'
            );
            $("#province_add").append( //初始化添加框中省份下拉选
                '<option value="' + $("#province").val() + '">' + $("#province").find("option:selected").text() + '</option>'
            );
            $("#city_add").append( //初始化添加框中城市下拉选
                '<option value="' + $("#city").val() + '">' + $("#city").find("option:selected").text() + '</option>'
            );
            $("#mer_type").val(mer_type); //初始化商品类型下拉选

            /*转换坐标点 Start*/
            var everPoint = $("#location").text()
            console.log("采集点的坐标：" + everPoint)
            var x1 = everPoint.substring(0, everPoint.indexOf(","))
            var y1 = everPoint.substring(everPoint.indexOf(",") + 1, everPoint.length)
            var tempPoint = {}
            var pointArr = new Array()
            tempPoint.lng = x1
            tempPoint.lat = y1
            pointArr.push(tempPoint);
            var convertor = new BMap.Convertor();
            if (title == "新增景点") {
                convertor.translate(pointArr, 1, 5, function (data) { //此处录入景点单元信息时，把百度坐标系转换为原始坐标系
                    if (data.status === 0) {
                        point = data.points[0]
                        var x2 = point.lng
                        var y2 = point.lat
                        var x = 2 * x1 - x2
                        var y = 2 * y1 - y2
                        x = x.toFixed(10)
                        y = y.toFixed(10)
                        $("#scenery_location").val(x + "," + y)
                    }
                })
            }
            /*转换坐标点 End*/
        }
    }, 50);
}

//百度坐标转换为原始坐标
var convertor = new BMap.Convertor()
function transToGps(pointList, $name) {
    convertor.translate(pointList, 5, 3, function (data) {
        console.log(data)
        if (data.status === 0) {
            transedPointStrs = "" //拼接后的原始坐标
            for (var k = 0; k < pointList.length; k++) {
                var x2 = data.points[k].lng
                var y2 = data.points[k].lat
                var tempPoint = gcj02towgs84(x2, y2)
                var x = tempPoint.lng
                var y = tempPoint.lat
                console.log(x + "," + y)
                transedPointStrs = transedPointStrs + (x.toFixed(10) + "," + y.toFixed(10) + " ")  //拼接转换后的坐标
                // $("#mer_central").val(transedPointStrs) //设置隐藏input为原始坐标
                $($name).val(transedPointStrs) //设置隐藏input为原始坐标
            }
        }
    })
}
/*GCJ02转WGS84坐标系 Start*/
function gcj02towgs84(lng, lat) {
    var transedpoint = {}
    if (out_of_china(lng, lat)) {
        return [lng, lat]
    }
    else {
        var dlat = transformlat(lng - 105.0, lat - 35.0);
        var dlng = transformlng(lng - 105.0, lat - 35.0);
        var radlat = lat / 180.0 * PI;
        var magic = Math.sin(radlat);
        magic = 1 - ee * magic * magic;
        var sqrtmagic = Math.sqrt(magic);
        dlat = (dlat * 180.0) / ((a * (1 - ee)) / (magic * sqrtmagic) * PI);
        dlng = (dlng * 180.0) / (a / sqrtmagic * Math.cos(radlat) * PI);
        mglat = lat + dlat;
        mglng = lng + dlng;
        transedpoint.lng = lng * 2 - mglng
        transedpoint.lat = lat * 2 - mglat
        return transedpoint
    }
}

function transformlat(lng, lat) {
    var ret = -100.0 + 2.0 * lng + 3.0 * lat + 0.2 * lat * lat + 0.1 * lng * lat + 0.2 * Math.sqrt(Math.abs(lng));
    ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
    ret += (20.0 * Math.sin(lat * PI) + 40.0 * Math.sin(lat / 3.0 * PI)) * 2.0 / 3.0;
    ret += (160.0 * Math.sin(lat / 12.0 * PI) + 320 * Math.sin(lat * PI / 30.0)) * 2.0 / 3.0;
    return ret
}

function transformlng(lng, lat) {
    var ret = 300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 0.1 * lng * lat + 0.1 * Math.sqrt(Math.abs(lng));
    ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
    ret += (20.0 * Math.sin(lng * PI) + 40.0 * Math.sin(lng / 3.0 * PI)) * 2.0 / 3.0;
    ret += (150.0 * Math.sin(lng / 12.0 * PI) + 300.0 * Math.sin(lng / 30.0 * PI)) * 2.0 / 3.0;
    return ret
}

//判断是否在国内，不在国内则不做偏移
function out_of_china(lng, lat) {
    return (lng < 72.004 || lng > 137.8347) || ((lat < 0.8293 || lat > 55.8271) || false);
}

/*GCJ02转WGS84坐标系 End*/


//采集目的地数据
var capturing;
function captureSceneryInfo() {
    $("#capture_input").val("采集中...")
    $("#capture_input").addClass("capture-bt");
    capturing = setInterval(function () {
        $("#capture_input").fadeOut(800)
        $("#capture_input").fadeIn(1000)
    }, 2000)
    $.ajax({
        url: "captureSceneryInfo",
        type: "post",
        data: $("#captureSceneryInfoForm").serialize(),
        dataType: "json",
        success: function (data) {
            if (data.status == "success") {
                console.log(data)
                removeTeriminiAnimation() //去除动态效果
            } else {
                removeTeriminiAnimation()
                showSuccessOrErrorModal(data.msg, "error");
            }
        },
        error: function (e) {
            removeTeriminiAnimation()
            showSuccessOrErrorModal("网络异常！", "error");
        }
    });
}

//去掉动态效果
function removeTeriminiAnimation() {
    $("#capture_input").val("采集完毕")
    window.clearInterval(capturing)
    $("#capture_input").removeClass("capture-bt");
    setTimeout(function () {
        $("#capture_input").val("目的地采集")
    }, 1500)
}

//直接输入景点数据采集
var capturing1;

function captureParkInfo() {
    var state = $("#state").find("option:selected").text() //国家
    var stateId = $("#state").val() //国家id
    var province = $("#province").find("option:selected").text() //省
    var provinceId = $("#province").val() //省id
    var city = $("#city").find("option:selected").text() //市
    var cityId = $("#city").val() //市id
    var scenery_name = $("#scenery_name").val() //景点名称
    if (scenery_name != "") { //根据搜索的景区信息展示相应的出入口标记及线路
        showMap((city == "" ? province : city) + scenery_name) //展示景点地图
        $("#capture_scenery_input").val("采集中...")
        $("#capture_scenery_input").addClass("capture-bt");
        capturing1 = setInterval(function () {
            $("#capture_scenery_input").fadeOut(800)
            $("#capture_scenery_input").fadeIn(1000)
        }, 2000)
        $.ajax({
            url: "captureParkInfo",
            type: "post",
            data: $("#captureSceneryInfoForm").serialize(),
            dataType: "json",
            success: function (data) {
                if (data.status == "success") {
                    console.log(data)
                    $("#capture_scenery_input").val("采集完毕")
                    removeAnimation() // 去除动态效果
                    setTimeout(function () {
                        queryAddSceneryInfo(stateId, provinceId, scenery_name) //展示添加框
                    }, 500)
                } else if (data.status == "fail") { //景点数据已存在
                    $("#capture_scenery_input").val("采集完毕")
                    removeAnimation()  // 去除动态效果
                    data = $.parseJSON(JSON.stringify(data)).data[0]
                    console.log(data)
                    var mer_central = data.mer_central
                    var lng = mer_central.substring(0, mer_central.indexOf(","))
                    var lat = mer_central.substring(mer_central.indexOf(",") + 1, mer_central.length)
                    title = "新增景点"
                    addNewSceneryInfos(map, lng, lat, title, data)
                } else {
                    $("#capture_scenery_input").val("采集完毕")
                    removeAnimation()
                    showSuccessOrErrorModal(data.msg, "error");
                }
            },
            error: function (e) {
                $("#capture_scenery_input").val("采集完毕")
                removeAnimation()
                showSuccessOrErrorModal("网络异常！", "error");
            }
        });
    } else {
        alert("请输入景点名称进行采集")
    }
}

function removeAnimation() {
    window.clearInterval(capturing1)
    $("#capture_scenery_input").removeClass("capture-bt");
    setTimeout(function () {
        $("#capture_scenery_input").val("景区定点采集")
    }, 1500)
}

//查询自动录入的景点信息
function queryAddSceneryInfo(state, province, scenery_name) {
    debugger
    var state = state
    var province = province
    var scenery_name = scenery_name
    $.ajax({
        url: "queryAddSceneryInfo",
        type: "get",
        data: {
            "state": state,
            "province": province,
            "merName": scenery_name
        },
        dataType: "json",
        success: function (data) {
            if (data.status == "success") {
                debugger
                data = $.parseJSON(JSON.stringify(data)).data[0]
                var mer_central = data.mer_central
                var lng = mer_central.substring(0, mer_central.indexOf(","))
                var lat = mer_central.substring(mer_central.indexOf(",") + 1, mer_central.length)
                title = "新增景点"
                addNewSceneryInfos(map, lng, lat, title, data)
            } else {
                debugger
            }
        },
        error: function () {
            showSuccessOrErrorModal("网络异常！", "error");
        }
    })
}

//提交
function submitSceneryInfo(e) {//lng, lat为当前点击的点的经纬度坐标
    var objReg = /^[0-9]+$/;  //正则判断最佳游玩时间是否为正整数
    title = "必填项提示"
    //表单验证
    if ($("#scenery_name_add").val() == "") {//景点名称
        msg = "请输入景点名称"
        showWarning(title, msg)
    } else if ($("#state_add").val() == "") {//国家
        msg = "请选择国家"
        showWarning(title, msg)
    } else if ($("#mer_address").val() == "") {//详细地址
        msg = "请输入详细地址"
        showWarning(title, msg)
    } else if ($("#scenery_start_time").val() == "") {//服务开始时间
        msg = "请输入服务开始时间"
        showWarning(title, msg)
    } else if ($("#scenery_end_time").val() == "") {//服务结束时间
        msg = "请输入服务结束时间"
        showWarning(title, msg)
    } else if ($("#mer_type").val() == "") {//商品类型
        msg = "请选择商品类型"
        showWarning(title, msg)
    } /*else if (!objReg.test($("#mer_best").val())) {//最佳游玩时间
        msg = "请输入有效游玩时间"
        showWarning(title, msg)
    } */else { //提交表单
        console.log("提交表单")
        saveNewSceneryInfo(e)// 调用新增(修改)函数
    }
}

//保存修改景点数据
function saveNewSceneryInfo(e) {
    $.ajax({
        url: "addOrUpdateSceneryInfo",
        type: "post",
        data: $("#addSceneryModalForm").serialize(),
        dataType: "json",
        success: function (data) {
            if (data.status == "success") {
                console.log(data)
                map.closeInfoWindow()
                showSuccessOrErrorModal(data.msg, "success");//保存成功后，需要添加一个标记点
            } else {
                showSuccessOrErrorModal(data.msg, "error");
            }
        },
        error: function (e) {
            showSuccessOrErrorModal("网络异常！", "error");
        }
    });
}

//采集全球数据
// function captureGlobalSceneryInfo() {
//     $("#capture_global_input").val("采集中...")
//     $.ajax({
//         url: "captureSceneryInfo",
//         type: "post",
//         data: {"type": "global"},
//         dataType: "json",
//         success: function (data) {
//             if (data.status == "success") {
//                 console.log(data)
//                 $("#capture_input").val("采集完毕")
//             } else {
//                 showSuccessOrErrorModal(data.msg, "error");
//             }
//         },
//         error: function (e) {
//             showSuccessOrErrorModal("网络异常！", "error");
//         }
//     });
// }

//根据选择的国家查询省份下拉选信息
function queryProvinceInfo() {
    var state = $("#state").val()
    for (var i = 0; i < GlobalCity.length; i++) {
        if (state == GlobalCity[i].id) {
            $("#province").html("")
            if (GlobalCity[i].child != undefined) {
                for (var j = 1; j < GlobalCity[i].child.length; j++) {
                    $("#province").append(
                        '<option value="' + GlobalCity[i].child[j].id + '">' + GlobalCity[i].child[j].value + '</option>'
                    );
                }
                queryCityInfo()
                break
            } else {
                $("#city").html("")
            }
        }
    }
}

//根据选择的省份信息查询城市下拉选信息
function queryCityInfo() {
    var state = $("#state").val()
    var province = $("#province").val()
    for (var i = 0; i < GlobalCity.length; i++) {
        if (state == GlobalCity[i].id) {
            for (var j = 0; j < GlobalCity[i].child.length; j++) {
                if (province == GlobalCity[i].child[j].id) {
                    $("#city").html("")
                    if (GlobalCity[i].child[j].child != undefined) {
                        for (var k = 1; k < GlobalCity[i].child[j].child.length; k++) {
                            $("#city").append(
                                '<option value="' + GlobalCity[i].child[j].child[k].id + '">' + GlobalCity[i].child[j].child[k].value + '</option>'
                            );
                        }
                    }
                    var city = $("#city").find("option:selected").text();
                    var province = $("#province").find("option:selected").text();
                    var state = $("#state").find("option:selected").text();
                    console.log(city == "")
                    city = city == "" ? (province == "" ? state : province) : city
                    showMap(city)
                    break
                }
            }
        }
    }
}

// 搜索展示地图
function showMapInfo() {
    var state = $("#state").find("option:selected").text()
    var province = $("#province").find("option:selected").text()
    var city = $("#city").find("option:selected").text()
    showMap((city == "" ? (province == "" ? state : province) : city) + $("#scenery_name").val())
}

function showSuccessOrErrorModal(msg, info) {
    swal({
        title: "操作提示",  //弹出框的title
        text: msg,   //弹出框里面的提示文本
        type: "success" == info ? "success" : "error", //弹出框类型
        // showCancelButton: true, //是否显示取消按钮
        confirmButtonColor: "#DD6B55",//确定按钮颜色
        // cancelButtonText: "取消",//取消按钮文本
        confirmButtonText: "确认",//确定按钮上面的文档
        closeOnConfirm: true
    });
}

//必填项提示框信息
function showWarning(title, msg) {
    $.alert({
        title: title,
        useBootstrap: true,
        // theme: 'supervan',
        content: msg
    });
}

/*百度坐标系转换为原始坐标系 Start*/
function transedBdPoiToGpsPoi() {
    var bd_poi = $("#bd_point").val() //要转换的百度坐标
    var convertor = new BMap.Convertor()
    var x = bd_poi.substring(0, bd_poi.indexOf(",")) //百度坐标系经度
    var y = bd_poi.substring(bd_poi.indexOf(",") + 1, bd_poi.length) //百度坐标系纬度
    var ggpoint = new BMap.Point(x, y)
    var pointArr = new Array()
    pointArr.push(ggpoint)
    convertor.translate(pointArr, 5, 3, function (data) { //百度坐标系转换为国测局坐标系
        console.log(data)
        var x1 = data.points[0].lng
        var y1 = data.points[0].lat
        var wgsPoint = gcj02towgs84(x1, y1)
        console.log("百度坐标系转换为国测局（原始坐标系）坐标系 Start")
        console.log(wgsPoint)
        console.log(wgsPoint.lng.toFixed(10) + "," + wgsPoint.lat.toFixed(10))
        console.log("百度坐标系转换为国测局（原始坐标系）坐标系 End")
        $("#gps_point").val(wgsPoint.lng.toFixed(10) + "," + wgsPoint.lat.toFixed(10))
    })
}
/*百度坐标系转换为原始坐标系 End*/

//校验时间格式有效性
function test_7(time) {
    var timeRegex_2 = new RegExp("([0-1]?[0-9]|2[0-3]):([0-5][0-9])$");
    var b_3 = timeRegex_2.test(time);
    console.log(b_3)
    return b_3;
}

// 查询校验,校验起始时间是否小于截至时间
function validateTimePeriod(begin, end) { //begin：开始时间, end：结束时间
    var b_len = begin.length
    var b_index = begin.indexOf(":")
    var b_t_h = begin.substring(0, b_index) // 时
    var b_t_m = begin.substring(b_index+1, b_len) // 分
    var e_len = end.length
    var e_index = end.indexOf(":")
    var e_t_h = end.substring(0, e_index) // 时
    var e_t_m = end.substring(e_index+1, e_len) // 分
    if(begin == end) {
        return false;
    } else if(parseInt(b_t_h) > parseInt(e_t_h) || (parseInt(b_t_h) == parseInt(e_t_h) && parseInt(b_t_m) > parseInt(e_t_m))) {
        if("00:00" == end) {
            return true;
        } else {
            return false;
        }
    }
    return true;
}
// 操作结束时间大于开始时间
function addMin(time_str, type) {
    var len = time_str.length
    var index = time_str.indexOf(":")
    var t_h = time_str.substring(0, index) // 时
    var t_h_f = time_str.substring(0, 1)
    var t_h_e = time_str.substring(1, index)
    var t_m = time_str.substring(index+1, len) // 分
    var t_m_f = time_str.substring(index+1, index+2)
    var t_m_e = time_str.substring(index+2, len)
    if(type == 0) {
        if(time_str == "23:59") {
            time_str = "00:00"
        } else if(t_m == "59") {
            time_str = (parseInt(t_h)+1)+":00"
        } else if(t_m_e == "9") {
            time_str = t_h + ":"+(parseInt(t_m_f)+1)+"0"
        } else {
            time_str = t_h + ":" + t_m_f + (parseInt(t_m_e)+1)
        }
    } else {
        if(time_str == "00:00") {
            time_str = "23:59"
        } else if(t_m == "00") {
            console.log(parseInt(t_h)-1)
            console.log(parseInt(t_h)-1 < 10)
            console.log("0"+(parseInt(t_h)-1))
            console.log(parseInt(t_h)-1 < 10 ? ("0"+(parseInt(t_h)-1)) : parseInt(t_h)-1)
            time_str = (parseInt(t_h)-1 < 10 ? ("0"+(parseInt(t_h)-1)) : parseInt(t_h)-1)+":59"
        } else if(t_m_e == "0") {
            time_str = t_h + ":"+(parseInt(t_m_f)-1)+"9"
        } else {
            time_str = t_h + ":" + t_m_f + (parseInt(t_m_e)-1)
        }
    }
    return time_str
}