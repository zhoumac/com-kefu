package com.chatopera.cc.app.cache.impl;

import com.chatopera.cc.app.basic.MainContext;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service

public class DemoCache {
    @Autowired
    private CacheManager cacheManager;

    private String cacheName  ="book";

    @CachePut(value="book", key="#key")
    public  Map<String,String> updateBook(String key,String value){

        Map<String,String> map = Maps.newHashMap();
        map.put(key,value);
       // cacheManager.getCache("book").put(key,value);
        return map;
    }


    public Object findAll(String key) {
        //Object book = cacheManager.getCache("book").getNativeCache();


        //CaffeineCacheManager cacheCacheManager= MainContext.getContext().getBean(CaffeineCacheManager.class);
        return cacheManager.getCache("book").getName();
    }
    @Cacheable(value = "book")
    public Map<String,String> findBook(String key) {


        System.out.println("查询了数据库");
        return null;
    }

}
