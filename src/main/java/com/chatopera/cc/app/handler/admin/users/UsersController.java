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
package com.chatopera.cc.app.handler.admin.users;

import com.chatopera.cc.app.algorithm.AutomaticServiceDist;
import com.chatopera.cc.app.basic.MainContext;
import com.chatopera.cc.app.cache.CacheHelper;
import com.chatopera.cc.app.handler.Handler;
import com.chatopera.cc.app.model.AgentStatus;
import com.chatopera.cc.app.model.User;
import com.chatopera.cc.app.model.UserRole;
import com.chatopera.cc.app.persistence.repository.UserRepository;
import com.chatopera.cc.app.persistence.repository.UserRoleRepository;
import com.chatopera.cc.util.Menu;
import com.chatopera.cc.util.OnlineUserUtils;
import io.swagger.annotations.Api;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @author 程序猿DD
 * @version 1.0.0
 * @blog http://blog.didispace.com
 */
//todo zhou use 用户管理
@Api(value = "用户管理")
@Controller
@RequestMapping("/admin/user")
public class UsersController extends Handler {
    private final static Logger logger = LoggerFactory.getLogger(UsersController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRes;

    /**
     * 客服列表
     */
    @RequestMapping("/index")
    @Menu(type = "admin", subtype = "user")
    public ModelAndView index(ModelMap map, HttpServletRequest request) throws IOException {
//        map.addAttribute(
//                "userList",
//                userRepository.findByDatastatusAndOrgiAndOrgid(
//                        false,
//                        super.getOrgiByTenantshare(request),
//                        super.getOrgid(request),
//                        new PageRequest(
//                                super.getP(request),
//                                super.getPs(request),
//                                Sort.Direction.ASC,
//                                "createtime"
//                        )
//                )
//        );

        //////////----->显示所有正常状态的用户    by Wayne on 2019/9/10 10:17   start--->
        map.addAttribute(
                "userList",
                userRepository.findByDatastatus(
                        false,
                        new PageRequest(
                                super.getP(request),
                                super.getPs(request),
                                Sort.Direction.ASC,
                                "createtime"
                        )
                )
        );
        //////////<-----显示所有正常状态的用户    by Wayne on 2019/9/10 10:17    <---end
        return request(super.createAdminTempletResponse("/admin/user/index"));
    }

    /**
     * 创建客服页面跳转接口
     */
    @RequestMapping("/add")
    @Menu(type = "admin", subtype = "user")
    public ModelAndView add(ModelMap map, HttpServletRequest request) {
        return request(super.createRequestPageTempletResponse("/admin/user/add"));
    }

    /**
     * 创建客服保存接口
     */
    @RequestMapping("/save")
    @Menu(type = "admin", subtype = "user")
    public ModelAndView save(HttpServletRequest request, @Valid User user) {
        String msg = "";
        msg = validUser(user);
        if (!StringUtils.isBlank(msg)) {
            return request(super.createRequestPageTempletResponse("redirect:/admin/user/index.html?msg=" + msg));
        } else {
            if (request.getParameter("admin") != null) {
                user.setUsertype("0");
            } else {
                user.setUsertype(null);
            }
            if (!StringUtils.isBlank(user.getPassword())) {
                // 因修改登录方法,所以修改加密方法为真MD5算法
                user.setPassword(DigestUtils.md5Hex(user.getPassword()));
//                user.setPassword(MainUtils.md5(user.getPassword()));
            }

            //////////----->orgi&orgid默认设置为客服登录名    by Wayne on 2019/9/10 14:44   start--->
//            user.setOrgi(super.getOrgiByTenantshare(request));
//            if (!StringUtils.isBlank(super.getUser(request).getOrgid())) {
//                user.setOrgid(super.getUser(request).getOrgid());
//            } else {
//                user.setOrgid(MainContext.SYSTEM_ORGI);
//            }
            user.setOrgi(user.getUsername());
            user.setOrgid(user.getUsername());
            //////////<-----orgi&orgid默认设置为客服登录名    by Wayne on 2019/9/10 14:44    <---end

            userRepository.save(user);
            OnlineUserUtils.clean(super.getOrgi(request));
        }
        return request(super.createRequestPageTempletResponse("redirect:/admin/user/index.html?msg=" + msg));
    }

    private String validUser(User user) {
        String msg = "";
        User tempUser = userRepository.findByUsernameAndDatastatus(user.getUsername(), false);
        if (tempUser != null) {
            msg = "username_exist";
            return msg;
        }
        tempUser = userRepository.findByEmailAndDatastatus(user.getEmail(), false);
        if (tempUser != null) {
            msg = "email_exist";
            return msg;
        }
        tempUser = userRepository.findByMobileAndDatastatus(user.getMobile(), false);
        if (tempUser != null) {
            msg = "mobile_exist";
            return msg;
        }

        if (!validUserCallcenterParams(user)) {
            msg = "sip_account_exist";
            return msg;
        }
        return msg;
    }

    /**
     * 更新客服保存接口
     */
    @RequestMapping("/edit")
    @Menu(type = "admin", subtype = "user")
    public ModelAndView edit(ModelMap map, HttpServletRequest request, @Valid String id) {
        ModelAndView view = request(super.createRequestPageTempletResponse("/admin/user/edit"));
//        view.addObject("userData", userRepository.findByIdAndOrgi(id, super.getOrgiByTenantshare(request)));

        //////////----->所有用户都可编辑    by Wayne on 2019/9/10 10:24   start--->
        view.addObject("userData", userRepository.getOne(id));
        //////////<-----所有用户都可编辑    by Wayne on 2019/9/10 10:24    <---end
        return view;
    }

    /**跳转接口
     */
    @RequestMapping("/update")
    @Menu(type = "admin", subtype = "user", admin = true)
    public ModelAndView update(HttpServletRequest request, @Valid User user) {
        User tempUser = userRepository.getOne(user.getId());
        if (tempUser != null) {
            String msg = validUserUpdate(user, tempUser);
            if (!StringUtils.isBlank(msg)) {
                return request(super.createRequestPageTempletResponse("redirect:/admin/user/index.html?msg=" + msg));
            }
            tempUser.setUname(user.getUname());
            tempUser.setUsername(user.getUsername());
            tempUser.setEmail(user.getEmail());
            tempUser.setMobile(user.getMobile());
            tempUser.setSipaccount(user.getSipaccount());
            tempUser.setAvatar(user.getAvatar());
            //切换成非坐席 判断是否坐席 以及 是否有对话
            if (!user.isAgent()) {
                AgentStatus agentStatus = (AgentStatus) CacheHelper.getAgentStatusCacheBean().getCacheObject((super.getUser(request)).getId(), super.getOrgi(request));
                if (!(agentStatus == null && AutomaticServiceDist.getAgentUsers(super.getUser(request).getId(), super.getOrgi(request)) == 0)) {
                    return request(super.createRequestPageTempletResponse("redirect:/admin/user/index.html?msg=t1"));
                }
            }
            tempUser.setAgent(user.isAgent());


            //////////----->加入客服的orgi存在,则不做修改    by Wayne on 2019/9/10 14:39   start--->
            if (StringUtils.isBlank(tempUser.getOrgi())) {
                tempUser.setOrgi(super.getOrgiByTenantshare(request));
            }
            if (StringUtils.isBlank(super.getUser(request).getOrgid())) {
                tempUser.setOrgid(MainContext.SYSTEM_ORGI);
            }
            //////////<-----加入客服的orgi存在,则不做修改    by Wayne on 2019/9/10 14:39    <---end
            
            tempUser.setCallcenter(user.isCallcenter());
            if (!StringUtils.isBlank(user.getPassword())) {
                // 因修改登录方法,所以修改加密方法为真MD5算法
                tempUser.setPassword(DigestUtils.md5Hex(user.getPassword()));
            }

            if (request.getParameter("admin") != null) {
                tempUser.setUsertype("0");
            } else {
                tempUser.setUsertype(null);
            }

            if (tempUser.getCreatetime() == null) {
                tempUser.setCreatetime(new Date());
            }
            tempUser.setUpdatetime(new Date());
            userRepository.save(tempUser);
            OnlineUserUtils.clean(super.getOrgi(request));
        }
        return request(super.createRequestPageTempletResponse("redirect:/admin/user/index.html"));
    }

    private String validUserUpdate(User user, User oldUser) {
        String msg = "";
        User tempUser = userRepository.findByUsernameAndDatastatus(user.getUsername(), false);
        if (tempUser != null && !user.getUsername().equals(oldUser.getUsername())) {
            msg = "username_exist";
            return msg;
        }
        tempUser = userRepository.findByEmailAndDatastatus(user.getEmail(), false);
        if (tempUser != null && !user.getEmail().equals(oldUser.getEmail())) {
            msg = "email_exist";
            return msg;
        }
        tempUser = userRepository.findByMobileAndDatastatus(user.getMobile(), false);
        if (tempUser != null && !user.getMobile().equals(oldUser.getMobile())) {
            msg = "mobile_exist";
            return msg;
        }

        if (!validUserCallcenterParams(user)) {
            msg = "sip_account_exist";
            return msg;
        }

        return msg;
    }

    /**
     * 删除客服接口
     */
    @RequestMapping("/delete")
    @Menu(type = "admin", subtype = "user")
    public ModelAndView delete(HttpServletRequest request, @Valid User user) {
        String msg = "admin_user_delete";
        if (user != null) {
            List<UserRole> userRole = userRoleRes.findByOrgiAndUser(super.getOrgiByTenantshare(request), user);
            userRoleRes.delete(userRole);    //删除用户的时候，同时删除用户对应的
            user = userRepository.getOne(user.getId());
            user.setDatastatus(true);
            userRepository.save(user);
            OnlineUserUtils.clean(super.getOrgi(request));
        } else {
            msg = "admin_user_not_exist";
        }
        return request(super.createRequestPageTempletResponse("redirect:/admin/user/index.html?msg=" + msg));
    }

    /**
     * 根据是否开启呼叫中心模块检测账号
     * @param user
     * @return
     */
    private boolean validUserCallcenterParams(final User user) {
        if (user.isCallcenter() && MainContext.isEnableCalloutModule()) {
            List<User> tempUserList = userRepository.findBySipaccountAndDatastatus(user.getSipaccount(), false);
            if (tempUserList.size() != 0 && user.getSipaccount() != "") {
                return false;
            }
        }
        return true;
    }

}