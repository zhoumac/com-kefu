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
package com.chatopera.cc.app.handler.api.rest;

import com.chatopera.cc.app.aop.CacheAop;
import com.chatopera.cc.app.aop.LogAop;
import com.chatopera.cc.app.cache.impl.DemoCache;
import com.chatopera.cc.app.interceptor.JpaInterceptor;
import com.chatopera.cc.app.aop.model.UseMessage;
import com.chatopera.cc.app.jwt.JwtUtil;
import com.chatopera.cc.app.jwt.TPlayer;
import com.chatopera.cc.util.Menu;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/test")
@Api(value = "测试" )
public class ApiTestController {
	@Autowired
	private DemoCache demoCache;

	@GetMapping("getUseTables")
	@ApiOperation("点击功能后获使用过的系统数据")
	@Menu(subtype = "token",access = true)
	public UseMessage getUseTables() {
		Set<String> useTables = JpaInterceptor.useTables;
		Set<String> useControllers = LogAop.useControllers;
		UseMessage useMessage = UseMessage.builder().useTables(useTables)
				.useControllers(useControllers)
				.useCache(CacheAop.useCache)
				.build();
		return useMessage;
	}

	@GetMapping("getToken")
	@ApiOperation("获取缓存")
	@Menu(subtype = "token",access = true)
	public Object getCache(@RequestParam  String name) {
		TPlayer tPlayer = new TPlayer();
		tPlayer.setNick(name);
		return JwtUtil.createJWT(60*100*24,tPlayer);
	}

	@GetMapping("updateBook")
	@ApiOperation("设置缓存")
	@Menu(subtype = "token",access = true)
	public Object updateBook(@RequestParam String key, @RequestParam String value) {

		return demoCache.updateBook(key,value);
	}

	@GetMapping("findAll")
	@ApiOperation("设置缓存")
	@Menu(subtype = "token",access = true)
	public Object getAll(@RequestParam String key) {

		return demoCache.findAll(key);
	}
}