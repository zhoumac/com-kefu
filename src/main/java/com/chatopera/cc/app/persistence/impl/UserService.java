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
package com.chatopera.cc.app.persistence.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;


@Service
public class UserService {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * 当获取到传递过来的留言游客存在pin时,
	 * 刷新该游客在数据库记录的历史username,
	 * 让系统显示玩家ID信息(玩家ID:xxx)
	 */
	public void  refreshNickName(String userid,String nickName){
		// TODO: 2019/8/27 考虑到聊天记录过多的时候可能会引起发起聊天的性能,在此暂时不修该之前游客的用户名信息 
//		//更新uk_chat_message(聊天记录表)表记录的username信息,用以刷新聊天页面显示的用户名
//		String chatMessageUpdateSql = "UPDATE uk_chat_message SET username=? WHERE userid=?";
//		 jdbcTemplate.update(chatMessageUpdateSql, nickName, userid);

		// 更新uk_agentuser表记录的username信息,用以刷新对话列表显示的用户名
		String angentuserUpateSql = "UPDATE uk_agentuser SET username=? WHERE userid=?";
		jdbcTemplate.update(angentuserUpateSql, nickName, userid);

		// 更新uk_onlineuser表记录的username信息,用以刷新主页显示的在线用户名
		String onlineuserUpateSql = "UPDATE uk_onlineuser SET username=? WHERE id=?";
		jdbcTemplate.update(onlineuserUpateSql, nickName, userid);
	}

}
