package com.bx.touristsinfo.utils;

import com.alibaba.fastjson.JSONArray;

import java.io.*;

public class ReadTextUtils {
    /**
     * @Author Breach
     * @Description 读txt文件
     * @Date 2018/11/1
     * @Param filePath
     * @return void
     */
    public static JSONArray readText(String filePath) {
        String text = null;
        BufferedReader br = null;
        JSONArray jr = new JSONArray();
        try {
            File file = new File(filePath);
            if(file.isFile() && file.exists()) {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
                while((text = br.readLine()) != null) {
                    text = text.trim();
                    jr.fluentAdd(text);
                }
//                br.close();

            } else {
                System.out.println("文件不存在");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return jr;
    }
}

  