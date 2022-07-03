package com.tool.ftl2images.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class CacheController {

    @Autowired
    RedisTemplate redisTemplate;

    @PostMapping("cache/clear")
    public Boolean getFtlFile(UUID key) {
        return redisTemplate.delete(key);
    }
}
