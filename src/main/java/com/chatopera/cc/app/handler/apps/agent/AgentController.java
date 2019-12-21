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
package com.chatopera.cc.app.handler.apps.agent;

import com.chatopera.cc.app.algorithm.AutomaticServiceDist;
import com.chatopera.cc.app.basic.MainContext;
import com.chatopera.cc.app.basic.MainUtils;
import com.chatopera.cc.app.cache.CacheHelper;
import com.chatopera.cc.app.handler.Handler;
import com.chatopera.cc.app.im.client.NettyClients;
import com.chatopera.cc.app.im.message.ChatMessage;
import com.chatopera.cc.app.im.router.OutMessageRouter;
import com.chatopera.cc.app.model.*;
import com.chatopera.cc.app.persistence.blob.JpaBlobHelper;
import com.chatopera.cc.app.persistence.repository.*;
import com.chatopera.cc.exception.CSKefuException;
import com.chatopera.cc.exchange.DataExchangeInterface;
import com.chatopera.cc.util.*;
import com.chatopera.cc.util.mobile.MobileAddress;
import com.chatopera.cc.util.mobile.MobileNumberUtils;
import freemarker.template.TemplateException;
import io.swagger.annotations.Api;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/agent")
//todo zhou use 聊天App
@Api(value = "聊天App")
public class AgentController extends Handler {

    static final Logger logger = LoggerFactory.getLogger(AgentController.class);

    @Autowired
    private ContactsRepository contactsRes;

    @Autowired
    private PropertiesEventRepository propertiesEventRes;

    @Autowired
    private AgentUserRepository agentUserRepository;


    @Autowired
    private AgentUserContactsRepository agentUserContactsRepository;

    @Autowired
    private AgentStatusRepository agentStatusRepository;

    @Autowired
    private AgentServiceRepository agentServiceRepository;

    @Autowired
    private OnlineUserRepository onlineUserRes;

    @Autowired
    private WeiXinUserRepository weiXinUserRes;

    @Autowired
    private ServiceSummaryRepository serviceSummaryRes;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private AttachmentRepository attachementRes;

    @Autowired
    private BlackListRepository blackListRes;

    @Autowired
    private TagRepository tagRes;

    @Autowired
    private OrganRepository organRes;

    @Autowired
    private TagRelationRepository tagRelationRes;

    @Autowired
    private QuickReplyRepository quickReplyRes;

    @Autowired
    private QuickTypeRepository quickTypeRes;

    @Autowired
    private AgentUserTaskRepository agentUserTaskRes;

    @Autowired
    private SNSAccountRepository snsAccountRes;

    @Autowired
    private UserRepository userRes;


    @Autowired
    private StatusEventRepository statusEventRes;

    @Autowired
    private PbxHostRepository pbxHostRes;


    @Autowired
    private AgentUserContactsRepository agentUserContactsRes;


    @Autowired
    private ConsultInviteRepository inviteRepository;

    @Autowired
    private StreamingFileRepository streamingFileRepository;

    @Autowired
    private JpaBlobHelper jpaBlobHelper;

    @Value("${web.upload-path}")
    private String path;

    @RequestMapping("/index")
    @Menu(type = "apps", subtype = "agent")
    public ModelAndView index(ModelMap map, HttpServletRequest request, HttpServletResponse response, @Valid String sort) throws IOException, TemplateException {
        ModelAndView view = request(super.createAppsTempletResponse("/apps/agent/index"));
        User user = super.getUser(request);
        Sort defaultSort = null;
        if (StringUtils.isBlank(sort)) {
            Cookie[] cookies = request.getCookies();//这样便可以获取一个cookie数组
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("sort")) {
                        sort = cookie.getValue();
                        break;
                    }
                }
            }
        }
        if (StringUtils.isNotBlank(sort)) {
            List<Order> list = new ArrayList<Order>();
            if (sort.equals("lastmessage")) {
                list.add(new Order(Direction.DESC, "status"));
                list.add(new Order(Direction.DESC, "lastmessage"));
            } else if (sort.equals("logintime")) {
                list.add(new Order(Direction.DESC, "status"));
                list.add(new Order(Direction.DESC, "createtime"));
            } else if (sort.equals("default")) {
                defaultSort = new Sort(Direction.DESC, "status");
                Cookie name = new Cookie("sort", null);
                name.setMaxAge(0);
                response.addCookie(name);
            }
            if (list.size() > 0) {
                defaultSort = new Sort(list);
                Cookie name = new Cookie("sort", sort);
                name.setMaxAge(60 * 60 * 24 * 365);
                response.addCookie(name);
                map.addAttribute("sort", sort);
            }
        } else {
            defaultSort = new Sort(Direction.DESC, "status");
        }
        //////////----->不考率orgi    by Wayne on 2019/9/18 14:25   start--->
//        List<AgentUser> agentUserList = agentUserRepository.findByAgentnoAndOrgi(user.getId(), super.getOrgi(request), defaultSort);
        List<AgentUser> agentUserList = agentUserRepository.findByAgentno(user.getId(), defaultSort);
        //////////<-----不考率orgi    by Wayne on 2019/9/18 14:25    <---end
        view.addObject("agentUserList", agentUserList);

        SessionConfig sessionConfig = AutomaticServiceDist.initSessionConfig(super.getOrgi(request));

        view.addObject("sessionConfig", sessionConfig);
        if (sessionConfig.isOtherquickplay()) {
            view.addObject("topicList", OnlineUserUtils.search(null, super.getOrgi(request), super.getUser(request)));
        }

        if (agentUserList.size() > 0) {
            AgentUser agentUser = agentUserList.get(0);
            agentUser = agentUserList.get(0);
            view.addObject("curagentuser", agentUser);
            view.addObject("inviteData", OnlineUserUtils.cousult(agentUser.getAppid(), agentUser.getOrgi(), inviteRepository));
            if (StringUtils.isNotBlank(agentUser.getAgentserviceid())) {

                //////////----->不考率orgi    by Wayne on 2019/9/18 14:30   start--->
//                List<AgentServiceSummary> summarizes = this.serviceSummaryRes.findByAgentserviceidAndOrgi(agentUser.getAgentserviceid(), super.getOrgi(request));
                List<AgentServiceSummary> summarizes = this.serviceSummaryRes.findByAgentserviceid(agentUser.getAgentserviceid());
                //////////<-----不考率orgi    by Wayne on 2019/9/18 14:30    <---end

                if (summarizes.size() > 0) {
                    view.addObject("summary", summarizes.get(0));
                }
            }

            //////////----->客服窗口显示所有的聊天记录,以便二次应答    by Wayne on 2019/9/18 11:05   start--->
//            view.addObject("agentUserMessageList", this.chatMessageRepository.findByUsessionAndOrgi(agentUser.getUserid(), super.getOrgi(request), new PageRequest(0, 20, Direction.DESC, "updatetime")));
            view.addObject("agentUserMessageList", this.chatMessageRepository.findByUsession(agentUser.getUserid(), new PageRequest(0, 20, Direction.DESC, "updatetime")));
            //////////<-----客服窗口显示所有的聊天记录,以便二次应答    by Wayne on 2019/9/18 11:05    <---end

            AgentService agentService = null;
            if (StringUtils.isNotBlank(agentUser.getAgentserviceid())) {
                agentService = this.agentServiceRepository.findOne(agentUser.getAgentserviceid());
                view.addObject("curAgentService", agentService);

                if (agentService != null) {
                    /**
                     * 获取关联数据
                     */
                    processRelaData(request, agentService, map);
                }
            }

            if (MainContext.ChannelTypeEnum.WEIXIN.toString().equals(agentUser.getChannel())) {
                List<WeiXinUser> weiXinUserList = weiXinUserRes.findByOpenid(agentUser.getUserid());
                if (weiXinUserList.size() > 0) {
                    WeiXinUser weiXinUser = weiXinUserList.get(0);
                    view.addObject("weiXinUser", weiXinUser);
                }
            } else if (MainContext.ChannelTypeEnum.WEBIM.toString().equals(agentUser.getChannel())) {

                //////////----->忽略orgi    by Wayne on 2019/9/19 17:29   start--->
//                List<OnlineUser> onlineUserList = this.onlineUserRes.findByUseridAndOrgi(agentUser.getUserid(), super.getOrgi(request));
                List<OnlineUser> onlineUserList = this.onlineUserRes.findByUserid(agentUser.getUserid());
                //////////<-----忽略orgi    by Wayne on 2019/9/19 17:29    <---end

                if (onlineUserList.size() > 0) {
                    OnlineUser onlineUser = onlineUserList.get(0);
                    if (MainContext.OnlineUserOperatorStatus.OFFLINE.toString().equals(onlineUser.getStatus())) {
                        onlineUser.setBetweentime((int) (onlineUser.getUpdatetime().getTime() - onlineUser.getLogintime().getTime()));
                    } else {
                        long time =onlineUser.getLogintime()==null?0:onlineUser.getLogintime().getTime();
                        onlineUser.setBetweentime((int) (System.currentTimeMillis() - time));
                    }
                    view.addObject("onlineUser", onlineUser);
                }
            } else if (MainContext.ChannelTypeEnum.PHONE.toString().equals(agentUser.getChannel())) {
                if (agentService != null && StringUtils.isNotBlank(agentService.getOwner())) {
                    StatusEvent statusEvent = this.statusEventRes.findById(agentService.getOwner());
                    if (statusEvent != null) {
                        if (StringUtils.isNotBlank(statusEvent.getHostid())) {
                            PbxHost pbxHost = pbxHostRes.findById(statusEvent.getHostid());
                            view.addObject("pbxHost", pbxHost);
                        }
                        view.addObject("statusEvent", statusEvent);
                    }
                    MobileAddress ma = MobileNumberUtils.getAddress(agentUser.getPhone());
                    view.addObject("mobileAddress", ma);
                }
            }
            view.addObject("tagRelationList", tagRelationRes.findByUserid(agentUser.getUserid()));


            //////////----->不考虑orgi    by Wayne on 2019/9/19 16:17   start--->
//            view.addObject("serviceCount", Integer.valueOf(this.agentServiceRepository.countByUseridAndOrgiAndStatus(agentUser .getUserid(), super.getOrgi(request),MainContext.AgentUserStatusEnum.END.toString())));
//            view.addObject("tags", tagRes.findByOrgiAndTagtype(super.getOrgi(request), MainContext.ModelType.USER.toString()));
//            view.addObject("quickReplyList", quickReplyRes.findByOrgiAndCreater(super.getOrgi(request), super.getUser(request).getId(), null));
//            List<QuickType> quickTypeList = quickTypeRes.findByOrgiAndQuicktype(super.getOrgi(request), MainContext.QuickTypeEnum.PUB.toString());
//            List<QuickType> priQuickTypeList = quickTypeRes.findByOrgiAndQuicktypeAndCreater(super.getOrgi(request), MainContext.QuickTypeEnum.PRI.toString(), super.getUser(request).getId());

            view.addObject("serviceCount", Integer.valueOf(this.agentServiceRepository.countByUseridAndStatus(agentUser.getUserid(), MainContext.AgentUserStatusEnum.END.toString())));
            view.addObject("tags", tagRes.findByTagtype(MainContext.ModelType.USER.toString()));
            view.addObject("quickReplyList", quickReplyRes.findByCreater(super.getUser(request).getId()));
            List<QuickType> quickTypeList = quickTypeRes.findByQuicktype(MainContext.QuickTypeEnum.PUB.toString());
            List<QuickType> priQuickTypeList = quickTypeRes.findByQuicktypeAndCreater(MainContext.QuickTypeEnum.PRI.toString(), super.getUser(request).getId());

            //////////<-----不考虑orgi    by Wayne on 2019/9/19 16:17    <---end

            quickTypeList.addAll(priQuickTypeList);
            view.addObject("pubQuickTypeList", quickTypeList);
        }
        return view;
    }

    @RequestMapping("/agentusers")
    @Menu(type = "apps", subtype = "agent")
    public ModelAndView agentusers(HttpServletRequest request, String userid) {
        ModelAndView view = request(super.createRequestPageTempletResponse("/apps/agent/agentusers"));
        User user = super.getUser(request);

        //////////----->不考虑orgi    by Wayne on 2019/9/19 16:22   start--->
//        view.addObject("agentUserList", agentUserRepository.findByAgentnoAndOrgi(user.getId(), super.getOrgi(request), new Sort(Direction.DESC, "status")));
//        List<AgentUser> agentUserList = agentUserRepository.findByUseridAndOrgi(userid, super.getOrgi(request));
        view.addObject("agentUserList", agentUserRepository.findByAgentno(user.getId(), new Sort(Direction.DESC, "status")));
        List<AgentUser> agentUserList = agentUserRepository.findByUserid(userid);

        //////////<-----不考虑orgi    by Wayne on 2019/9/19 16:22    <---end

        view.addObject("curagentuser", agentUserList != null && agentUserList.size() > 0 ? agentUserList.get(0) : null);

        return view;
    }

    private void processRelaData(HttpServletRequest request, AgentService agentService, ModelMap map) {
        Sort defaultSort = null;
        defaultSort = new Sort(Direction.DESC, "servicetime");

        //////////----->不考虑orgi    by Wayne on 2019/9/19 16:24   start--->
//        map.addAttribute("agentServiceList", agentServiceRepository.findByUseridAndOrgiAndStatus(agentService.getUserid(), super.getOrgi(request), MainContext.AgentUserStatusEnum.END.toString(), defaultSort));
        map.addAttribute("agentServiceList", agentServiceRepository.findByUseridAndStatus(agentService.getUserid(), MainContext.AgentUserStatusEnum.END.toString(), defaultSort));
        //////////<-----不考虑orgi    by Wayne on 2019/9/19 16:24    <---end


        if (StringUtils.isNotBlank(agentService.getAppid())) {

            //////////----->不考虑orgi    by Wayne on 2019/9/19 16:26   start--->
//            map.addAttribute("snsAccount", snsAccountRes.findBySnsidAndOrgi(agentService.getAppid(), super.getOrgi(request)));
            map.addAttribute("snsAccount", snsAccountRes.findBySnsid(agentService.getAppid()));
            //////////<-----不考虑orgi    by Wayne on 2019/9/19 16:26    <---end

        }

        //////////----->不考虑orgi    by Wayne on 2019/9/19 16:27   start--->
//        List<AgentUserContacts> relaList = agentUserContactsRes.findByUseridAndOrgi(agentService.getUserid(), agentService.getOrgi());
        List<AgentUserContacts> relaList = agentUserContactsRes.findByUserid(agentService.getUserid());
        //////////<-----不考虑orgi    by Wayne on 2019/9/19 16:27    <---end

        if (relaList.size() > 0) {
            AgentUserContacts agentUserContacts = relaList.get(0);
            if (MainContext.model.get("contacts") != null && StringUtils.isNotBlank(agentUserContacts.getContactsid())) {
                DataExchangeInterface dataExchange = (DataExchangeInterface) MainContext.getContext().getBean("contacts");
                if (dataExchange != null) {

                    //////////----->不考虑orgi    by Wayne on 2019/9/19 16:28   start--->
//                    map.addAttribute("contacts", dataExchange.getDataByIdAndOrgi(agentUserContacts.getContactsid(), super.getOrgi(request)));
                    map.addAttribute("contacts", dataExchange.getDataById(agentUserContacts.getContactsid()));
                    //////////<-----不考虑orgi    by Wayne on 2019/9/19 16:28    <---end

                }
            }
            if (MainContext.model.get("workorders") != null && StringUtils.isNotBlank(agentUserContacts.getContactsid())) {
                DataExchangeInterface dataExchange = (DataExchangeInterface) MainContext.getContext().getBean("workorders");
                if (dataExchange != null) {

                    //////////----->不考虑orgi    by Wayne on 2019/9/19 16:29   start--->
//                    map.addAttribute("workOrdersList", dataExchange.getListDataByIdAndOrgi(agentUserContacts.getContactsid(), super.getUser(request).getId(), super.getOrgi(request)));
                    map.addAttribute("workOrdersList", dataExchange.getListDataById(agentUserContacts.getContactsid(), super.getUser(request).getId()));
                    //////////<-----不考虑orgi    by Wayne on 2019/9/19 16:29    <---end

                }
                map.addAttribute("contactsid", agentUserContacts.getContactsid());
            }
        }
    }

    @RequestMapping("/agentuser")
    @Menu(type = "apps", subtype = "agent")
    public ModelAndView agentuser(ModelMap map,
                                  HttpServletRequest request,
                                  String id,
                                  String channel) throws IOException, TemplateException {
        String mainagentuser = "/apps/agent/mainagentuser";
        if (channel.equals("phone")) {
            mainagentuser = "/apps/agent/mainagentuser_callout";
        }
        ModelAndView view = request(super.createRequestPageTempletResponse(mainagentuser));

        //////////----->不考虑orgi    by Wayne on 2019/9/19 16:30   start--->
//        AgentUser agentUser = agentUserRepository.findByIdAndOrgi(id, super.getOrgi(request));
        AgentUser agentUser = agentUserRepository.getOne(id);
        //////////<-----不考虑orgi    by Wayne on 2019/9/19 16:30    <---end

        if (agentUser != null) {
            view.addObject("curagentuser", agentUser);

            //////////----->不考虑orgi    by Wayne on 2019/9/19 16:31   start--->
//            view.addObject("inviteData", OnlineUserUtils.cousult(agentUser.getAppid(), agentUser.getOrgi(), inviteRepository));
//            List<AgentUserTask> agentUserTaskList = agentUserTaskRes.findByIdAndOrgi(id, super.getOrgi(request));
            view.addObject("inviteData", OnlineUserUtils.cousult(agentUser.getAppid(), inviteRepository));
            List<AgentUserTask> agentUserTaskList = agentUserTaskRes.findById(id);
            //////////<-----不考虑orgi    by Wayne on 2019/9/19 16:31    <---end

            if (agentUserTaskList.size() > 0) {
                AgentUserTask agentUserTask = agentUserTaskList.get(0);
                agentUserTask.setTokenum(0);
                agentUserTaskRes.save(agentUserTask);
            }

            if (StringUtils.isNotBlank(agentUser.getAgentserviceid())) {

                //////////----->不考虑orgi    by Wayne on 2019/9/19 16:34   start--->
//                List<AgentServiceSummary> summarizes = this.serviceSummaryRes.findByAgentserviceidAndOrgi(agentUser.getAgentserviceid(), super.getOrgi(request));
                List<AgentServiceSummary> summarizes = this.serviceSummaryRes.findByAgentserviceid(agentUser.getAgentserviceid());
                //////////<-----不考虑orgi    by Wayne on 2019/9/19 16:34    <---end

                if (summarizes.size() > 0) {
                    view.addObject("summary", summarizes.get(0));
                }
            }

            //////////----->不考虑orgi,以方便玩家二次咨询的时候,客服可看到之前的聊天内容    by Wayne on 2019/9/18 14:12   start--->
//            view.addObject("agentUserMessageList", this.chatMessageRepository.findByUsessionAndOrgi(agentUser.getUserid(), super.getOrgi(request), new PageRequest(0, 20, Direction.DESC, "updatetime")));
            view.addObject("agentUserMessageList", this.chatMessageRepository.findByUsession(agentUser.getUserid(), new PageRequest(0, 20, Direction.DESC, "updatetime")));
            //////////<-----不考虑orgi,以方便玩家二次咨询的时候,客服可看到之前的聊天内容    by Wayne on 2019/9/18 14:12    <---end

            AgentService agentService = null;
            if (StringUtils.isNotBlank(agentUser.getAgentserviceid())) {
                agentService = this.agentServiceRepository.findOne(agentUser.getAgentserviceid());
                view.addObject("curAgentService", agentService);
                if (agentService != null) {
                    /**
                     * 获取关联数据
                     */
                    processRelaData(request, agentService, map);
                }
            }
            if (MainContext.ChannelTypeEnum.WEIXIN.toString().equals(agentUser.getChannel())) {

                //////////----->不考虑orgi    by Wayne on 2019/9/19 16:35   start--->
//                List<WeiXinUser> weiXinUserList = weiXinUserRes.findByOpenidAndOrgi(agentUser.getUserid(), super.getOrgi(request));
                List<WeiXinUser> weiXinUserList = weiXinUserRes.findByOpenid(agentUser.getUserid());
                //////////<-----不考虑orgi    by Wayne on 2019/9/19 16:35    <---end

                if (weiXinUserList.size() > 0) {
                    WeiXinUser weiXinUser = weiXinUserList.get(0);
                    view.addObject("weiXinUser", weiXinUser);
                }
            } else if (MainContext.ChannelTypeEnum.WEBIM.toString().equals(agentUser.getChannel())) {

                //////////----->不考虑orgi    by Wayne on 2019/9/19 16:36   start--->
//                List<OnlineUser> onlineUserList = this.onlineUserRes.findByUseridAndOrgi(agentUser.getUserid(), super.getOrgi(request));
                List<OnlineUser> onlineUserList = this.onlineUserRes.findByUserid(agentUser.getUserid());
                //////////<-----不考虑orgi    by Wayne on 2019/9/19 16:36    <---end

                if (onlineUserList.size() > 0) {
                    OnlineUser onlineUser = onlineUserList.get(0);
                    if (onlineUser.getLogintime() != null) {
                        if (MainContext.OnlineUserOperatorStatus.OFFLINE.toString().equals(onlineUser.getStatus())) {
                            onlineUser.setBetweentime((int) (onlineUser.getUpdatetime().getTime() - onlineUser.getLogintime().getTime()));
                        } else {
                            onlineUser.setBetweentime((int) (System.currentTimeMillis() - onlineUser.getLogintime().getTime()));
                        }
                    }
                    view.addObject("onlineUser", onlineUser);
                }
            } else if (MainContext.ChannelTypeEnum.PHONE.toString().equals(agentUser.getChannel())) {
                if (agentService != null && StringUtils.isNotBlank(agentService.getOwner())) {
                    StatusEvent statusEvent = this.statusEventRes.findById(agentService.getOwner());
                    if (statusEvent != null) {
                        if (StringUtils.isNotBlank(statusEvent.getHostid())) {
                            PbxHost pbxHost = pbxHostRes.findById(statusEvent.getHostid());
                            view.addObject("pbxHost", pbxHost);
                        }
                        view.addObject("statusEvent", statusEvent);
                    }
                }
            }

            //////////----->忽略orgi    by Wayne on 2019/9/19 16:37   start--->
//            view.addObject("serviceCount", Integer.valueOf(this.agentServiceRepository.countByUseridAndOrgiAndStatus(agentUser.getUserid(), super.getOrgi(request), MainContext.AgentUserStatusEnum.END.toString())));
            view.addObject("serviceCount", Integer.valueOf(this.agentServiceRepository.countByUseridAndStatus(agentUser.getUserid(), MainContext.AgentUserStatusEnum.END.toString())));
            //////////<-----忽略orgi    by Wayne on 2019/9/19 16:37    <---end


            view.addObject("tagRelationList", tagRelationRes.findByUserid(agentUser.getUserid()));
        }

        SessionConfig sessionConfig = AutomaticServiceDist.initSessionConfig(super.getOrgi(request));

        view.addObject("sessionConfig", sessionConfig);
        if (sessionConfig.isOtherquickplay()) {
            view.addObject("topicList", OnlineUserUtils.search(null, super.getOrgi(request), super.getUser(request)));
        }


        //////////----->不考虑orgi    by Wayne on 2019/9/19 17:22   start--->
//        view.addObject("tags", tagRes.findByOrgiAndTagtype(super.getOrgi(request), MainContext.ModelType.USER.toString()));
//        view.addObject("quickReplyList", quickReplyRes.findByOrgiAndCreater(super.getOrgi(request), super.getUser(request).getId(), null));
//        List<QuickType> quickTypeList = quickTypeRes.findByOrgiAndQuicktype(super.getOrgi(request), MainContext.QuickTypeEnum.PUB.toString());
//        List<QuickType> priQuickTypeList = quickTypeRes.findByOrgiAndQuicktypeAndCreater(super.getOrgi(request), MainContext.QuickTypeEnum.PRI.toString(), super.getUser(request).getId());

        view.addObject("tags", tagRes.findByTagtype(MainContext.ModelType.USER.toString()));
        view.addObject("quickReplyList", quickReplyRes.findByCreater(super.getUser(request).getId()));
        List<QuickType> quickTypeList = quickTypeRes.findByQuicktype(MainContext.QuickTypeEnum.PUB.toString());
        List<QuickType> priQuickTypeList = quickTypeRes.findByQuicktypeAndCreater(MainContext.QuickTypeEnum.PRI.toString(), super.getUser(request).getId());
        //////////<-----不考虑orgi    by Wayne on 2019/9/19 17:22    <---end

        quickTypeList.addAll(priQuickTypeList);
        view.addObject("pubQuickTypeList", quickTypeList);

        return view;
    }

    @RequestMapping("/other/topic")
    @Menu(type = "apps", subtype = "othertopic")
    public ModelAndView othertopic(ModelMap map, HttpServletRequest request, String q) throws IOException, TemplateException {
        SessionConfig sessionConfig = AutomaticServiceDist.initSessionConfig(super.getOrgi(request));

        map.put("sessionConfig", sessionConfig);
        if (sessionConfig.isOtherquickplay()) {
            map.put("topicList", OnlineUserUtils.search(q, super.getOrgi(request), super.getUser(request)));
        }

        return request(super.createRequestPageTempletResponse("/apps/agent/othertopic"));
    }

    @RequestMapping("/other/topic/detail")
    @Menu(type = "apps", subtype = "othertopicdetail")
    public ModelAndView othertopicdetail(ModelMap map, HttpServletRequest request, String id) throws IOException, TemplateException {
        SessionConfig sessionConfig = AutomaticServiceDist.initSessionConfig(super.getOrgi(request));

        map.put("sessionConfig", sessionConfig);
        if (sessionConfig.isOtherquickplay()) {
            map.put("topic", OnlineUserUtils.detail(id, super.getOrgi(request), super.getUser(request)));
        }

        return request(super.createRequestPageTempletResponse("/apps/agent/topicdetail"));
    }


    @RequestMapping("/workorders/list")
    @Menu(type = "apps", subtype = "workorderslist")
    public ModelAndView workorderslist(HttpServletRequest request, String contactsid, ModelMap map) {
        if (MainContext.model.get("workorders") != null && StringUtils.isNotBlank(contactsid)) {
            DataExchangeInterface dataExchange = (DataExchangeInterface) MainContext.getContext().getBean("workorders");
            if (dataExchange != null) {

                //////////----->不考虑orgi    by Wayne on 2019/9/19 17:24   start--->
//                map.addAttribute("workOrdersList", dataExchange.getListDataByIdAndOrgi(contactsid, super.getUser(request).getId(), super.getOrgi(request)));
                map.addAttribute("workOrdersList", dataExchange.getListDataById(contactsid, super.getUser(request).getId()));
                //////////<-----不考虑orgi    by Wayne on 2019/9/19 17:24    <---end

            }
            map.addAttribute("contactsid", contactsid);
        }
        return request(super.createRequestPageTempletResponse("/apps/agent/workorders"));
    }


    @RequestMapping(value = "/ready")
    @Menu(type = "apps", subtype = "agent")
    public ModelAndView ready(HttpServletRequest request) {
        User user = super.getUser(request);

        //////////----->不考虑orgi    by Wayne on 2019/9/19 17:25   start--->
//        List<AgentStatus> agentStatusList = agentStatusRepository.findByAgentnoAndOrgi(user.getId(), super.getOrgi(request));
        List<AgentStatus> agentStatusList = agentStatusRepository.findByAgentno(user.getId());
        //////////<-----不考虑orgi    by Wayne on 2019/9/19 17:25    <---end

        AgentStatus agentStatus = null;
        if (agentStatusList.size() > 0) {
            agentStatus = agentStatusList.get(0);
        } else {
            agentStatus = new AgentStatus();
            agentStatus.setUserid(user.getId());
            agentStatus.setUsername(user.getUname());
            agentStatus.setAgentno(user.getId());
            agentStatus.setLogindate(new Date());

            if (StringUtils.isNotBlank(user.getOrgan())) {
                Organ organ = organRes.findByIdAndOrgi(user.getOrgan(), super.getOrgiByTenantshare(request));
                if (organ != null && organ.isSkill()) {
                    agentStatus.setSkill(organ.getId());
                    agentStatus.setSkillname(organ.getName());
                }
            }
            agentStatus.setUpdatetime(new Date());
            SessionConfig sessionConfig = AutomaticServiceDist.initSessionConfig(super.getOrgi(request));


            //////////----->不考虑orgi    by Wayne on 2019/9/19 17:26   start--->
//            agentStatus.setUsers(agentUserRepository.countByAgentnoAndStatusAndOrgi(user.getId(), MainContext.AgentUserStatusEnum.INSERVICE.toString(), super.getOrgi(request)));
            agentStatus.setUsers(agentUserRepository.countByAgentnoAndStatus(user.getId(), MainContext.AgentUserStatusEnum.INSERVICE.toString()));
            //////////<-----不考虑orgi    by Wayne on 2019/9/19 17:26    <---end


            agentStatus.setOrgi(super.getOrgi(request));
            agentStatus.setMaxusers(sessionConfig.getMaxuser());
            agentStatusRepository.save(agentStatus);
        }
        if (agentStatus != null) {
            /**
             * 更新当前用户状态
             */
            agentStatus.setUsers(AutomaticServiceDist.getAgentUsers(agentStatus.getAgentno(), super.getOrgi(request)));
            agentStatus.setStatus(MainContext.AgentStatusEnum.READY.toString());
            CacheHelper.getAgentStatusCacheBean().put(agentStatus.getAgentno(), agentStatus, super.getOrgi(request));

            AutomaticServiceDist.allotAgent(agentStatus.getAgentno(), super.getOrgi(request));

            AutomaticServiceDist.recordAgentStatus(agentStatus.getAgentno(), agentStatus.getUsername(), agentStatus.getAgentno(), agentStatus.getSkill(), "0".equals(super.getUser(request).getUsertype()), agentStatus.getAgentno(), MainContext.AgentStatusEnum.OFFLINE.toString(), MainContext.AgentStatusEnum.READY.toString(), MainContext.AgentWorkType.MEIDIACHAT.toString(), agentStatus.getOrgi(), null);
        }

        return request(super.createRequestPageTempletResponse("/public/success"));
    }

    @RequestMapping(value = "/notready")
    @Menu(type = "apps", subtype = "agent")
    public ModelAndView notready(HttpServletRequest request) {
        User user = super.getUser(request);
        if (user != null) {
            AutomaticServiceDist.deleteAgentStatus(user.getId(), user.getOrgi(), "0".equals(user.getUsertype()));
        }
        return request(super.createRequestPageTempletResponse("/public/success"));
    }

    @RequestMapping(value = "/busy")
    @Menu(type = "apps", subtype = "agent")
    public ModelAndView busy(HttpServletRequest request) {
        User user = super.getUser(request);
        List<AgentStatus> agentStatusList = agentStatusRepository.findByAgentnoAndOrgi(user.getId(), super.getOrgi(request));
        AgentStatus agentStatus = null;
        if (agentStatusList.size() > 0) {
            agentStatus = agentStatusList.get(0);
            agentStatus.setBusy(true);
            AutomaticServiceDist.recordAgentStatus(agentStatus.getAgentno(), agentStatus.getUsername(), agentStatus.getAgentno(), agentStatus.getSkill(), "0".equals(super.getUser(request).getUsertype()), agentStatus.getAgentno(), MainContext.AgentStatusEnum.READY.toString(), MainContext.AgentStatusEnum.BUSY.toString(), MainContext.AgentWorkType.MEIDIACHAT.toString(), agentStatus.getOrgi(), agentStatus.getUpdatetime());
            agentStatus.setUpdatetime(new Date());
            agentStatusRepository.save(agentStatus);
            CacheHelper.getAgentStatusCacheBean().put(agentStatus.getAgentno(), agentStatus, super.getOrgi(request));
        }
        AutomaticServiceDist.publishMessage(super.getOrgi(request), "agent", "busy", user.getId());

        return request(super.createRequestPageTempletResponse("/public/success"));
    }

    @RequestMapping(value = "/notbusy")
    @Menu(type = "apps", subtype = "agent")
    public ModelAndView notbusy(HttpServletRequest request) {
        User user = super.getUser(request);
        List<AgentStatus> agentStatusList = agentStatusRepository.findByAgentnoAndOrgi(user.getId(), super.getOrgi(request));
        AgentStatus agentStatus = null;
        if (agentStatusList.size() > 0) {
            agentStatus = agentStatusList.get(0);
            agentStatus.setBusy(false);
            AutomaticServiceDist.recordAgentStatus(agentStatus.getAgentno(), agentStatus.getUsername(), agentStatus.getAgentno(), agentStatus.getSkill(), "0".equals(super.getUser(request).getUsertype()), agentStatus.getAgentno(), MainContext.AgentStatusEnum.BUSY.toString(), MainContext.AgentStatusEnum.READY.toString(), MainContext.AgentWorkType.MEIDIACHAT.toString(), agentStatus.getOrgi(), agentStatus.getUpdatetime());

            agentStatus.setUpdatetime(new Date());
            agentStatusRepository.save(agentStatus);
            CacheHelper.getAgentStatusCacheBean().put(agentStatus.getAgentno(), agentStatus, super.getOrgi(request));
            AutomaticServiceDist.allotAgent(agentStatus.getAgentno(), super.getOrgi(request));
        }
        return request(super.createRequestPageTempletResponse("/public/success"));
    }


    //////////----->忽略orgi    by Wayne on 2019/9/21 9:42   start--->
    @RequestMapping(value = "/clean")
    @Menu(type = "apps", subtype = "clean", access = false)
    public ModelAndView clean(HttpServletRequest request) throws Exception {
//        List<AgentUser> agentUserList = agentUserRepository.findByAgentnoAndStatusAndOrgi(super.getUser(request).getId(), MainContext.AgentUserStatusEnum.END.toString(), super.getOrgi(request));
        List<AgentUser> agentUserList = agentUserRepository.findByAgentnoAndStatus(super.getUser(request).getId(), MainContext.AgentUserStatusEnum.END.toString());
        List<AgentService> agentServiceList = new ArrayList<AgentService>();
        for (AgentUser agentUser : agentUserList) {
            if (agentUser != null && super.getUser(request).getId().equals(agentUser.getAgentno())) {
                AutomaticServiceDist.deleteAgentUser(agentUser);
//                AgentService agentService = agentServiceRepository.findByIdAndOrgi(agentUser.getAgentserviceid(), super.getOrgi(request));
                AgentService agentService = agentServiceRepository.getOne(agentUser.getAgentserviceid());
                if (agentService != null) {
                    agentService.setStatus(MainContext.AgentUserStatusEnum.END.toString());
                    agentServiceList.add(agentService);
                }
            }
        }
        agentServiceRepository.save(agentServiceList);
        return request(super
                .createRequestPageTempletResponse("redirect:/agent/index.html"));
    }

    //////////<-----忽略orgi    by Wayne on 2019/9/21 9:42    <---end

    //////////----->忽略orgi    by Wayne on 2019/9/20 19:55   start--->
    @RequestMapping({"/end"})
    @Menu(type = "apps", subtype = "agent")
    public ModelAndView end(HttpServletRequest request, @Valid String userid) throws Exception {
//        AgentUser agentUser = agentUserRepository.findByIdAndOrgi(userid, super.getOrgi(request));
        AgentUser agentUser = agentUserRepository.getOne(userid);

        if (agentUser != null && super.getUser(request).getId().equals(agentUser.getAgentno())) {
//            AutomaticServiceDist.deleteAgentUser(agentUser, super.getOrgi(request));
            AutomaticServiceDist.deleteAgentUser(agentUser);
            if (StringUtils.isNotBlank(agentUser.getAgentserviceid())) {
//                AgentService agentService = agentServiceRepository.findByIdAndOrgi(agentUser.getAgentserviceid(), super.getOrgi(request));
                AgentService agentService = agentServiceRepository.getOne(agentUser.getAgentserviceid());
                agentService.setStatus(MainContext.AgentUserStatusEnum.END.toString());
                agentServiceRepository.save(agentService);
            }
        }
        return request(super.createRequestPageTempletResponse("redirect:/agent/index.html"));
    }
    //////////<-----忽略orgi    by Wayne on 2019/9/20 19:55    <---end

    @RequestMapping({"/readmsg"})
    @Menu(type = "apps", subtype = "agent")
    public ModelAndView readmsg(HttpServletRequest request, @Valid String userid)
            throws Exception {

        //////////----->不考虑orgi    by Wayne on 2019/9/19 16:12   start--->
//        List<AgentUserTask> agentUserTaskList = agentUserTaskRes.findByIdAndOrgi(userid, super.getOrgi(request));
        List<AgentUserTask> agentUserTaskList = agentUserTaskRes.findById(userid);
        //////////<-----不考虑orgi    by Wayne on 2019/9/19 16:12    <---end

        if (agentUserTaskList.size() > 0) {
            AgentUserTask agentUserTask = agentUserTaskList.get(0);
            agentUserTask.setTokenum(0);
            agentUserTaskRes.save(agentUserTask);
        }
        return request(super.createRequestPageTempletResponse("/public/success"));
    }

    @RequestMapping({"/blacklist/add"})
    @Menu(type = "apps", subtype = "blacklist")
    public ModelAndView blacklistadd(ModelMap map, HttpServletRequest request, @Valid String agentuserid, @Valid String agentserviceid, @Valid String userid)
            throws Exception {
        map.addAttribute("agentuserid", agentuserid);
        map.addAttribute("agentserviceid", agentserviceid);
        map.addAttribute("userid", userid);

        //////////----->不考虑orgi    by Wayne on 2019/9/19 17:32   start--->
//        map.addAttribute("agentUser", agentUserRepository.findByIdAndOrgi(userid, super.getOrgi(request)));
        map.addAttribute("agentUser", agentUserRepository.getOne(userid));
        //////////<-----不考虑orgi    by Wayne on 2019/9/19 17:32    <---end

        return request(super.createRequestPageTempletResponse("/apps/agent/blacklistadd"));
    }

    @RequestMapping({"/blacklist/save"})
    @Menu(type = "apps", subtype = "blacklist")
    public ModelAndView blacklist(HttpServletRequest request, @Valid String agentuserid, @Valid String agentserviceid, @Valid String userid, @Valid BlackEntity blackEntity)
            throws Exception {
        User user = super.getUser(request);

        //////////----->不考虑orgi    by Wayne on 2019/9/19 17:32   start--->
//        List<AgentUser> agentUserList = this.agentUserRepository.findByUseridAndOrgi(userid, super.getOrgi(request));
        List<AgentUser> agentUserList = this.agentUserRepository.findByUserid(userid);
        //////////<-----不考虑orgi    by Wayne on 2019/9/19 17:32    <---end

        if (agentUserList.size() > 0) {
            AgentUser agentUser = agentUserList.get(0);

            //////////----->忽略orgi    by Wayne on 2019/9/19 17:33   start--->
//            BlackEntity tempBlackEntiry = blackListRes.findByUseridAndOrgi(agentUser.getUserid(), super.getOrgi(request));
            BlackEntity tempBlackEntiry = blackListRes.findByUserid(agentUser.getUserid());
            //////////<-----忽略orgi    by Wayne on 2019/9/19 17:33    <---end

            if (tempBlackEntiry == null) {
                blackEntity.setUserid(userid);
                blackEntity.setCreater(user.getId());
                blackEntity.setOrgi(super.getOrgi(request));
                if (blackEntity.getControltime() > 0) {
                    blackEntity.setEndtime(new Date(System.currentTimeMillis() + blackEntity.getControltime() * 3600 * 1000L));
                }
                blackEntity.setAgentid(user.getId());
                blackEntity.setAgentuser(agentUser.getUsername());
                blackEntity.setSessionid(agentUser.getSessionid());
                blackEntity.setAgentserviceid(agentserviceid);
                blackEntity.setChannel(agentUser.getChannel());
                blackListRes.save(blackEntity);
            } else {
                if (blackEntity.getControltime() > 0) {
                    tempBlackEntiry.setEndtime(new Date(System.currentTimeMillis() + blackEntity.getControltime() * 3600 * 1000L));
                }
                tempBlackEntiry.setDescription(tempBlackEntiry.getDescription());
                tempBlackEntiry.setControltime(blackEntity.getControltime());
                tempBlackEntiry.setAgentuser(agentUser.getUsername());
                blackListRes.save(tempBlackEntiry);
                blackEntity = tempBlackEntiry;
            }
            if (StringUtils.isNotBlank(userid)) {
                CacheHelper.getSystemCacheBean().put(userid, blackEntity, super.getOrgi(request));
            }
        }
        return end(request, agentuserid);
    }

    @RequestMapping("/tagrelation")
    @Menu(type = "apps", subtype = "tagrelation")
    public ModelAndView tagrelation(ModelMap map, HttpServletRequest request, @Valid String userid, @Valid String tagid, @Valid String dataid) {
        TagRelation tagRelation = tagRelationRes.findByUseridAndTagid(userid, tagid);
        if (tagRelation == null) {
            tagRelation = new TagRelation();
            tagRelation.setUserid(userid);
            tagRelation.setTagid(tagid);
            tagRelation.setDataid(dataid);
            tagRelationRes.save(tagRelation);
        } else {
            tagRelationRes.delete(tagRelation);
        }
        return request(super
                .createRequestPageTempletResponse("/public/success"));
    }

    @RequestMapping("/image/upload")
    @Menu(type = "im", subtype = "image", access = false)
    public ModelAndView upload(ModelMap map,
                               HttpServletRequest request,
                               @RequestParam(value = "imgFile", required = false) MultipartFile multipart,
                               @Valid String id,
                               @Valid String paste) throws IOException {
        ModelAndView view = request(super.createRequestPageTempletResponse("/apps/agent/upload"));
        UploadStatus notify = null;
        if (multipart != null && multipart.getOriginalFilename().lastIndexOf(".") > 0) {
            File uploadDir = new File(path, "upload");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            String fileid = MainUtils.getUUID();
            String fileURL = null;
            ChatMessage data = new ChatMessage();
            StreamingFile sf = new StreamingFile();

            if (multipart.getContentType() != null && multipart.getContentType().indexOf(Constants.ATTACHMENT_TYPE_IMAGE) >= 0) {
                // process thumbnail
                File original = new File(path, "upload/" + fileid + "_original");
                File thumbnail = new File(path, "upload/" + fileid);
                FileCopyUtils.copy(multipart.getBytes(), original);
                MainUtils.processImage(thumbnail, original);
                sf.setThumbnail(jpaBlobHelper.createBlobWithFile(thumbnail));
                fileURL = "/res/image.html?id=" + fileid;
            } else {
                String attachid = processAttachmentFile(multipart, fileid, request);
                fileURL = "/res/file.html?id=" + attachid;
            }

            sf.setId(fileid);
            sf.setData(jpaBlobHelper.createBlob(multipart.getInputStream(), multipart.getSize()));
            sf.setName(multipart.getOriginalFilename());
            sf.setMime(multipart.getContentType());
            streamingFileRepository.save(sf);

            data.setFilename(multipart.getOriginalFilename());
            data.setFilesize((int) multipart.getSize());
            data.setAttachmentid(fileid);

            notify = new UploadStatus("0", fileURL);

            //////////----->不考虑orgi    by Wayne on 2019/9/19 16:10   start--->
//            AgentUser agentUser = agentUserRepository.findByIdAndOrgi(id, super.getOrgi(request));
            AgentUser agentUser = agentUserRepository.getOne(id);
            //////////<-----不考虑orgi    by Wayne on 2019/9/19 16:10    <---end


            if (agentUser != null && paste == null) { // 发送消息
                OutMessageRouter router = (OutMessageRouter) MainContext.getContext().getBean(agentUser.getChannel());
                MessageOutContent outMessage = new MessageOutContent();
                if (router != null) { // 发送消息给访客
                    outMessage.setMessage(fileURL);
                    outMessage.setFilename(multipart.getOriginalFilename());
                    outMessage.setFilesize((int) multipart.getSize());
                    if (multipart.getContentType() != null && multipart.getContentType().indexOf(Constants.ATTACHMENT_TYPE_IMAGE) >= 0) {
                        outMessage.setMessageType(MainContext.MediaTypeEnum.IMAGE.toString());
                        data.setMsgtype(MainContext.MediaTypeEnum.IMAGE.toString());
                    } else {
                        outMessage.setMessageType(MainContext.MediaTypeEnum.FILE.toString());
                        data.setMsgtype(MainContext.MediaTypeEnum.FILE.toString());
                    }
                    outMessage.setCalltype(MainContext.CallTypeEnum.OUT.toString());
                    outMessage.setCreatetime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    outMessage.setNickName(super.getUser(request).getUname());

                    router.handler(agentUser.getUserid(), MainContext.MessageTypeEnum.MESSAGE.toString(), agentUser.getAppid(), outMessage);
                }
                //同时发送消息给 坐席
                data.setMessage(fileURL);
                data.setId(MainUtils.getUUID());
                data.setContextid(agentUser.getContextid());

                data.setAgentserviceid(agentUser.getAgentserviceid());

                data.setCalltype(MainContext.CallTypeEnum.OUT.toString());
                if (StringUtils.isNotBlank(agentUser.getAgentno())) {
                    data.setTouser(agentUser.getUserid());
                }
                data.setChannel(agentUser.getChannel());

                data.setUsession(agentUser.getUserid());
                data.setAppid(agentUser.getAppid());
                data.setUserid(super.getUser(request).getId());

                data.setOrgi(super.getUser(request).getOrgi());

                data.setCreater(super.getUser(request).getId());
                data.setUsername(super.getUser(request).getUname());

                chatMessageRepository.save(data);
                // 通知文件上传消息
                NettyClients.getInstance().publishAgentEventMessage(agentUser.getAgentno(), MainContext.MessageTypeEnum.MESSAGE.toString(), data);
            }
        } else {
            notify = new UploadStatus("请选择图片文件");
        }
        map.addAttribute("upload", notify);
        return view;
    }

    @RequestMapping("/message/image")
    @Menu(type = "resouce", subtype = "image", access = true)
    public ModelAndView messageimage(HttpServletResponse response, ModelMap map, @Valid String id, @Valid String t) throws IOException {
        ChatMessage message = chatMessageRepository.findById(id);
        map.addAttribute("chatMessage", message);

        //////////----->不考虑orgi    by Wayne on 2019/9/19 17:35   start--->
//        map.addAttribute("agentUser", CacheHelper.getAgentUserCacheBean().getCacheObject(message.getUserid(), message.getOrgi()));
        map.addAttribute("agentUser", CacheHelper.getAgentUserCacheBean().getCacheObject(message.getUserid()));
        //////////<-----不考虑orgi    by Wayne on 2019/9/19 17:35    <---end

    	/*if(StringUtils.isNotBlank(t)){
    		map.addAttribute("t", t) ;
    	}*/
        map.addAttribute("t", true);
        return request(super.createRequestPageTempletResponse("/apps/agent/media/messageimage"));
    }

    @RequestMapping("/message/image/upload")
    @Menu(type = "im", subtype = "image", access = false)
    public ModelAndView messageimage(ModelMap map,
                                     HttpServletRequest request,
                                     @RequestParam(value = "image", required = false) MultipartFile image,
                                     @Valid String id,
                                     @Valid String userid,
                                     @Valid String fileid) throws IOException {
        logger.info("messageimage id {}, fileid {}", id, fileid);
        if (image != null && StringUtils.isNotBlank(fileid)) {
            File tempFile = File.createTempFile(fileid, ".png");
            try {
                // 创建临时图片文件
                if (!tempFile.getParentFile().exists()) {
                    tempFile.getParentFile().mkdirs();
                }
                // 写入临时文件
                FileCopyUtils.copy(image.getBytes(), tempFile);
                ChatMessage chatMessage = chatMessageRepository.findById(id);
                chatMessage.setCooperation(true);
                chatMessageRepository.save(chatMessage);

                // 写入协作文件
                String fileName = "upload/" + fileid + "_cooperation";
                File imageFile = new File(path, fileName);
                MainUtils.scaleImage(imageFile, tempFile, 0.1F);

                // 保存到数据库
                StreamingFile sf = streamingFileRepository.findOne(fileid);
                if (sf != null) {
                    sf.setCooperation(jpaBlobHelper.createBlobWithFile(imageFile));
                    streamingFileRepository.save(sf);
                }

                OutMessageRouter router = null;

                //////////----->不考虑orgi    by Wayne on 2019/9/19 17:36   start--->
//                AgentUser agentUser = (AgentUser) CacheHelper.getAgentUserCacheBean().getCacheObject(chatMessage.getUserid(), chatMessage.getOrgi());
                AgentUser agentUser = (AgentUser) CacheHelper.getAgentUserCacheBean().getCacheObject(chatMessage.getUserid());
                //////////<-----不考虑orgi    by Wayne on 2019/9/19 17:36    <---end


                if (agentUser != null) {
                    router = (OutMessageRouter) MainContext.getContext().getBean(agentUser.getChannel());
                    MessageOutContent outMessage = new MessageOutContent();
                    if (router != null) {
                        outMessage.setMessage("/res/image.html?id=" + fileid + "&cooperation=true");
                        outMessage.setFilename(imageFile.getName());

                        outMessage.setAttachmentid(chatMessage.getAttachmentid());

                        outMessage.setFilesize((int) imageFile.length());
                        outMessage.setMessageType(MainContext.MediaTypeEnum.ACTION.toString());
                        outMessage.setCalltype(MainContext.CallTypeEnum.INVITE.toString());
                        outMessage.setCreatetime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                        outMessage.setNickName(super.getUser(request).getUname());

                        router.handler(agentUser.getUserid(), MainContext.MessageTypeEnum.MESSAGE.toString(), agentUser.getAppid(), outMessage);
                    }
                }
            } finally {
                if (tempFile.exists()) {
                    tempFile.delete();
                }
            }
        }
        return request(super.createRequestPageTempletResponse("/public/success"));
    }

    private String processAttachmentFile(final MultipartFile multipart, final String fileid, HttpServletRequest request) throws IOException {
        String id = null;
        if (multipart.getSize() > 0) {            //文件尺寸 限制 ？在 启动 配置中 设置 的最大值，其他地方不做限制
            AttachmentFile attachmentFile = new AttachmentFile();
            attachmentFile.setCreater(super.getUser(request).getId());
            attachmentFile.setOrgi(super.getOrgi(request));
            attachmentFile.setOrgan(super.getUser(request).getOrgan());
            attachmentFile.setModel(MainContext.ModelType.WEBIM.toString());
            attachmentFile.setFilelength((int) multipart.getSize());
            if (multipart.getContentType() != null && multipart.getContentType().length() > 255) {
                attachmentFile.setFiletype(multipart.getContentType().substring(0, 255));
            } else {
                attachmentFile.setFiletype(multipart.getContentType());
            }
            File uploadFile = new File(multipart.getOriginalFilename());
            if (uploadFile.getName() != null && uploadFile.getName().length() > 255) {
                attachmentFile.setTitle(uploadFile.getName().substring(0, 255));
            } else {
                attachmentFile.setTitle(uploadFile.getName());
            }
            if (StringUtils.isNotBlank(attachmentFile.getFiletype()) && attachmentFile.getFiletype().indexOf(Constants.ATTACHMENT_TYPE_IMAGE) >= 0) {
                attachmentFile.setImage(true);
            }
            attachmentFile.setFileid(fileid);
            attachementRes.save(attachmentFile);
            FileUtils.writeByteArrayToFile(new File(path, "upload/" + fileid), multipart.getBytes());
            id = attachmentFile.getId();
        }
        return id;
    }


    @RequestMapping(value = "/contacts")
    @Menu(type = "apps", subtype = "contacts")
    public ModelAndView contacts(ModelMap map, HttpServletRequest request, @Valid String contactsid, @Valid String userid, @Valid String agentserviceid, @Valid String agentuserid) {
        if (StringUtils.isNotBlank(userid) && StringUtils.isNotBlank(contactsid)) {

            //////////----->orgi    by Wayne on 2019/9/19 17:37   start--->
//            List<OnlineUser> onlineUserList = this.onlineUserRes.findByUseridAndOrgi(userid, super.getOrgi(request));
            List<OnlineUser> onlineUserList = this.onlineUserRes.findByUserid(userid);
            //////////<-----orgi    by Wayne on 2019/9/19 17:37    <---end

            if (onlineUserList.size() > 0) {
                OnlineUser onlineUser = onlineUserList.get(0);
                onlineUser.setContactsid(contactsid);
                this.onlineUserRes.save(onlineUser);
            }
            AgentService agentService = this.agentServiceRepository.findOne(agentserviceid);
            if (agentService != null) {
                agentService.setContactsid(contactsid);
                this.agentServiceRepository.save(agentService);

                //////////----->忽略orgi    by Wayne on 2019/9/19 17:37   start--->
//                List<AgentUserContacts> agentUserContactsList = agentUserContactsRes.findByUseridAndOrgi(userid, super.getOrgi(request));
                List<AgentUserContacts> agentUserContactsList = agentUserContactsRes.findByUserid(userid);
                //////////<-----忽略orgi    by Wayne on 2019/9/19 17:37    <---end

                if (agentUserContactsList.size() == 0) {
                    AgentUserContacts agentUserContacts = new AgentUserContacts();
                    agentUserContacts.setAppid(agentService.getAppid());
                    agentUserContacts.setChannel(agentService.getChannel());
                    agentUserContacts.setContactsid(contactsid);
                    agentUserContacts.setUserid(userid);
                    agentUserContacts.setCreater(super.getUser(request).getId());
                    agentUserContacts.setOrgi(super.getOrgi(request));
                    agentUserContacts.setCreatetime(new Date());
                    agentUserContactsRes.save(agentUserContacts);
                } else {
                    AgentUserContacts agentUserContacts = agentUserContactsList.get(0);
                    agentUserContacts.setContactsid(contactsid);
                    agentUserContactsRes.save(agentUserContacts);
                }
            }
            DataExchangeInterface dataExchange = (DataExchangeInterface) MainContext.getContext().getBean("contacts");
            if (dataExchange != null) {

                //////////----->忽略orgi    by Wayne on 2019/9/19 17:39   start--->
//                map.addAttribute("contacts", dataExchange.getDataByIdAndOrgi(contactsid, super.getOrgi(request)));
                map.addAttribute("contacts", dataExchange.getDataById(contactsid));
                //////////<-----忽略orgi    by Wayne on 2019/9/19 17:39    <---end

            }
        }

        return request(super.createRequestPageTempletResponse("/apps/agent/contacts"));
    }

    @RequestMapping(value = "/summary")
    @Menu(type = "apps", subtype = "summary")
    public ModelAndView summary(ModelMap map,
                                HttpServletRequest request,
                                @Valid String userid,
                                @Valid String agentserviceid,
                                @Valid String agentuserid,
                                @Valid String channel) {
        if (StringUtils.isNotBlank(userid) && StringUtils.isNotBlank(agentuserid)) {

            //////////----->忽略orgi    by Wayne on 2019/9/19 17:39   start--->
//            AgentUser agentUser = this.agentUserRepository.findByIdAndOrgi(agentuserid, super.getOrgi(request));
            AgentUser agentUser = this.agentUserRepository.getOne(agentuserid);
            //////////<-----忽略orgi    by Wayne on 2019/9/19 17:39    <---end

            if (agentUser != null && StringUtils.isNotBlank(agentUser.getAgentserviceid())) {

                //////////----->忽略orgi    by Wayne on 2019/9/19 17:40   start--->
//                List<AgentServiceSummary> summaries = this.serviceSummaryRes.findByAgentserviceidAndOrgi(agentUser.getAgentserviceid(), super.getOrgi(request));
                List<AgentServiceSummary> summaries = this.serviceSummaryRes.findByAgentserviceid(agentUser.getAgentserviceid());
                //////////<-----忽略orgi    by Wayne on 2019/9/19 17:40    <---end

                if (summaries.size() > 0) {
                    map.addAttribute("summary", summaries.get(0));
                }
            }

            //////////----->忽略orgi    by Wayne on 2019/9/19 17:41   start--->
//            map.addAttribute("tags", tagRes.findByOrgiAndTagtype(super.getOrgi(request), MainContext.ModelType.SUMMARY.toString()));
            map.addAttribute("tags", tagRes.findByTagtype(MainContext.ModelType.SUMMARY.toString()));
            //////////<-----忽略orgi    by Wayne on 2019/9/19 17:41    <---end

            map.addAttribute("userid", userid);
            map.addAttribute("agentserviceid", agentserviceid);
            map.addAttribute("agentuserid", agentuserid);
            map.addAttribute("channel", channel);

        }

        return request(super.createRequestPageTempletResponse("/apps/agent/summary"));
    }

    @RequestMapping(value = "/summary/save")
    @Menu(type = "apps", subtype = "summarysave")
    public ModelAndView summarysave(ModelMap map,
                                    HttpServletRequest request,
                                    @Valid AgentServiceSummary summary,
                                    @Valid String contactsid,
                                    @Valid String userid,
                                    @Valid String agentserviceid,
                                    @Valid String agentuserid,
                                    @Valid String channel) {
        if (StringUtils.isNotBlank(userid) && StringUtils.isNotBlank(agentuserid)) {
            summary.setOrgi(super.getOrgi(request));
            summary.setCreater(super.getUser(request).getId());

            summary.setCreatetime(new Date());

            //////////----->忽略orgi    by Wayne on 2019/9/19 17:41   start--->
//            AgentService service = agentServiceRepository.findByIdAndOrgi(agentserviceid, super.getOrgi(request));
            AgentService service = agentServiceRepository.getOne(agentserviceid);
            //////////<-----忽略orgi    by Wayne on 2019/9/19 17:41    <---end

            summary.setAgent(service.getAgentno());
            summary.setAgentno(service.getAgentno());
            summary.setUsername(service.getUsername());
            summary.setAgentusername(service.getAgentusername());
            summary.setChannel(service.getChannel());
            summary.setContactsid(contactsid);
            summary.setLogindate(service.getLogindate());
            summary.setContactsid(service.getContactsid());
            summary.setEmail(service.getEmail());
            summary.setPhonenumber(service.getPhone());
            serviceSummaryRes.save(summary);
        }

        return request(super.createRequestPageTempletResponse("redirect:/agent/agentuser.html?id=" + agentuserid + "&channel=" + channel));
    }

    @RequestMapping(value = "/transfer")
    @Menu(type = "apps", subtype = "transfer")
    public ModelAndView transfer(ModelMap map, HttpServletRequest request, @Valid String userid, @Valid String agentserviceid, @Valid String agentuserid) {
        if (StringUtils.isNotBlank(userid) && StringUtils.isNotBlank(agentuserid)) {
            //map.addAttribute("organList", organRes.findByOrgiAndOrgid(super.getOrgi(request),super.getOrgid(request))) ;

            List<Organ> skillList = OnlineUserUtils.organ(super.getOrgi(request), true);
            String currentOrgan = super.getUser(request).getOrgan();
            if (StringUtils.isBlank(currentOrgan)) {
                if (!skillList.isEmpty()) {
                    currentOrgan = skillList.get(0).getId();
                }
            }
            List<AgentStatus> agentStatusList = AutomaticServiceDist.getAgentStatus(null, super.getOrgi(request));
            List<String> usersids = new ArrayList<String>();
            if (!agentStatusList.isEmpty()) {
                for (AgentStatus agentStatus : agentStatusList) {
                    if (agentStatus != null && !agentStatus.getAgentno().equals(super.getUser(request).getId())) {
                        usersids.add(agentStatus.getAgentno());
                    }
                }

            }
            List<User> userList = userRes.findAll(usersids);
            for (User user : userList) {

                //////////----->忽略orgi    by Wayne on 2019/9/19 17:45   start--->
//                user.setAgentStatus((AgentStatus) CacheHelper.getAgentStatusCacheBean().getCacheObject(user.getId(), super.getOrgi(request)));
                user.setAgentStatus((AgentStatus) CacheHelper.getAgentStatusCacheBean().getCacheObject(user.getId()));
                //////////<-----忽略orgi    by Wayne on 2019/9/19 17:45    <---end

            }
            map.addAttribute("userList", userList);
            map.addAttribute("userid", userid);
            map.addAttribute("agentserviceid", agentserviceid);
            map.addAttribute("agentuserid", agentuserid);

            map.addAttribute("skillList", skillList);

            //////////----->忽略orgi    by Wayne on 2019/9/19 17:45   start--->
//            map.addAttribute("agentservice", this.agentServiceRepository.findByIdAndOrgi(agentserviceid, super.getOrgi(request)));
            map.addAttribute("agentservice", this.agentServiceRepository.getOne(agentserviceid));
            //////////<-----忽略orgi    by Wayne on 2019/9/19 17:45    <---end

            map.addAttribute("currentorgan", currentOrgan);
        }

        return request(super.createRequestPageTempletResponse("/apps/agent/transfer"));
    }

    @RequestMapping(value = "/transfer/agent")
    @Menu(type = "apps", subtype = "transferagent")
    public ModelAndView transferagent(ModelMap map, HttpServletRequest request, @Valid String organ) {
        if (StringUtils.isNotBlank(organ)) {
            List<String> usersids = new ArrayList<String>();
            List<AgentStatus> agentStatusList = AutomaticServiceDist.getAgentStatus(organ, super.getOrgi(request));
            if (!agentStatusList.isEmpty()) {
                for (AgentStatus agentStatus : agentStatusList) {
                    if (agentStatus != null && !agentStatus.getAgentno().equals(super.getUser(request).getId())) {
                        usersids.add(agentStatus.getAgentno());
                    }
                }

            }
            List<User> userList = userRes.findAll(usersids);
            for (User user : userList) {

                //////////----->忽略orgi    by Wayne on 2019/9/19 17:47   start--->
//                user.setAgentStatus((AgentStatus) CacheHelper.getAgentStatusCacheBean().getCacheObject(user.getId(), super.getOrgi(request)));
                user.setAgentStatus((AgentStatus) CacheHelper.getAgentStatusCacheBean().getCacheObject(user.getId()));
                //////////<-----忽略orgi    by Wayne on 2019/9/19 17:47    <---end

            }
            map.addAttribute("userList", userList);
            map.addAttribute("currentorgan", organ);
        }
        return request(super.createRequestPageTempletResponse("/apps/agent/transferagentlist"));
    }


    @RequestMapping(value = "/transfer/save")
    @Menu(type = "apps", subtype = "transfersave")
    public ModelAndView transfersave(ModelMap map, HttpServletRequest request, @Valid String userid, @Valid String agentserviceid, @Valid String agentuserid, @Valid String agentno, @Valid String memo) {
        if (StringUtils.isNotBlank(userid) && StringUtils.isNotBlank(agentuserid) && StringUtils.isNotBlank(agentno)) {

            //////////----->忽略orgi    by Wayne on 2019/9/19 17:48   start--->
//            AgentUser agentUser = (AgentUser) CacheHelper.getAgentUserCacheBean().getCacheObject(userid, super.getOrgi(request));
//            AgentService agentService = this.agentServiceRepository.findByIdAndOrgi(agentserviceid, super.getOrgi(request));
            AgentUser agentUser = (AgentUser) CacheHelper.getAgentUserCacheBean().getCacheObject(userid);
            AgentService agentService = this.agentServiceRepository.getOne(agentserviceid);
            //////////<-----忽略orgi    by Wayne on 2019/9/19 17:48    <---end

            if (agentUser != null) {

                agentUser.setAgentno(agentno);
                CacheHelper.getAgentUserCacheBean().put(userid, agentUser, super.getOrgi(request));
                agentUserRepository.save(agentUser);
                if (MainContext.AgentUserStatusEnum.INSERVICE.toString().equals(agentUser.getStatus())) {        //转接 ， 发送消息给 目标坐席

                    //////////----->忽略orgi    by Wayne on 2019/9/19 17:49   start--->
//                    AgentStatus agentStatus = (AgentStatus) CacheHelper.getAgentStatusCacheBean().getCacheObject(super.getUser(request).getId(), super.getOrgi(request));
                    AgentStatus agentStatus = (AgentStatus) CacheHelper.getAgentStatusCacheBean().getCacheObject(super.getUser(request).getId());
                    //////////<-----忽略orgi    by Wayne on 2019/9/19 17:49    <---end


                    if (agentStatus != null) {
                        AutomaticServiceDist.updateAgentStatus(agentStatus, agentUser, super.getOrgi(request), false);
                    }

                    //////////----->忽略orgi    by Wayne on 2019/9/19 17:49   start--->
//                    AgentStatus transAgentStatus = (AgentStatus) CacheHelper.getAgentStatusCacheBean().getCacheObject(agentno, super.getOrgi(request));
                    AgentStatus transAgentStatus = (AgentStatus) CacheHelper.getAgentStatusCacheBean().getCacheObject(agentno);
                    //////////<-----忽略orgi    by Wayne on 2019/9/19 17:49    <---end

                    if (transAgentStatus != null) {
                        AutomaticServiceDist.updateAgentStatus(transAgentStatus, agentUser, super.getOrgi(request), true);
                        agentService.setAgentno(agentno);
                        agentService.setAgentusername(transAgentStatus.getUsername());
                    }
                    // 通知转接消息
                    NettyClients.getInstance().publishAgentEventMessage(agentno, MainContext.MessageTypeEnum.NEW.toString(), agentUser);
                }
            } else {

                //////////----->忽略orgi    by Wayne on 2019/9/19 17:53   start--->
//                agentUser = agentUserRepository.findByIdAndOrgi(agentuserid, super.getOrgi(request));
                agentUser = agentUserRepository.getOne(agentuserid);
                //////////<-----忽略orgi    by Wayne on 2019/9/19 17:53    <---end

                if (agentUser != null) {
                    agentUser.setAgentno(agentno);
                    agentUserRepository.save(agentUser);
                }
            }

            if (agentService != null) {
                agentService.setAgentno(agentno);
                if (StringUtils.isNotBlank(memo)) {
                    agentService.setTransmemo(memo);
                }
                agentService.setTrans(true);
                agentService.setTranstime(new Date());
                agentServiceRepository.save(agentService);
            }
        }

        return request(super.createRequestPageTempletResponse("redirect:/agent/index.html"));
    }


    @RequestMapping("/quicklist")
    @Menu(type = "setting", subtype = "quickreply", admin = true)
    public ModelAndView quicklist(ModelMap map, HttpServletRequest request, @Valid String typeid) {

        //////////----->忽略orgi    by Wayne on 2019/9/19 17:54   start--->
//        map.addAttribute("quickReplyList", quickReplyRes.findByOrgiAndCreater(super.getOrgi(request), super.getUser(request).getId(), null));
//        List<QuickType> quickTypeList = quickTypeRes.findByOrgiAndQuicktype(super.getOrgi(request), MainContext.QuickTypeEnum.PUB.toString());
//        List<QuickType> priQuickTypeList = quickTypeRes.findByOrgiAndQuicktypeAndCreater(super.getOrgi(request), MainContext.QuickTypeEnum.PRI.toString(), super.getUser(request).getId());

        map.addAttribute("quickReplyList", quickReplyRes.findByCreater(super.getUser(request).getId()));
        List<QuickType> quickTypeList = quickTypeRes.findByQuicktype(MainContext.QuickTypeEnum.PUB.toString());
        List<QuickType> priQuickTypeList = quickTypeRes.findByQuicktypeAndCreater(MainContext.QuickTypeEnum.PRI.toString(), super.getUser(request).getId());
        //////////<-----忽略orgi    by Wayne on 2019/9/19 17:54    <---end

        quickTypeList.addAll(priQuickTypeList);
        map.addAttribute("pubQuickTypeList", quickTypeList);

        if (StringUtils.isNotBlank(typeid)) {

            //////////----->忽略orgi    by Wayne on 2019/9/19 17:55   start--->
//            map.addAttribute("quickType", quickTypeRes.findByIdAndOrgi(typeid, super.getOrgi(request)));
            map.addAttribute("quickType", quickTypeRes.getOne(typeid));
            //////////<-----忽略orgi    by Wayne on 2019/9/19 17:55    <---end

        }

        return request(super.createRequestPageTempletResponse("/apps/agent/quicklist"));
    }


    @RequestMapping("/quickreply/add")
    @Menu(type = "setting", subtype = "quickreplyadd", admin = true)
    public ModelAndView quickreplyadd(ModelMap map, HttpServletRequest request, @Valid String parentid) {
        if (StringUtils.isNotBlank(parentid)) {

            //////////----->忽略orgi    by Wayne on 2019/9/19 17:55   start--->
//            map.addAttribute("quickType", quickTypeRes.findByIdAndOrgi(parentid, super.getOrgi(request)));
            map.addAttribute("quickType", quickTypeRes.getOne(parentid));
            //////////<-----忽略orgi    by Wayne on 2019/9/19 17:55    <---end

        }

        //////////----->忽略orgi    by Wayne on 2019/9/19 17:56   start--->
//        map.addAttribute("quickTypeList", quickTypeRes.findByOrgiAndQuicktypeAndCreater(super.getOrgi(request), MainContext.QuickTypeEnum.PRI.toString(), super.getUser(request).getId()));
        map.addAttribute("quickTypeList", quickTypeRes.findByQuicktypeAndCreater(MainContext.QuickTypeEnum.PRI.toString(), super.getUser(request).getId()));
        //////////<-----忽略orgi    by Wayne on 2019/9/19 17:56    <---end

        return request(super.createRequestPageTempletResponse("/apps/agent/quickreply/add"));
    }

    @RequestMapping("/quickreply/save")
    @Menu(type = "setting", subtype = "quickreply", admin = true)
    public ModelAndView quickreplysave(ModelMap map, HttpServletRequest request, @Valid QuickReply quickReply) {
        if (StringUtils.isNotBlank(quickReply.getTitle()) && StringUtils.isNotBlank(quickReply.getContent())) {
            quickReply.setOrgi(super.getOrgi(request));
            quickReply.setCreater(super.getUser(request).getId());
            quickReply.setType(MainContext.QuickTypeEnum.PRI.toString());
            quickReplyRes.save(quickReply);
        }
        return request(super.createRequestPageTempletResponse("redirect:/agent/quicklist.html?typeid=" + quickReply.getCate()));
    }

    @RequestMapping("/quickreply/delete")
    @Menu(type = "setting", subtype = "quickreply", admin = true)
    public ModelAndView quickreplydelete(ModelMap map, HttpServletRequest request, @Valid String id) {
        QuickReply quickReply = quickReplyRes.findOne(id);
        if (quickReply != null) {
            quickReplyRes.delete(quickReply);
        }
        return request(super.createRequestPageTempletResponse("redirect:/agent/quicklist.html?typeid=" + quickReply.getCate()));
    }

    @RequestMapping("/quickreply/edit")
    @Menu(type = "setting", subtype = "quickreply", admin = true)
    public ModelAndView quickreplyedit(ModelMap map, HttpServletRequest request, @Valid String id) {
        QuickReply quickReply = quickReplyRes.findOne(id);
        map.put("quickReply", quickReply);
        if (quickReply != null) {
            map.put("quickType", quickTypeRes.findByIdAndOrgi(quickReply.getCate(), super.getOrgi(request)));
        }

        //////////----->忽略orgi    by Wayne on 2019/9/19 17:57   start--->
//        map.addAttribute("quickTypeList", quickTypeRes.findByOrgiAndQuicktype(super.getOrgi(request), MainContext.QuickTypeEnum.PUB.toString()));
        map.addAttribute("quickTypeList", quickTypeRes.findByQuicktype(MainContext.QuickTypeEnum.PUB.toString()));
        //////////<-----忽略orgi    by Wayne on 2019/9/19 17:57    <---end

        return request(super.createRequestPageTempletResponse("/apps/agent/quickreply/edit"));
    }

    @RequestMapping("/quickreply/update")
    @Menu(type = "setting", subtype = "quickreply", admin = true)
    public ModelAndView quickreplyupdate(ModelMap map, HttpServletRequest request, @Valid QuickReply quickReply) {
        if (StringUtils.isNotBlank(quickReply.getId())) {
            QuickReply temp = quickReplyRes.findOne(quickReply.getId());
            quickReply.setOrgi(super.getOrgi(request));
            quickReply.setCreater(super.getUser(request).getId());
            if (temp != null) {
                quickReply.setCreatetime(temp.getCreatetime());
            }
            quickReply.setType(MainContext.QuickTypeEnum.PUB.toString());
            quickReplyRes.save(quickReply);
        }
        return request(super.createRequestPageTempletResponse("redirect:/agent/quicklist.html?typeid=" + quickReply.getCate()));
    }

    @RequestMapping({"/quickreply/addtype"})
    @Menu(type = "apps", subtype = "kbs")
    public ModelAndView addtype(ModelMap map, HttpServletRequest request, @Valid String typeid) {

        //////////----->忽略orgi    by Wayne on 2019/9/19 17:58   start--->
//        map.addAttribute("quickTypeList", quickTypeRes.findByOrgiAndQuicktypeAndCreater(super.getOrgi(request), MainContext.QuickTypeEnum.PRI.toString(), super.getUser(request).getId()));
        map.addAttribute("quickTypeList", quickTypeRes.findByQuicktypeAndCreater(MainContext.QuickTypeEnum.PRI.toString(), super.getUser(request).getId()));
        //////////<-----忽略orgi    by Wayne on 2019/9/19 17:58    <---end

        if (StringUtils.isNotBlank(typeid)) {

            //////////----->忽略orgi    by Wayne on 2019/9/19 17:58   start--->
//            map.addAttribute("quickType", quickTypeRes.findByIdAndOrgi(typeid, super.getOrgi(request)));
            map.addAttribute("quickType", quickTypeRes.getOne(typeid));
            //////////<-----忽略orgi    by Wayne on 2019/9/19 17:58    <---end

        }
        return request(super.createRequestPageTempletResponse("/apps/agent/quickreply/addtype"));
    }

    @RequestMapping("/quickreply/type/save")
    @Menu(type = "apps", subtype = "kbs")
    public ModelAndView typesave(HttpServletRequest request, @Valid QuickType quickType) {

        //////////----->    by Wayne on 2019/9/19 17:59   start--->
//        int count = quickTypeRes.countByOrgiAndNameAndParentid(super.getOrgi(request), quickType.getName(), quickType.getParentid());
        int count = quickTypeRes.countByNameAndParentid(quickType.getName(), quickType.getParentid());
        //////////<-----    by Wayne on 2019/9/19 17:59    <---end

        if (count == 0) {
            quickType.setOrgi(super.getOrgi(request));
            quickType.setCreater(super.getUser(request).getId());
            quickType.setCreatetime(new Date());
            quickType.setQuicktype(MainContext.QuickTypeEnum.PRI.toString());
            quickTypeRes.save(quickType);
        }
        return request(super.createRequestPageTempletResponse("redirect:/agent/quicklist.html?typeid=" + quickType.getParentid()));
    }

    @RequestMapping({"/quickreply/edittype"})
    @Menu(type = "apps", subtype = "kbs")
    public ModelAndView edittype(ModelMap map, HttpServletRequest request, String id) {
        map.addAttribute("quickType", quickTypeRes.findByIdAndOrgi(id, super.getOrgi(request)));
        map.addAttribute("quickTypeList", quickTypeRes.findByOrgiAndQuicktypeAndCreater(super.getOrgi(request), MainContext.QuickTypeEnum.PRI.toString(), super.getUser(request).getId()));
        return request(super.createRequestPageTempletResponse("/apps/agent/quickreply/edittype"));
    }

    @RequestMapping("/quickreply/type/update")
    @Menu(type = "apps", subtype = "kbs")
    public ModelAndView typeupdate(HttpServletRequest request, @Valid QuickType quickType) {
        QuickType tempQuickType = quickTypeRes.findByIdAndOrgi(quickType.getId(), super.getOrgi(request));
        if (tempQuickType != null) {
            tempQuickType.setName(quickType.getName());
            tempQuickType.setDescription(quickType.getDescription());
            tempQuickType.setInx(quickType.getInx());
            tempQuickType.setParentid(quickType.getParentid());
            quickTypeRes.save(tempQuickType);
        }
        return request(super.createRequestPageTempletResponse("redirect:/agent/quicklist.html?typeid=" + quickType.getId()));
    }

    @RequestMapping({"/quickreply/deletetype"})
    @Menu(type = "apps", subtype = "kbs")
    public ModelAndView deletetype(ModelMap map, HttpServletRequest request, @Valid String id) {
        QuickType tempQuickType = quickTypeRes.findByIdAndOrgi(id, super.getOrgi(request));
        if (tempQuickType != null) {
            quickTypeRes.delete(tempQuickType);

            Page<QuickReply> quickReplyList = quickReplyRes.findByOrgiAndCate(super.getOrgi(request), id, new PageRequest(0, 10000));

            quickReplyRes.delete(quickReplyList.getContent());
        }
        return request(super.createRequestPageTempletResponse("redirect:/agent/quicklist.html" + (tempQuickType != null ? "?typeid=" + tempQuickType.getParentid() : "")));
    }

    @RequestMapping({"/quickreply/content"})
    @Menu(type = "apps", subtype = "quickreply")
    public ModelAndView quickreplycontent(ModelMap map, HttpServletRequest request, @Valid String id) {
        QuickReply quickReply = quickReplyRes.findOne(id);
        if (quickReply != null) {
            map.addAttribute("quickReply", quickReply);
        }
        return request(super.createRequestPageTempletResponse("/apps/agent/quickreplycontent"));
    }

    @RequestMapping("/calloutcontact/add")
    @Menu(type = "apps", subtype = "calloutcontact", admin = true)
    public ModelAndView add(ModelMap map, HttpServletRequest request, @Valid String ckind) {
        map.addAttribute("ckind", ckind);
        return request(super.createRequestPageTempletResponse("/apps/agent/calloutcontact/add"));
    }

    @RequestMapping(value = "/calloutcontact/save")
    @Menu(type = "apps", subtype = "calloutcontact")
    public ModelAndView calloutcontactsave(ModelMap map,
                                           HttpServletRequest request,
                                           @RequestParam(value = "agentuser", required = true) String agentuser,
                                           @Valid Contacts contacts) throws CSKefuException {
        logger.info("[agent ctrl] calloutcontactsave agentuser [{}]", agentuser);
        AgentUser au = agentUserRepository.findOne(agentuser);
        if (au == null)
            throw new CSKefuException("不存在该服务记录");

        User curruser = super.getUser(request);
        contacts.setId(MainUtils.getUUID());
        contacts.setCreater(curruser.getId());
        contacts.setOrgi(curruser.getOrgi());
        contacts.setOrgan(curruser.getOrgan());
        contacts.setPinyin(PinYinTools.getInstance().getFirstPinYin(contacts.getName()));
        if (StringUtils.isBlank(contacts.getCusbirthday())) {
            contacts.setCusbirthday(null);
        }
        contactsRes.save(contacts);

        AgentUserContacts auc = new AgentUserContacts();
        auc.setId(MainUtils.getUUID());
        auc.setUsername(au.getUsername());
        auc.setOrgi(MainContext.SYSTEM_ORGI);
        auc.setUserid(au.getUserid());
        auc.setContactsid(contacts.getId());
        auc.setChannel(au.getChannel());
        auc.setCreatetime(new Date());
        auc.setAppid(au.getAppid());
        auc.setCreater(curruser.getId());
        agentUserContactsRepository.save(auc);
        return request(super.createRequestPageTempletResponse("redirect:/agent/index.html"));
    }

    @RequestMapping("/calloutcontact/update")
    @Menu(type = "apps", subtype = "calloutcontact")
    public ModelAndView update(HttpServletRequest request, @Valid Contacts contacts) {
        Contacts data = contactsRes.findOne(contacts.getId());
        if (data != null) {
            List<PropertiesEvent> events = PropertiesEventUtils.processPropertiesModify(request, contacts, data, "id", "orgi", "creater", "createtime", "updatetime");    //记录 数据变更 历史
            if (events.size() > 0) {
                String modifyid = MainUtils.getUUID();
                Date modifytime = new Date();
                for (PropertiesEvent event : events) {
                    event.setDataid(contacts.getId());
                    event.setCreater(super.getUser(request).getId());
                    event.setOrgi(super.getOrgi(request));
                    event.setModifyid(modifyid);
                    event.setCreatetime(modifytime);
                    propertiesEventRes.save(event);
                }
            }

            User curruser = super.getUser(request);

            contacts.setCreater(data.getCreater());
            contacts.setCreatetime(data.getCreatetime());
            contacts.setOrgi(curruser.getOrgi());
            contacts.setOrgan(curruser.getOrgan());
            contacts.setPinyin(PinYinTools.getInstance().getFirstPinYin(contacts.getName()));
            if (StringUtils.isBlank(contacts.getCusbirthday())) {
                contacts.setCusbirthday(null);
            }
            contactsRes.save(contacts);
        }

        return request(super.createRequestPageTempletResponse("redirect:/agent/index.html"));
    }
}
