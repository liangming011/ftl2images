package com.tool.ftl2images.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.tool.ftl2images.util.Html2ImageUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

@RestController
public class Ftl2ImagesController {


    @PostMapping("/ftl2Image")
    public String ftl2Image(@RequestBody JSONObject jsonObject) throws Exception {
        Map<String,Object> map = JSON.parseObject(jsonObject.getString("data"),Map.class);
        // 读取 ftl 文件
        String html = Html2ImageUtil.freemarkerRender(map, jsonObject.getString("ftlHtml"));
        // 生成图片
        Integer width = jsonObject.getInteger("width");
        String result = Html2ImageUtil.createImages(html, Objects.isNull(width)?1024:width);

        return result;
    }

}
