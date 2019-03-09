package com.bx.touristsinfo.common;

import com.alibaba.fastjson.JSONArray;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * @Description TODO
 * @Author Breach
 * @Date 2018/12/11
 * @Version V1.0
 **/
public class TouristsCommon {

    /**
      * @Author Breach
      * @Description 解析页面
      * @Date 2018/12/11
      * @Param suffixUrl
      * @return void
      */
    public Document getHtmlInfo(String suffixUrl) {
        JSONArray ja = new JSONArray();
        /**HtmlUnit请求web页面*/
        WebClient wc = new WebClient(BrowserVersion.CHROME);
        wc.getOptions().setJavaScriptEnabled(true); //启用JS解释器，默认为true
        wc.getOptions().setCssEnabled(false); //禁用css支持
        wc.getOptions().setThrowExceptionOnScriptError(false); //js运行错误时，是否抛出异常
        wc.getOptions().setTimeout(30000); //设置连接超时时间 ，这里是30S。如果为0，则无限期等待
        wc.getOptions().setUseInsecureSSL(true);//允许绕过SSL认证
        wc.setAjaxController(new NicelyResynchronizingAjaxController());//设置支持ajax异步

        HtmlPage page = null;
        Document doc = null;
        try {
            String url = MapConstant.BAIDU_LY + suffixUrl;//拼接的要收集的页面的请求地址
            page = wc.getPage(url);
            wc.waitForBackgroundJavaScript(60 * 1000);//等待JS驱动dom完成获得还原后的网页
            String pageXml = page.asXml(); //以xml的形式获取响应文本
            /**jsoup解析文档*/
            doc = Jsoup.parse(pageXml);

            wc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }
}
