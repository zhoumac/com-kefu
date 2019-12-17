package com.chatopera.cc.app.handler.admin.pay;

import com.chatopera.cc.app.handler.Handler;
import com.chatopera.cc.app.model.UkPayMsgEntity;
import com.chatopera.cc.app.model.User;
import com.chatopera.cc.app.persistence.repository.UkPayMsgRepository;
import com.chatopera.cc.app.persistence.repository.UserRepository;
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


@Controller
@RequestMapping("/admin/pay/payMsg")
//todo zhou use 支付列表
@Api(value = "支付列表")
public class PayMsgController extends Handler {

	private static final String URL = "/admin/pay/payMsg";
	private static final String MODULE_NAME = "支付信息";

	@Autowired
	private UkPayMsgRepository repository;
	@Autowired
	private UserRepository userRepository;

	@RequestMapping("/index")
	@Menu(type = "admin", subtype = "payMsg")
	public ModelAndView index(ModelMap map, HttpServletRequest request, final String code, final String name,
	                          final String type, final String message, final String status, final String createUser) {
		map.addAttribute("entityList", getUkPayMsgEntityList(request));
//		map.addAttribute("entityList", repository.findAll((Root<UkPayMsgEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb)->{
//			List<Predicate> list = new ArrayList<>();
//			if (StringUtils.isNotBlank(code)) {
//				list.add(cb.equal(root.get("code").as(String.class), code));
//			}
//			if (StringUtils.isNotBlank(name)) {
//				list.add(cb.equal(root.get("name").as(String.class), name));
//			}
//			if (StringUtils.isNotBlank(type)) {
//				list.add(cb.equal(root.get("type").as(String.class), type));
//			}
//			if (StringUtils.isNotBlank(message)) {
//				list.add(cb.like(root.get("code").as(String.class), message));
//			}
//			if (StringUtils.isNotBlank(code)) {
//				list.add(cb.equal(root.get("code").as(String.class), code));
//			}
//			if (StringUtils.isNotBlank(code)) {
//				list.add(cb.equal(root.get("code").as(String.class), code));
//			}
//			if (StringUtils.isNotBlank(code)) {
//				list.add(cb.equal(root.get("code").as(String.class), code));
//			}
//			if (StringUtils.isNotBlank(code)) {
//				list.add(cb.equal(root.get("code").as(String.class), code));
//			}
//		}));
		map.addAttribute("URL", URL);
		map.addAttribute("MODULE_NAME", MODULE_NAME);
		map.addAttribute("userList", userRepository.findAll());
		return request(super.createAdminTempletResponse(URL + "/index"));
	}

	private Page<UkPayMsgEntity> getUkPayMsgEntityList(HttpServletRequest request) {
		//////////----->超级管理员可以看到所有人录入的信息,非超级管理员只能看到自己录入的信息    by Wayne on 2019/9/13 10:38   start--->
		final User user = super.getUser(request);
		final String usertype = user.getUsertype();
		if (CommonConstant.ADMIN_USER_TYPE.equals(usertype)) {
			return repository.findByStatusNot(CommonConstant.DELETE_STATUS, new PageRequest(super.getP(request), super.getPs(request)));
		} else {
			final String username = user.getUsername();
			return repository.findByCreateUserAndStatus(username, CommonConstant.NORMAL_STATUS, new PageRequest(super.getP(request), super.getPs(request)));
		}
		//////////<-----超级管理员可以看到所有人录入的信息,非超级管理员只能看到自己录入的信息    by Wayne on 2019/9/13 10:38    <---end
	}

	@RequestMapping("/goAdd")
	@Menu(type = "admin", subtype = "payMsg")
	public ModelAndView goAdd(ModelMap map, HttpServletRequest request) {
		map.addAttribute("URL", URL);
		return request(super.createRequestPageTempletResponse(URL + "/add"));
	}

	@RequestMapping("/goUpdate")
	@Menu(type = "admin", subtype = "payMsg")
	public ModelAndView goUpdate(ModelMap map, HttpServletRequest request, @Valid Integer id) {
		ModelAndView view = request(super.createRequestPageTempletResponse(URL + "/update"));
		view.addObject("entity", repository.getOne(id));
		view.addObject("URL", URL);
		return view;
	}

	@RequestMapping("/doAdd")
	@Menu(type = "admin", subtype = "payMsg")
	public ModelAndView doAdd(HttpServletRequest request, @Valid UkPayMsgEntity entity) {
		String msg = "创建成功!";
		msg = doVerify(entity, request);

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
	@Menu(type = "doUpdate", subtype = "payMsg", admin = true)
	public ModelAndView update(UkPayMsgEntity entity, HttpServletRequest request) {
		// 校验数据
		String msg = "更新成功!";
		msg = doVerify(entity, request);
		if (!StringUtils.isBlank(msg)) {
			return request(super.createRequestPageTempletResponse("redirect:" + URL + "/index.html?msg=" + msg));
		} else {
			UkPayMsgEntity ukPayMsgEntity = repository.findOne(entity.getId());
			User user = super.getUser(request);
			entity.setUpdateUser(user.getUsername());
			entity.setUpdateTime(new Date());
			try {
				MyBeanUtils.copyBeanNotNull2Bean(entity, ukPayMsgEntity);
				repository.save(ukPayMsgEntity);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return request(super.createRequestPageTempletResponse("redirect:" + URL + "/index.html?msg=" + msg));
	}

	@RequestMapping("/doDelete")
	@Menu(type = "doDelete", subtype = "payMsg", admin = true)
	public ModelAndView doDelete(HttpServletRequest request, @Valid UkPayMsgEntity entity) {
		// 校验数据
		String msg = "删除成功!";
		try {
			entity = repository.findOne(entity.getId());
			User user = super.getUser(request);
			entity.setDeleteUser(user.getUname());
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

	private String doVerify(UkPayMsgEntity entity, HttpServletRequest request) {
		final int id = entity.getId();
		final String code = entity.getCode();
		final String name = entity.getName();

		//////////----->区分用户校验    by Wayne on 2019/9/13 10:51   start--->
		User user = super.getUser(request);
		final String username = user.getUsername();
		if (repository.findByCreateUserAndCodeAndIdNotAndStatus(username, code, id, CommonConstant.NORMAL_STATUS) != null) {
			return "code_exist";
		}
		if (repository.findByCreateUserAndNameAndIdNotAndStatus(username, name, id, CommonConstant.NORMAL_STATUS) != null) {
			return "name_exist";
		}
		//////////<-----区分用户校验    by Wayne on 2019/9/13 10:51    <---end

		return null;
	}
}
