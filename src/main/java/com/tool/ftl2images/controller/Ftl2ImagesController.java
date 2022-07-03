package com.tool.ftl2images.controller;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.tool.ftl2images.util.Html2ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
public class Ftl2ImagesController {

    @Autowired
    RedisTemplate<UUID, String> redisTemplate;

    /**
     * Java的FreeMarker模板引擎的几种模板加载方式
     * https://zhuanlan.zhihu.com/p/367432688
     *
     * {
     *   "data": "{\"test1\": \"1\"}",
     *   "ftlHtml": "<html><body><p>${test1}</p></body></html>",
     *   "width":1234
     * }
     **/
    @PostMapping("/testftl2Image")
    public String testftl2Image(@RequestBody JSONObject jsonObject) throws Exception {
        Map<String, Object> map = JSON.parseObject(jsonObject.getString("data"), Map.class);
        // 读取 ftl 文件，填充数据
        String html = Html2ImageUtil.freemarkerRender(map, jsonObject.getString("ftlHtml"));
        // 生成图片
        Integer width = jsonObject.getInteger("width");
        String result = Html2ImageUtil.createImages(html, Objects.isNull(width) ? 1024 : width);

        return result;
    }

    /**
     * {
     *   "ftlHtml": "<html><body><p>${test1}</p></body></html>"
     * }
     **/
    @PostMapping("/saveftl2Image")
    public UUID saveftl2Image(@RequestBody String ftlHtml) {
        UUID id = UUID.randomUUID();
        redisTemplate.opsForValue().set(id, ftlHtml);
        return id;
    }

    /**
     * {
     *   "data": "{\"test1\": \"1\"}",
     *   "width":1234
     * }
     **/
    @PostMapping("/ftl2Image")
    public String saveftl2Image(@RequestBody UUID key, @RequestBody String data) throws Exception {
        JSONObject data1 = JSON.parseObject(data);
        String ftlHtml = redisTemplate.opsForValue().get(key);
        Map<String, Object> map = JSON.parseObject(data1.getString("data"), Map.class);
        // 读取 ftl 文件，填充数据
        String html = Html2ImageUtil.freemarkerRender(map, ftlHtml);
        // 生成图片
        Integer width = data1.getInteger("width");
        String result = Html2ImageUtil.createImages(html, Objects.isNull(width) ? 1024 : width);
        return result;
    }

}
