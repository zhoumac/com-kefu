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
package com.chatopera.cc.app.handler;

import com.chatopera.cc.app.basic.MainContext;
import com.chatopera.cc.app.basic.MainUtils;
import com.chatopera.cc.app.basic.Viewport;
import com.chatopera.cc.app.cache.CacheHelper;
import com.chatopera.cc.app.handler.api.rest.QueryParams;
import com.chatopera.cc.app.model.*;
import com.chatopera.cc.app.persistence.blob.JpaBlobHelper;
import com.chatopera.cc.app.persistence.repository.OnlineUserRepository;
import com.chatopera.cc.app.persistence.repository.StreamingFileRepository;
import com.chatopera.cc.app.persistence.repository.TenantRepository;

import com.chatopera.cc.exception.CSKefuException;
import org.apache.commons.lang.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;



@Controller
@SessionAttributes
public class Handler {
    private static final Logger logger = LoggerFactory.getLogger(Handler.class);

    @Autowired
    private TenantRepository tenantRes;

    @Autowired
    private JpaBlobHelper jpaBlobHelper;

    @Autowired
    private StreamingFileRepository streamingFileRes;

    @Autowired
    private OnlineUserRepository onlineUserRepository;

    public final static int PAGE_SIZE_BG = 1;
    public final static int PAGE_SIZE_TW = 20;
    public final static int PAGE_SIZE_FV = 50;
    public final static int PAGE_SIZE_HA = 100;

    private long starttime = System.currentTimeMillis();

    public User getUser(HttpServletRequest request) {
        User user = (User) request.getSession(true).getAttribute(MainContext.USER_SESSION_NAME);
        if (user == null) {
            String authorization = request.getHeader("authorization");
            if (StringUtils.isBlank(authorization) && request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if (cookie.getName().equals("authorization")) {
                        authorization = cookie.getValue();
                        break;
                    }
                }
            }
            if (StringUtils.isNotBlank(authorization)) {
                user = (User) CacheHelper.getApiUserCacheBean().getCacheObject(authorization, MainContext.SYSTEM_ORGI);
            }
            if (user == null) {
                user = new User();
                user.setId(MainUtils.getContextID(request.getSession().getId()));
                user.setUsername(MainContext.GUEST_USER + "_" + MainUtils.genIDByKey(user.getId()));
                user.setOrgi(MainContext.SYSTEM_ORGI);
                user.setSessionid(user.getId());
            }
        } else {
            user.setSessionid(user.getId());
        }
        return user;
    }

    public User getIMUser(HttpServletRequest request, String userid, String nickname) {
        User user = (User) request.getSession(true).getAttribute(MainContext.IM_USER_SESSION_NAME);
        if (user == null) {
            user = new User();
            if (StringUtils.isNotBlank(userid)) {
                user.setId(userid);
            } else {
                user.setId(MainUtils.getContextID(request.getSession().getId()));
            }
            if (StringUtils.isNotBlank(nickname)) {
                user.setUsername(nickname);
            } else {
                // 保存ip或pin
                OnlineUser onlineUser = onlineUserRepository.findById(userid);
                if (onlineUser != null) {
                    user.setUsername(onlineUser.getUsername());
                }else {
                    user.setUsername(MainContext.GUEST_USER + "_" + MainUtils.genIDByKey(user.getId()));
                }
            }
            user.setSessionid(user.getId());
        } else {
            user.setSessionid(MainUtils.getContextID(request.getSession().getId()));
        }
        return user;
    }

    public void setUser(HttpServletRequest request, User user) {
        request.getSession(true).removeAttribute(MainContext.USER_SESSION_NAME);
        request.getSession(true).setAttribute(MainContext.USER_SESSION_NAME, user);
    }


    /**
     * 创建系统监控的 模板页面
     *
     * @param page
     * @return
     */
    public Viewport createAdminTempletResponse(String page) {
        return new Viewport("/admin/include/tpl", page);
    }

    /**
     * 创建系统监控的 模板页面
     *
     * @param page
     * @return
     */
    public Viewport createAppsTempletResponse(String page) {
        return new Viewport("/apps/include/tpl", page);
    }

    /**
     * 创建系统监控的 模板页面
     *
     * @param page
     * @return
     */
    public Viewport createEntIMTempletResponse(String page) {
        return new Viewport("/apps/entim/include/tpl", page);
    }

    public Viewport createRequestPageTempletResponse(String page) {
        return new Viewport(page);
    }

    /**
     * @param data
     * @return
     */
    public ModelAndView request(Viewport data) {
        return new ModelAndView(data.getTemplet() != null ? data.getTemplet() : data.getPage(), "data", data);
    }

    public int getP(HttpServletRequest request) {
        int page = 0;
        String p = request.getParameter("p");
        if (StringUtils.isNotBlank(p) && p.matches("[\\d]*")) {
            page = Integer.parseInt(p);
            if (page > 0) {
                page = page - 1;
            }
        }
        return page;
    }

    public int getPs(HttpServletRequest request) {
        int pagesize = PAGE_SIZE_TW;
        String ps = request.getParameter("ps");
        if (StringUtils.isNotBlank(ps) && ps.matches("[\\d]*")) {
            pagesize = Integer.parseInt(ps);
        }
        return pagesize;
    }

    public int getP(QueryParams params) {
        int page = 0;
        if (params != null && StringUtils.isNotBlank(params.getP()) && params.getP().matches("[\\d]*")) {
            page = Integer.parseInt(params.getP());
            if (page > 0) {
                page = page - 1;
            }
        }
        return page;
    }

    public int getPs(QueryParams params) {
        int pagesize = PAGE_SIZE_TW;
        if (params != null && StringUtils.isNotBlank(params.getPs()) && params.getPs().matches("[\\d]*")) {
            pagesize = Integer.parseInt(params.getPs());
        }
        return pagesize;
    }


    public int get50Ps(HttpServletRequest request) {
        int pagesize = PAGE_SIZE_FV;
        String ps = request.getParameter("ps");
        if (StringUtils.isNotBlank(ps) && ps.matches("[\\d]*")) {
            pagesize = Integer.parseInt(ps);
        }
        return pagesize;
    }

    public String getOrgi(HttpServletRequest request) {
        return getUser(request).getOrgi();
    }

    /**
     * 机构id
     *
     * @param request
     * @return
     */
    public String getOrgid(HttpServletRequest request) {
        User u = getUser(request);
        return u.getOrgid();
    }

    public Tenant getTenant(HttpServletRequest request) {
        return tenantRes.findById(getOrgi(request));
    }

    /**
     * 根据是否租户共享获取orgi
     *
     * @param request
     * @return
     */
    public String getOrgiByTenantshare(HttpServletRequest request) {
        SystemConfig systemConfig = MainUtils.getSystemConfig();
        if (systemConfig != null && systemConfig.isEnabletneant() && systemConfig.isTenantshare()) {
            User user = this.getUser(request);
            return user.getOrgid();
        }
        return getOrgi(request);
    }

    /**
     * 判断是否租户共享
     *
     * @return
     */
    public boolean isTenantshare() {
        SystemConfig systemConfig = MainUtils.getSystemConfig();
        if (systemConfig != null && systemConfig.isEnabletneant() && systemConfig.isTenantshare()) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否多租户
     *
     * @return
     */
    public boolean isEnabletneant() {
        SystemConfig systemConfig = MainUtils.getSystemConfig();
        if (systemConfig != null && systemConfig.isEnabletneant()) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否多租户
     *
     * @return
     */
    public boolean isTenantconsole() {
        SystemConfig systemConfig = MainUtils.getSystemConfig();
        if (systemConfig != null && systemConfig.isEnabletneant() && systemConfig.isTenantconsole()) {
            return true;
        }
        return false;
    }

    public long getStarttime() {
        return starttime;
    }

    public void setStarttime(long starttime) {
        this.starttime = starttime;
    }

    /**
     * 使用Blob保存文件
     * @param multipart
     * @return id
     * @throws IOException
     */
    public String saveImageFileWithMultipart(MultipartFile multipart) throws IOException {
        StreamingFile sf = new StreamingFile();
        final String fileid = MainUtils.getUUID();
        sf.setId(fileid);
        sf.setMime(multipart.getContentType());
        sf.setData(jpaBlobHelper.createBlob(multipart.getInputStream(), multipart.getSize()));
        sf.setName(multipart.getOriginalFilename());
        streamingFileRes.save(sf);
        return fileid;
    }


}
