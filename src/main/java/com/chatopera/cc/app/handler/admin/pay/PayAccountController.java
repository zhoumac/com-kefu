package com.chatopera.cc.app.handler.admin.pay;

import com.chatopera.cc.app.handler.Handler;
import com.chatopera.cc.app.model.UkPayAccountEntity;
import com.chatopera.cc.app.model.UkPayMsgEntity;
import com.chatopera.cc.app.model.User;
import com.chatopera.cc.app.persistence.repository.UkPayAccountRepository;
import com.chatopera.cc.app.persistence.repository.UkPayMsgRepository;
import com.chatopera.cc.constant.CommonConstant;
import com.chatopera.cc.util.Menu;
import com.chatopera.cc.util.jeecg.MyBeanUtils;
import io.swagger.annotations.Api;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;


@Controller
@RequestMapping("/admin/pay/payAccount")
//todo zhou use 支付
@Api(value = "支付")
public class PayAccountController extends Handler {

	private static final String URL = "/admin/pay/payAccount";
	private static final String MODULE_NAME = "支付账号";


	@Autowired
	private UkPayAccountRepository repository;
	@Autowired
	private UkPayMsgRepository ukPayMsgRepository;

	@RequestMapping("/index")
	@Menu(type = "admin", subtype = "payAccount")
	public ModelAndView index(ModelMap map, HttpServletRequest request) {
		map.addAttribute("entityList", getUKPayAccountEntityList(request));
		map.addAttribute("URL", URL);
		map.addAttribute("MODULE_NAME", MODULE_NAME);
		map.addAttribute("user", super.getUser(request));
		return request(super.createAdminTempletResponse(URL + "/index"));
	}

	private Page<UkPayAccountEntity> getUKPayAccountEntityList(HttpServletRequest request) {
		//////////----->超级管理员可以看到所有人录入的信息,非超级管理员只能看到自己录入的信息    by Wayne on 2019/9/13 13:40   start--->
		final User user = super.getUser(request);
		final String usertype = user.getUsertype();
		if (CommonConstant.ADMIN_USER_TYPE.equals(usertype)) {
			Page<UkPayAccountEntity> list = repository.findByStatusNot(CommonConstant.DELETE_STATUS, new PageRequest(super.getP(request), super.getPs(request)));
			for (UkPayAccountEntity ukPayAccountEntity : list) {
				ukPayAccountEntity.setPayFuctionName(ukPayMsgRepository.findOne(ukPayAccountEntity.getPayFuctionId()).getName());
			}
			return list;
		} else {
			final String username = user.getUsername();
			final Page<UkPayAccountEntity> list = repository.findByCreateUserAndStatus(username, CommonConstant.NORMAL_STATUS, new PageRequest(super.getP(request), super.getPs(request)));
			for (UkPayAccountEntity ukPayAccountEntity : list) {
				ukPayAccountEntity.setPayFuctionName(ukPayMsgRepository.findOne(ukPayAccountEntity.getPayFuctionId()).getName());
			}
			return list;
		}
		//////////<-----超级管理员可以看到所有人录入的信息,非超级管理员只能看到自己录入的信息    by Wayne on 2019/9/13 13:40    <---end


	}

	private List<UkPayMsgEntity> getUkPayMsgEntityList(HttpServletRequest request) {
		final User user = super.getUser(request);
		final String username = user.getUsername();
		return ukPayMsgRepository.findByCreateUserAndStatus(username, CommonConstant.NORMAL_STATUS);
	}

	@RequestMapping("/goAdd")
	@Menu(type = "admin", subtype = "payAccount")
	public ModelAndView goAdd(ModelMap map, HttpServletRequest request, String type) {
		map.addAttribute("URL", URL);
		map.addAttribute("UkPayMsgEntityList", getUkPayMsgEntityList(request));
		map.addAttribute("type", type);
		map.addAttribute("userid", super.getUser(request).getId());
		return request(super.createRequestPageTempletResponse(URL + "/add"));
	}

	@RequestMapping("/goUpdate")
	@Menu(type = "admin", subtype = "payAccount")
	public ModelAndView goUpdate(ModelMap map, HttpServletRequest request, @Valid Integer id, String type) {
		ModelAndView view = request(super.createRequestPageTempletResponse(URL + "/update"));
		view.addObject("entity", repository.getOne(id));
		view.addObject("URL", URL);
		map.addAttribute("UkPayMsgEntityList", getUkPayMsgEntityList(request));
		map.addAttribute("userid", super.getUser(request).getId());
		return view;
	}


	@RequestMapping("/doAdd")
	@Menu(type = "admin", subtype = "payAccount")
	public ModelAndView doAdd(HttpServletRequest request, @Valid UkPayAccountEntity entity) {
		String msg = "创建成功!";
		msg = doVerify(entity);

		if (!StringUtils.isBlank(msg)) {
			return request(super.createRequestPageTempletResponse("redirect:" + URL + "/index.html?msg=" + msg));
		} else {
			User user = super.getUser(request);
			entity.setCreateUser(user.getUsername());
			entity.setCreateTime(new Date());
			repository.save(entity);
		}
		return request(super.createRequestPageTempletResponse("redirect:" + URL + "/index.html?msg=" + msg));
	}

	@RequestMapping("/doUpdate")
	@Menu(type = "doUpdate", subtype = "payAccount", admin = true)
	public ModelAndView update(UkPayAccountEntity entity, HttpServletRequest request) {
		// 校验数据
		String msg = "更新成功!";
		msg = doVerify(entity);
		if (!StringUtils.isBlank(msg)) {
			return request(super.createRequestPageTempletResponse("redirect:" + URL + "/index.html?msg=" + msg));
		} else {
			UkPayAccountEntity UkPayAccountEntity = repository.findOne(entity.getId());
			User user = super.getUser(request);
			entity.setUpdateUser(user.getUsername());
			entity.setUpdateTime(new Date());
			try {
				MyBeanUtils.copyBeanNotNull2Bean(entity, UkPayAccountEntity);
				repository.save(UkPayAccountEntity);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return request(super.createRequestPageTempletResponse("redirect:" + URL + "/index.html?msg=" + msg));
	}

	@RequestMapping("/doDelete")
	@Menu(type = "doDelete", subtype = "payAccount", admin = true)
	public ModelAndView doDelete(HttpServletRequest request, @Valid UkPayAccountEntity entity) {
		// 校验数据
		String msg = "删除成功!";
		try {
			entity = repository.findOne(entity.getId());
			User user = super.getUser(request);
			entity.setDeleteUser(user.getUsername());
			entity.setDeleteTime(new Date());
			entity.setStatus(CommonConstant.DELETE_STATUS);
			repository.save(entity);
		} catch (Exception e) {
			e.printStackTrace();
			msg = "删除失败!" + e.getMessage();
		} finally {
			return request(super.createRequestPageTempletResponse("redirect:" + URL + "/index.html?msg=" + msg));
		}
	}

	@RequestMapping("/doLock")
	public ModelAndView doLock(HttpServletRequest request, @Valid UkPayAccountEntity entity) {
		String msg = "锁定成功!";
		try {
			entity = repository.findOne(entity.getId());
			User user = super.getUser(request);
			entity.setLockUser(user.getUsername());
			entity.setLockTime(new Date());
			entity.setStatus(CommonConstant.LOCKED_STATUS);
			repository.save(entity);
		} catch (Exception e) {
			e.printStackTrace();
			msg = "锁定失败!" + e.getMessage();
		} finally {
			return request(super.createRequestPageTempletResponse("redirect:" + URL + "/index.html?msg=" + msg));
		}
	}

	@RequestMapping("/doUnlock")
	public ModelAndView doUnlock(HttpServletRequest request, @Valid UkPayAccountEntity entity) {
		String msg = "解锁成功!";
		try {
			entity = repository.findOne(entity.getId());
			User user = super.getUser(request);
			entity.setUnlockUser(user.getUsername());
			entity.setUnlockTime(new Date());
			entity.setStatus(CommonConstant.NORMAL_STATUS);
			repository.save(entity);
		} catch (Exception e) {
			e.printStackTrace();
			msg = "解锁失败!" + e.getMessage();
		} finally {
			return request(super.createRequestPageTempletResponse("redirect:" + URL + "/index.html?msg=" + msg));
		}
	}

	private String doVerify(UkPayAccountEntity entity) {
		final Integer id = entity.getId();
		final Integer payFuctionId = entity.getPayFuctionId();
		final String account = entity.getAccount();

		if (repository.findByPayFuctionIdAndAccountAndIdNotAndStatus(payFuctionId, account, id, CommonConstant.NORMAL_STATUS) != null) {
			return "account_exist";
		}

		return null;
	}
}
