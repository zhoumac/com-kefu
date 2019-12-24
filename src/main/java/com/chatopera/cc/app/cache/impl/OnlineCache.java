/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018 Chatopera Inc, <https://www.chatopera.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chatopera.cc.app.cache.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chatopera.cc.app.cache.CacheBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Service("online_cache")
public class OnlineCache  implements CacheBean {

	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private CacheManager cacheManager;

	private HashOperations getHashOperations(){
		HashOperations hashOperations = redisTemplate.opsForHash();
		return hashOperations;
	}

	private String cacheName ;

	@Override
	@CachePut(value="HAZLCAST_ONLINE_CACHE", key="#key")
	public Object put(String key, Object value, String orgi) {

		getHashOperations().put(cacheName,key,value);
		return value;
	}

	@Override
	public void clear(String orgi) {

		getHashOperations().delete(cacheName);
		cacheManager.getCache(cacheName).clear();
	}

	@Override
	@CacheEvict(value = "HAZLCAST_ONLINE_CACHE",key="#key")
	public Object delete(String key, String orgi) {
		return 	getHashOperations().delete(cacheName,key);
	}

	@Override
	@CacheEvict(value = "HAZLCAST_ONLINE_CACHE",key="#key")
	public Object delete(String key) {
		return 	getHashOperations().delete(cacheName,key);
	}

	@Override
	@CachePut(value="HAZLCAST_ONLINE_CACHE", key="#key")
	public Object update(String key, String orgi, Object object) {

		getHashOperations().put(cacheName,key,object);
		return object;
	}

	@Override
	@Cacheable(value = "HAZLCAST_ONLINE_CACHE",key="#key")
	public Object getCacheObject(String key, String orgi) {

		return getHashOperations().get(cacheName,key);
	}

	@Override
	@Cacheable(value = "HAZLCAST_ONLINE_CACHE",key="#key")
	public Object getCacheObject(String key) {
		return getHashOperations().get(cacheName,key);

	}

	@Override
	@Cacheable(value = "HAZLCAST_ONLINE_CACHE",key="#key")
	public Object getCacheObject(String key, String orgi, Object defaultValue) {
		return getHashOperations().get(cacheName,key);

	}

	@Override
	public Collection<?> getAllCacheObject(String orgi) {

		Map map = (Map)redisTemplate.opsForHash().keys(cacheName);

		return map.keySet();

	}

	public CacheBean getCacheInstance(String cacheName){
		this.cacheName = cacheName ;
		return this ;
	}



	@Override
	public Object getCache() {

		return getHashOperations().entries(cacheName);
	}

	@Override
	public JSONObject getStatics() {

		return (JSONObject) JSON.parseObject(JSON.toJSONString(getCacheObject(cacheName)));
	}

	@Override
	public long getSize() {

		Set map = (Set) redisTemplate.opsForHash().keys(cacheName);

		return map.size();
	}
}
