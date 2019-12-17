/*
 * Copyright (C) 2018 Chatopera Inc, <https://www.chatopera.com>
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

package com.chatopera.cc.app.schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.chatopera.cc.util.ExtUtils;

/**
 * 拓展任务，定期给用户解锁
 */
@Configuration
@EnableScheduling
public class ExtTast {

	@Value("${ext.delKeyPre:ext_lock_*}")
	private String delKeyPre;

	/**
	 * 使用StringRedisTemplate而不是RedisTemplate解决序列化问题
	 * https://stackoverflow.com/questions/13215024/weird-redis-key-with-spring-data-jedis
	 */
	@Autowired
	private StringRedisTemplate redis;

//	@Scheduled(fixedDelay = 36000000) // 每10小时执行一次
	 @Scheduled(fixedDelay = 30000) // 每10小时执行一次
	public void dowork() {
		int today = ExtUtils.getCurrentDay();
		Set<String> keys = redis.keys(delKeyPre);

		List<String> delKeys = new ArrayList<String>();
		for (String s : keys) {
			String arr[] = s.split("-");
			if (arr.length > 0) {
				String str = arr[arr.length - 1];
				Integer day = null;
				try {
					day = Integer.parseInt(str);
				} catch (Exception e) {
				}
				if (day != null) {
					if (day < today || (today == 1 && day > 360)) {
						delKeys.add(s);
					}
				}
			}
		}
		if (!delKeys.isEmpty()) {
			redis.delete(delKeys);
		}
	}
}
