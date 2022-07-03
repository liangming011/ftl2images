package com.tool.ftl2images.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class CacheController {

    @Autowired
    RedisTemplate redisTemplate;

    public static final String CACHE_DIR = "ftl2image:cache:ftl:*";

    @PostMapping("cache/ftl")
    public Set<String> getFtlFile() {
        Set<String> keys = redisTemplate.keys(CACHE_DIR);
        return keys;
    }

    @PostMapping("cache/ftl/{ftlName}")
    public String getFtlFile(@PathVariable String ftlName) {
        return (String) redisTemplate.opsForValue().get(ftlName);
    }
}
