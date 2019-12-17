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
package com.chatopera.cc.app.handler.apps.setting;

import com.chatopera.cc.app.algorithm.AutomaticServiceDist;
import com.chatopera.cc.app.basic.MainContext;
import com.chatopera.cc.app.basic.MainUtils;
import com.chatopera.cc.app.cache.CacheHelper;
import com.chatopera.cc.app.handler.Handler;
import com.chatopera.cc.app.model.AdType;
import com.chatopera.cc.app.model.BlackEntity;
import com.chatopera.cc.app.model.SessionConfig;
import com.chatopera.cc.app.model.SysDic;
import com.chatopera.cc.app.model.Tag;
import com.chatopera.cc.app.model.UKeFuDic;
import com.chatopera.cc.app.persistence.blob.JpaBlobHelper;
import com.chatopera.cc.app.persistence.repository.AdTypeRepository;
import com.chatopera.cc.app.persistence.repository.BlackListRepository;
import com.chatopera.cc.app.persistence.repository.SessionConfigRepository;
import com.chatopera.cc.app.persistence.repository.StreamingFileRepository;
import com.chatopera.cc.app.persistence.repository.TagRepository;
import com.chatopera.cc.app.persistence.repository.TemplateRepository;
import com.chatopera.cc.util.Menu;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/setting")
public class IMAgentController extends Handler {

	@Autowired
	private SessionConfigRepository sessionConfigRes;

	@Autowired
	private TagRepository tagRes;

	@Autowired
	private BlackListRepository blackListRes;

	@Autowired
	private AdTypeRepository adTypeRes;

	@Autowired
	private TemplateRepository templateRes;


	@Autowired
	private StreamingFileRepository streamingFileRes;

	@Autowired
	private JpaBlobHelper jpaBlobHelper;

	@Value("${web.upload-path}")
	private String path;

	@RequestMapping("/agent/index")
	@Menu(type = "setting", subtype = "sessionconfig", admin = false)
	public ModelAndView index(ModelMap map, HttpServletRequest request) {

		//////////----->忽略orgi    by Wayne on 2019/9/20 14:44   start--->
//        SessionConfig sessionConfig = sessionConfigRes.findByOrgi(super.getOrgi(request));
		final List<SessionConfig> sessionConfigList = sessionConfigRes.findAll();
		//////////<-----忽略orgi    by Wayne on 2019/9/20 14:44    <---end

		if (sessionConfigList == null || sessionConfigList.size() < 1) {
			map.put("sessionConfig", new SessionConfig());
		} else {
			map.put("sessionConfig", sessionConfigList.get(0));
		}


		List<SysDic> dicList = UKeFuDic.getInstance().getDic(MainContext.UKEFU_SYSTEM_DIC);
		SysDic inputDic = null, outputDic = null;
		for (SysDic dic : dicList) {
			if (dic.getCode().equals(MainContext.UKEFU_SYSTEM_AI_INPUT)) {
				inputDic = dic;
			}
			if (dic.getCode().equals(MainContext.UKEFU_SYSTEM_AI_OUTPUT)) {
				outputDic = dic;
			}
		}

		//////////----->忽略orgi    by Wayne on 2019/9/20 14:47   start--->
//        if (inputDic != null) {
//            map.addAttribute("innputtemlet", templateRes.findByTemplettypeAndOrgi(inputDic.getId(), super.getOrgi(request)));
//        }
//        if (outputDic != null) {
//            map.addAttribute("outputtemlet", templateRes.findByTemplettypeAndOrgi(outputDic.getId(), super.getOrgi(request)));
//        }

		if (inputDic != null) {
			map.addAttribute("innputtemlet", templateRes.findByTemplettype(inputDic.getId()));
		}
		if (outputDic != null) {
			map.addAttribute("outputtemlet", templateRes.findByTemplettype(outputDic.getId()));
		}
		//////////<-----忽略orgi    by Wayne on 2019/9/20 14:47    <---end


		return request(super.createAppsTempletResponse("/apps/setting/agent/index"));
	}

	@RequestMapping("/agent/sessionconfig/save")
	@Menu(type = "setting", subtype = "sessionconfig", admin = false)
	public ModelAndView sessionconfig(ModelMap map, HttpServletRequest request, @Valid SessionConfig sessionConfig) {
		SessionConfig tempSessionConfig = null;
		//////////----->忽略orgi    by Wayne on 2019/9/20 14:47   start--->
//        SessionConfig tempSessionConfig = sessionConfigRes.findByOrgi(super.getOrgi(request));
		final List<SessionConfig> sessionConfigList = sessionConfigRes.findAll();
		if (sessionConfigList != null && sessionConfigList.size() > 0) {
			tempSessionConfig = sessionConfigList.get(0);
		}
		//////////<-----忽略orgi    by Wayne on 2019/9/20 14:47    <---end

		if (tempSessionConfig == null) {
			tempSessionConfig = sessionConfig;
			tempSessionConfig.setCreater(super.getUser(request).getId());
		} else {
			MainUtils.copyProperties(sessionConfig, tempSessionConfig);
		}
		tempSessionConfig.setOrgi(super.getOrgi(request));
		sessionConfigRes.save(tempSessionConfig);


		//////////----->忽略orgi    by Wayne on 2019/9/20 14:49   start--->
//        CacheHelper.getSystemCacheBean().put(MainContext.SYSTEM_CACHE_SESSION_CONFIG + "_" + super.getOrgi(request), tempSessionConfig, super.getOrgi(request));
		CacheHelper.getSystemCacheBean().put(MainContext.SYSTEM_CACHE_SESSION_CONFIG, tempSessionConfig, super.getOrgi(request));
		//////////<-----忽略orgi    by Wayne on 2019/9/20 14:49    <---end


		CacheHelper.getSystemCacheBean().delete(MainContext.SYSTEM_CACHE_SESSION_CONFIG_LIST, MainContext.SYSTEM_ORGI);

		AutomaticServiceDist.initSessionConfigList();
		map.put("sessionConfig", tempSessionConfig);


		return request(super.createRequestPageTempletResponse("redirect:/setting/agent/index.html"));
	}

	@RequestMapping("/blacklist")
	@Menu(type = "setting", subtype = "blacklist", admin = false)
	public ModelAndView blacklist(ModelMap map, HttpServletRequest request) {

		//////////----->忽略orgi    by Wayne on 2019/9/20 14:50   start--->
//        map.put("blackList", blackListRes.findByOrgi(super.getOrgi(request), new PageRequest(super.getP(request), super.getPs(request), Sort.Direction.DESC, "endtime")));
		map.put("blackList", blackListRes.findAll(new PageRequest(super.getP(request), super.getPs(request), Sort.Direction.DESC, "endtime")));
		//////////<-----忽略orgi    by Wayne on 2019/9/20 14:50    <---end

		map.put("tagTypeList", UKeFuDic.getInstance().getDic("com.dic.tag.type"));
		return request(super.createAppsTempletResponse("/apps/setting/agent/blacklist"));
	}

	@RequestMapping("/blacklist/delete")
	@Menu(type = "setting", subtype = "tag", admin = false)
	public ModelAndView blacklistdelete(ModelMap map, HttpServletRequest request, @Valid String id) {
		if (!StringUtils.isBlank(id)) {

			//////////----->忽略orgi    by Wayne on 2019/9/20 14:52   start--->
//            BlackEntity tempBlackEntity = blackListRes.findByIdAndOrgi(id, super.getOrgi(request));
			BlackEntity tempBlackEntity = blackListRes.findOne(id);
			//////////<-----忽略orgi    by Wayne on 2019/9/20 14:52    <---end

			if (tempBlackEntity != null) {
				blackListRes.delete(tempBlackEntity);
				CacheHelper.getSystemCacheBean().delete(tempBlackEntity.getUserid(), MainContext.SYSTEM_ORGI);
			}
		}
		return request(super.createRequestPageTempletResponse("redirect:/setting/blacklist.html"));
	}

	@RequestMapping("/tag")
	@Menu(type = "setting", subtype = "tag", admin = false)
	public ModelAndView tag(ModelMap map, HttpServletRequest request, @Valid String code) {
		SysDic tagType = null;
		List<SysDic> tagList = UKeFuDic.getInstance().getDic("com.dic.tag.type");
		if (tagList.size() > 0) {

			if (!StringUtils.isBlank(code)) {
				for (SysDic dic : tagList) {
					if (code.equals(dic.getCode())) {
						tagType = dic;
					}
				}
			} else {
				tagType = tagList.get(0);
			}
			map.put("tagType", tagType);
		}
		if (tagType != null) {

			//////////----->忽略orgi    by Wayne on 2019/9/20 14:52   start--->
//            map.put("tagList", tagRes.findByOrgiAndTagtype(super.getOrgi(request), tagType.getCode(), new PageRequest(super.getP(request), super.getPs(request))));
			map.put("tagList", tagRes.findByTagtype(tagType.getCode(), new PageRequest(super.getP(request), super.getPs(request))));
			//////////<-----忽略orgi    by Wayne on 2019/9/20 14:52    <---end

		}
		map.put("tagTypeList", tagList);
		return request(super.createAppsTempletResponse("/apps/setting/agent/tag"));
	}

	@RequestMapping("/tag/add")
	@Menu(type = "setting", subtype = "tag", admin = false)
	public ModelAndView tagadd(ModelMap map, HttpServletRequest request, @Valid String tagtype) {
		map.addAttribute("tagtype", tagtype);
		return request(super.createRequestPageTempletResponse("/apps/setting/agent/tagadd"));
	}

	@RequestMapping("/tag/edit")
	@Menu(type = "setting", subtype = "tag", admin = false)
	public ModelAndView tagedit(ModelMap map, HttpServletRequest request, @Valid String id, @Valid String tagtype) {
		map.put("tag", tagRes.findOne(id));
		map.addAttribute("tagtype", tagtype);
		return request(super.createRequestPageTempletResponse("/apps/setting/agent/tagedit"));
	}

	@RequestMapping("/tag/update")
	@Menu(type = "setting", subtype = "tag", admin = false)
	public ModelAndView tagupdate(ModelMap map, HttpServletRequest request, @Valid Tag tag, @Valid String tagtype) {

		//////////----->忽略orgi    by Wayne on 2019/9/20 14:54   start--->
//        Tag temptag = tagRes.findByOrgiAndTag(super.getOrgi(request), tag.getTag());
		Tag temptag = tagRes.findByTag(tag.getTag());
		//////////<-----忽略orgi    by Wayne on 2019/9/20 14:54    <---end

		if (temptag == null || tag.getId().equals(temptag.getId())) {
			tag.setOrgi(super.getOrgi(request));
			tag.setCreater(super.getUser(request).getId());
			tagRes.save(tag);
		}
		return request(super.createRequestPageTempletResponse("redirect:/setting/tag.html?code=" + tagtype));
	}

	@RequestMapping("/tag/save")
	@Menu(type = "setting", subtype = "tag", admin = false)
	public ModelAndView tagsave(ModelMap map, HttpServletRequest request, @Valid Tag tag, @Valid String tagtype) {
		//////////-----> 忽略orgi   by Wayne on 2019/9/20 14:55   start--->
//        if (tagRes.findByOrgiAndTag(super.getOrgi(request), tag.getTag()) == null) {
		if (tagRes.findByTag(tag.getTag()) == null) {
			tag.setOrgi(super.getOrgi(request));
			tag.setCreater(super.getUser(request).getId());
			tagRes.save(tag);
		}
		//////////<----- 忽略orgi   by Wayne on 2019/9/20 14:55    <---end
		return request(super.createRequestPageTempletResponse("redirect:/setting/tag.html?code=" + tagtype));
	}

	@RequestMapping("/tag/delete")
	@Menu(type = "setting", subtype = "tag", admin = false)
	public ModelAndView tagdelete(ModelMap map, HttpServletRequest request, @Valid String id, @Valid String tagtype) {
		tagRes.delete(id);
		return request(super.createRequestPageTempletResponse("redirect:/setting/tag.html?code=" + tagtype));
	}


	@RequestMapping("/acd")
	@Menu(type = "setting", subtype = "acd", admin = false)
	public ModelAndView acd(ModelMap map, HttpServletRequest request) {
		map.put("tagTypeList", UKeFuDic.getInstance().getDic("com.dic.tag.type"));
		return request(super.createAppsTempletResponse("/apps/setting/agent/acd"));
	}


	@RequestMapping("/adv")
	@Menu(type = "setting", subtype = "adv", admin = false)
	public ModelAndView adv(ModelMap map, HttpServletRequest request, @Valid String adpos) {
		SysDic advType = null;
		List<SysDic> tagList = UKeFuDic.getInstance().getDic("com.dic.adv.type");
		if (tagList.size() > 0) {
			if (!StringUtils.isBlank(adpos)) {
				for (SysDic dic : tagList) {
					if (adpos.equals(dic.getId())) {
						advType = dic;
					}
				}
			} else {
				advType = tagList.get(0);
			}
			map.put("advType", advType);
		}
		if (advType != null) {

			//////////----->忽略orgi    by Wayne on 2019/9/20 14:56   start--->
//            map.put("adTypeList", adTypeRes.findByAdposAndOrgi(advType.getId(), super.getOrgi(request)));
			map.put("adTypeList", adTypeRes.findByOrgi(advType.getId()));
			//////////<-----忽略orgi    by Wayne on 2019/9/20 14:56    <---end

		}

		map.put("tagTypeList", UKeFuDic.getInstance().getDic("com.dic.tag.type"));

		map.put("advTypeList", UKeFuDic.getInstance().getDic("com.dic.adv.type"));

		return request(super.createAppsTempletResponse("/apps/setting/agent/adv"));
	}

	@RequestMapping("/adv/add")
	@Menu(type = "setting", subtype = "adv", admin = false)
	public ModelAndView advadd(ModelMap map, HttpServletRequest request, @Valid String adpos) {
		map.addAttribute("adpos", adpos);
		return request(super.createRequestPageTempletResponse("/apps/setting/agent/adadd"));
	}

	@RequestMapping("/adv/save")
	@Menu(type = "setting", subtype = "adv", admin = false)
	public ModelAndView advsave(ModelMap map, HttpServletRequest request, @Valid AdType adv, @Valid String advtype, @RequestParam(value = "imgfile", required = false) MultipartFile imgfile) throws IOException {
		adv.setOrgi(super.getOrgi(request));
		adv.setCreater(super.getUser(request).getId());
		if (StringUtils.isNotBlank(adv.getContent())) {
			adv.setContent(adv.getContent().replaceAll("\"", "'"));
		}
		adv.setCreatetime(new Date());
		if (imgfile != null && imgfile.getSize() > 0) {
			adv.setImgurl("/res/image.html?id=" + super.saveImageFileWithMultipart(imgfile));
		}
		adTypeRes.save(adv);

		MainUtils.initAdv(super.getOrgi(request));

		return request(super.createRequestPageTempletResponse("redirect:/setting/adv.html?adpos=" + adv.getAdpos()));
	}

	@RequestMapping("/adv/edit")
	@Menu(type = "setting", subtype = "adv", admin = false)
	public ModelAndView advedit(ModelMap map, HttpServletRequest request, @Valid String adpos, @Valid String id) {
		map.addAttribute("adpos", adpos);
		map.put("ad", adTypeRes.findByIdAndOrgi(id, super.getOrgi(request)));
		return request(super.createRequestPageTempletResponse("/apps/setting/agent/adedit"));
	}

	@RequestMapping("/adv/update")
	@Menu(type = "setting", subtype = "adv", admin = false)
	public ModelAndView advupdate(ModelMap map, HttpServletRequest request, @Valid AdType ad, @Valid String adpos, @RequestParam(value = "imgfile", required = false) MultipartFile imgfile) throws IOException {

		//////////----->忽略orgi    by Wayne on 2019/9/20 14:57   start--->
//        AdType tempad = adTypeRes.findByIdAndOrgi(ad.getId(), super.getOrgi(request));
		AdType tempad = adTypeRes.findOne(ad.getId());
		//////////<-----忽略orgi    by Wayne on 2019/9/20 14:57    <---end

		if (tempad != null) {
			ad.setOrgi(super.getOrgi(request));
			ad.setCreater(tempad.getCreater());
			ad.setCreatetime(tempad.getCreatetime());
			if (StringUtils.isNotBlank(ad.getContent())) {
				ad.setContent(ad.getContent().replaceAll("\"", "'"));
			}
			if (imgfile != null && imgfile.getSize() > 0) {
				ad.setImgurl("/res/image.html?id=" + super.saveImageFileWithMultipart(imgfile));
			} else {
				ad.setImgurl(tempad.getImgurl());
			}
			adTypeRes.save(ad);
			MainUtils.initAdv(super.getOrgi(request));
		}
		return request(super.createRequestPageTempletResponse("redirect:/setting/adv.html?adpos=" + adpos));
	}

	@RequestMapping("/adv/delete")
	@Menu(type = "setting", subtype = "adv", admin = false)
	public ModelAndView advdelete(ModelMap map, HttpServletRequest request, @Valid String id, @Valid String adpos) {
		adTypeRes.delete(id);
		MainUtils.initAdv(super.getOrgi(request));
		return request(super.createRequestPageTempletResponse("redirect:/setting/adv.html?adpos=" + adpos));
	}
}