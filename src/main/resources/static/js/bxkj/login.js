$(function () {

})

/**
 * @Author Breach
 * @Description 跳转到采集页面
 * @Date 2019/2/15
 */
var flag = true //设置开关，控制点击速度
function checkLogin() {
    var username = $("#username").val() //用户名
    var password = $("#password").val() //密码

    if (flag) {
        var doc = document.createElement("div")
        doc.setAttribute("class", "remind-info")
        if (username == "" || username == null) { //未输入账号提醒
            var str = document.createTextNode("🔊 请输入账号")
            doc.appendChild(str)
            document.getElementById("remind").appendChild(doc)
            $("#username").addClass("name-remind")
            flag = false //控制点击速度开关（关闭）
            setTimeout(function () {
                $("#username").removeClass("name-remind")
                document.getElementById("remind").removeChild(doc)
                flag = true //控制点击速度开关（打开）
            }, 1000)
        } else if (password == "" || password == null) { //未输入密码提醒
            var str = document.createTextNode("🔊 请输入密码")
            doc.appendChild(str)
            document.getElementById("remind").appendChild(doc)
            flag = false //控制点击速度开关（关闭）
            $("#password").addClass("psw-remind")
            setTimeout(function () {
                $("#password").removeClass("psw-remind")
                document.getElementById("remind").removeChild(doc)
                flag = true //控制点击速度开关（打开）
            }, 1000)
        } else {
            $.ajax({
                url: "check",
                data: {
                    "username": username,
                    "password": password
                },
                type: "post",
                dataType: "json",
                success: function (data) {
                    if (data.status == "success") {
                        location.href = "index";
                    } else {
                        var str = document.createTextNode("　　　🔊 用户名或密码错误!")
                        doc.appendChild(str)
                        document.getElementById("remind").appendChild(doc)
                        $("#username").addClass("name-remind") //输入框表格样式标红提示
                        $("#password").addClass("psw-remind")
                        flag = false
                        setTimeout(function () {
                            $("#username").removeClass("name-remind")
                            $("#password").removeClass("psw-remind")
                            document.getElementById("remind").removeChild(doc)
                            $("#username").val("") //重置输入框
                            $("#password").val("") //重置输入框
                            flag = true
                        }, 1000)
                    }
                },
                error: function (e) {
                    alert(e)
                }

            })
        }
    }
}