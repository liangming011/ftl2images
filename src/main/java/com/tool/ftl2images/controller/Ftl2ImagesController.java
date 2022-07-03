package com.tool.ftl2images.controller;

import com.tool.ftl2images.util.Html2ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
public class Ftl2ImagesController {

    @Autowired
    RedisTemplate redisTemplate;

    public static final String CACHE_DIR = "ftl2image:cache:ftl:";

    public static final String SUFFIX = ".ftl";

    @PostMapping("ftl2Image")
    public String ftl2Image(Map<String, Object> data, String ftlHtml, Integer width) throws Exception {
        // 将 ftl 存储到 redis 中
        String ftlName = UUID.randomUUID() + SUFFIX;
        redisTemplate.opsForValue().set(CACHE_DIR + ftlName, ftlHtml);

        // 读取 ftl 文件
        String html = Html2ImageUtil.freemarkerRender(data, ftlName);
        // 生成图片
        return Html2ImageUtil.createImages(html, Objects.isNull(width)?1024:width);
    }
}
