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

/**
 * freemarker 文档
 * http://freemarker.foofun.cn/dgui_quickstart_basics.html
 * */
@RestController
public class Ftl2ImagesController {

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * Java的FreeMarker模板引擎的几种模板加载方式
     * https://zhuanlan.zhihu.com/p/367432688
     *
     * {
     *   "data": "{\"test1\": \"1\"}",
     *   "ftlHtml": "<html><body><p>${test1}</p></body></html>",
     *   "width":1234
     * }
     *
     * {
     *   "data": "{\"user\":\"Big Joe\",\"animals\":[{\"name\":\"testname\",\"price\":12341},{\"name\":\"testname\",\"price\":12341},{\"name\":\"testname\",\"price\":12341}]}",
     *   "ftlHtml": "<html><head><title>Welcome!</title><script type=\"text/javascript\" src=\"https://cdnjs.cloudflare.com/ajax/libs/jquery/3.6.0/jquery.min.js\"></script><link href=\"https://g.csdnimg.cn/static/logo/favicon32.ico\" rel=\"shortcut icon\" type=\"image/x-icon\"></link><style>body { font-family:\"宋体\";}</style></head><body><h1>Welcome ${user}<#if user == \"Big Joe\">, our beloved leader</#if>!</h1><h1>Welcome John Doe!</h1><p>Our latest product:<a href=\"products/greenmouse.html\">green mouse</a>!</p><table border=\"1\" style=\"background-color:#00ff00;\"><tr><td>mouse</td><td>50 Euros</td></tr><tr><td>elephant</td><td>5000 Euros</td></tr><tr><td>python</td><td>4999 Euros</td></tr></table><h1>Welcome ${user}!</h1><p>We have these animals:</p><ul><#list animals as animal><li>${animal.name} for ${animal.price} Euros</li></#list></ul></body></html>"
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
        String ftlHtml = (String) redisTemplate.opsForValue().get(key);
        Map<String, Object> map = JSON.parseObject(data1.getString("data"), Map.class);
        // 读取 ftl 文件，填充数据
        String html = Html2ImageUtil.freemarkerRender(map, ftlHtml);
        // 生成图片
        Integer width = data1.getInteger("width");
        String result = Html2ImageUtil.createImages(html, Objects.isNull(width) ? 1024 : width);
        return result;
    }

}
