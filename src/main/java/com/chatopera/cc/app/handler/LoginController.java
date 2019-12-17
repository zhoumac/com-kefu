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
import com.chatopera.cc.app.cache.CacheHelper;
import com.chatopera.cc.app.model.Organ;
import com.chatopera.cc.app.model.Role;
import com.chatopera.cc.app.model.RoleAuth;
import com.chatopera.cc.app.model.SystemConfig;
import com.chatopera.cc.app.model.User;
import com.chatopera.cc.app.model.UserRole;
import com.chatopera.cc.app.persistence.repository.OrganRepository;
import com.chatopera.cc.app.persistence.repository.RoleAuthRepository;
import com.chatopera.cc.app.persistence.repository.UserRepository;
import com.chatopera.cc.app.persistence.repository.UserRoleRepository;
import com.chatopera.cc.util.ExtUtils;
import com.chatopera.cc.util.Menu;
import com.chatopera.cc.util.OnlineUserUtils;
import com.hazelcast.aws.utility.StringUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author UK
 * @version 1.0.0
 */
@Controller
public class LoginController extends Handler {
	private final static Logger logger = LoggerFactory.getLogger(LoginController.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserRoleRepository userRoleRes;

	@Autowired
	private RoleAuthRepository roleAuthRes;

	@Autowired
	private OrganRepository organRepository;

	private ValueOperations<String, String> valueOperations;

	@Value("${ext.login-uid-max-error:10}")
	private int loginUidMaxError;
	@Value("${ext.login-ip-max-error:10}")
	private int loginIpMaxError;
	@Value("${login.user.session.prefix}")
	private String loginUserSessionIdPrefix;
	@Value("${login.user.ip.days:7}")
	private int loginUserIpDays;


	private HashOperations<String, String, String> redisHashOps;
	/**
	 * 使用StringRedisTemplate而不是RedisTemplate解决序列化问题
	 * https://stackoverflow.com/questions/13215024/weird-redis-key-with-spring-data-jedis
	 */
	@Autowired
	private StringRedisTemplate redis;

	@PostConstruct
	private void init() {
		redisHashOps = redis.opsForHash();
		valueOperations = redis.opsForValue();
	}

	/**
	 * 获取一个用户所拥有的所有部门ID
	 *
	 * @param user
	 */
	private void organs(final User user, final String organ) {
		if (organ == null)
			return;

		if (user.inMyorgans(organ))
			return;

		user.getMyorgans().add(organ);
		List<Organ> y = organRepository.findByOrgiAndParent(user.getOrgi(), organ);
		for (Organ x : y) {
			try {
				organs(user, x.getId());
			} catch (Exception e) {
				logger.error("organs", e);
			}
		}
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	@Menu(type = "apps", subtype = "user", access = true)
	public ModelAndView login(HttpServletRequest request, HttpServletResponse response,
			@RequestHeader(value = "referer", required = false) String referer, @Valid String msg)
			throws NoSuchAlgorithmException {
		ModelAndView view = request(super.createRequestPageTempletResponse("redirect:/"));
		if (request.getSession(true).getAttribute(MainContext.USER_SESSION_NAME) == null) {
			view = request(super.createRequestPageTempletResponse("/login"));
			if (!StringUtils.isBlank(request.getParameter("referer"))) {
				referer = request.getParameter("referer");
			}
			if (!StringUtils.isBlank(referer)) {
				view.addObject("referer", referer);
			}
			Cookie[] cookies = request.getCookies();// 这样便可以获取一个cookie数组
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if (cookie != null && !StringUtils.isBlank(cookie.getName())
							&& !StringUtils.isBlank(cookie.getValue())) {
						if (cookie.getName().equals(MainContext.UKEFU_SYSTEM_COOKIES_FLAG)) {
							String flagid = MainUtils.decryption(cookie.getValue());
							if (!StringUtils.isBlank(flagid)) {
								User user = userRepository.findById(flagid);
								if (user != null) {
									view = this.processLogin(request, response, view, user, referer);
								}
							}
						}
					}
				}
			}
		}
		if (!StringUtils.isBlank(msg)) {
			view.addObject("msg", msg);
		}
		SystemConfig systemConfig = MainUtils.getSystemConfig();
		if (systemConfig != null && systemConfig.isEnableregorgi()) {
			view.addObject("show", true);
		}
		if (systemConfig != null) {
			view.addObject("systemConfig", systemConfig);
		}
		return view;
	}

	private boolean isLock(String ip, String ipKey, String uid, String uidKey) {
		String ipTimes = redisHashOps.get(ipKey, ip);
		if (StringUtil.isNotEmpty(ipTimes) && Integer.parseInt(ipTimes) > loginIpMaxError) {
			return true;
		}

		String uidTimes = redisHashOps.get(uidKey, uid);
		if (StringUtil.isNotEmpty(uidTimes) && Integer.parseInt(uidTimes) > loginIpMaxError) {
			return true;
		}
		return false;

	}

	private void tryLock(String ip, String ipKey, String uid, String uidKey) {
		try {
			redisHashOps.increment(ipKey, ip, 1);
			redisHashOps.increment(uidKey, uid, 1);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@Menu(type = "apps", subtype = "user", access = true)
	public ModelAndView login(HttpServletRequest request, HttpServletResponse response, @Valid User user,
			@Valid String referer, @Valid String sla) throws NoSuchAlgorithmException {
		ModelAndView view = request(super.createRequestPageTempletResponse("redirect:/"));
		if (request.getSession(true).getAttribute(MainContext.USER_SESSION_NAME) == null) {
			if (user != null && user.getUsername() != null) {
				////// 防止用户暴力破解 sin 2019-08-17 18:30:00 start
				String ip = ExtUtils.getIpAddress(request);
				String ipKey = ExtUtils.buildKey(ExtUtils.ext_lock_ip, "login",
						String.valueOf(ExtUtils.getCurrentDay()));
				String uid = user.getUsername();
				String uidKey = ExtUtils.buildKey(ExtUtils.ext_lock_uid, "login",
						String.valueOf(ExtUtils.getCurrentDay()));
				if (isLock(ip, ipKey, uid, uidKey)) {
					view = request(super.createRequestPageTempletResponse("/login"));
					if (!StringUtils.isBlank(referer)) {
						view.addObject("referer", referer);
					}
					view.addObject("msg", "2");
					SystemConfig systemConfig = MainUtils.getSystemConfig();
					if (systemConfig != null && systemConfig.isEnableregorgi()) {
						view.addObject("show", true);
					}
					if (systemConfig != null) {
						view.addObject("systemConfig", systemConfig);
					}
					return view;
				}
				////// 防止用户暴力破解 sin 2019-08-17 18:30:00 end


				// 修改登录密码判断方法
				// http请求很容易被截获，在写登录模块时，直接使用明文密码请求，很容易明文密码泄露；
				//      若在js页面对密码进行一次加密后在传输，虽不是明文密码，但也完全可以截获加密后的暗文，
				//      伪造http请求进行登录。为了防止密码泄露，通过参考各种方案，找到了以下比较好实现的方法：
				// 1、登录请求分两次进行，第一次仅传用户名
				// 2、服务器收到用户名后，生成一串随机数，将随机数响应给客户端，并将用户名和随机数存到session
				// 3、客户端收到响应后，将密码和随机数安一定的规则组合起来，再md5加密，再http请求
				//      （此时保证了每一次登录请求的密码会随随机数的不同而不同，这个随机数为服务器生成，
				//      相当于一个公钥，与本次登录操作唯一且一一对应，客户端无法伪造）
				// 4、服务器收到请求，取出session中的用户名和随机数串，核对用户名，再取数据库中的正确密码，
				//      再按相同的规则与随机数组合并md5加密，再比较请求的密码暗文，返回登录结果。
				User loginUser = userRepository.findByUsernameAndDatastatus(user.getUsername(), false);
				if (loginUser != null) {
					// 数据库储存的经md5加密后的暗文密码
					String realPassword = loginUser.getPassword();
					// 前端传递过来的经过md5加密后的暗文密码
					// 拼接该次登录请求所得存在session的uuid所得的字符串再经md5加密得到的二次暗文密码
					// 即二次暗文密码=MD5(MD5(明文密码)+uuid)
					String ciphertextPassword = user.getPassword();
					HttpSession session = request.getSession();
					// session中存放的用户名
					String username = String.valueOf(session.getAttribute("username"));
					// session中存放的uuid
					String userUuid = String.valueOf(session.getAttribute("userUuid"));
					// 数据库中存放的经MD5加密的暗文密码拼接session中存放的uuid再经MD5加密得到的校验字符串
					String validation = DigestUtils.md5Hex(realPassword + userUuid);
					if (StringUtils.equals(user.getUsername(),username)&&StringUtils.equals(ciphertextPassword,validation)) {
						view = getLoginSuccesModelAndView(request, response, referer, sla, view, uid, loginUser);
					}else {
						view = getLoginFailModelAndView(referer, ip, ipKey, uid, uidKey);
					}
				}else {
					view = getLoginFailModelAndView(referer, ip, ipKey, uid, uidKey);
				}
			}
		}
		SystemConfig systemConfig = MainUtils.getSystemConfig();
		if (systemConfig != null && systemConfig.isEnableregorgi()) {
			view.addObject("show", true);
		}
		if (systemConfig != null) {
			view.addObject("systemConfig", systemConfig);
		}
		return view;
	}

	private ModelAndView getLoginFailModelAndView(@Valid String referer, String ip, String ipKey, String uid, String uidKey) {
		ModelAndView view;////// 登录失败处理start yl 2019-08-15 10:22:01
		tryLock(ip, ipKey, uid, uidKey);
		////// 登录失败处理end yl 2019-08-15 10:22:01
		view = request(super.createRequestPageTempletResponse("/login"));
		if (!StringUtils.isBlank(referer)) {
			view.addObject("referer", referer);
		}
		view.addObject("msg", "0");
		return view;
	}

	private ModelAndView getLoginSuccesModelAndView(HttpServletRequest request, HttpServletResponse response, @Valid String referer, @Valid String sla, ModelAndView view, String uid, User loginUser) throws NoSuchAlgorithmException {
		view = this.processLogin(request, response, view, loginUser, referer);
		if (!StringUtils.isBlank(sla) && sla.equals("1")) {
			Cookie flagid = new Cookie(MainContext.UKEFU_SYSTEM_COOKIES_FLAG,
					MainUtils.encryption(loginUser.getId()));
			flagid.setMaxAge(7 * 24 * 60 * 60);
			response.addCookie(flagid);
			// add authorization code for rest api
			String auth = MainUtils.getUUID();
			CacheHelper.getApiUserCacheBean().put(auth, loginUser, MainContext.SYSTEM_ORGI);
			response.addCookie((new Cookie("authorization", auth)));
		}

		// 记录用户登录的ip
		remarkUserSessionId(uid,request.getSession().getId());
		return view;
	}

	@PostMapping(value = "/getUserUuid")
	@ResponseBody
	public String getUserUuid(HttpServletRequest request, String username) {
		String uuid = MainUtils.getUUID();
		HttpSession session = request.getSession();
		session.setAttribute("username",username);
		session.setAttribute("userUuid", uuid);
		return uuid;
	}

	@RequestMapping(value = "/checkLoginValid")
	@ResponseBody
	public boolean checkLoginValid(HttpServletRequest request, String username){
		return userSessionValidation(request.getSession().getId(), username);
	}

	private boolean userSessionValidation(String sessionId, String username) {
		String loginUserKey = MessageFormat.format(loginUserSessionIdPrefix, username);
		return StringUtils.equals( valueOperations.get(loginUserKey),sessionId);
	}

	/**
	 * 记录用户登录时的ip
	 * @param userName
	 * @param sessionId
	 */
	private void remarkUserSessionId(String userName, String sessionId) {
		String loginUserSessionKey = MessageFormat.format(loginUserSessionIdPrefix, userName);
		valueOperations.set(loginUserSessionKey, sessionId,loginUserIpDays, TimeUnit.DAYS);
	}

	private ModelAndView processLogin(HttpServletRequest request, HttpServletResponse response, ModelAndView view,
			final User loginUser, String referer) {
		if (loginUser != null) {
			loginUser.setLogin(true);
			if (!StringUtils.isBlank(referer)) {
				view = request(super.createRequestPageTempletResponse("redirect:" + referer));
			} else {
				view = request(super.createRequestPageTempletResponse("redirect:/"));
			}
			// 登录成功 判断是否进入多租户页面
			SystemConfig systemConfig = MainUtils.getSystemConfig();
			if (systemConfig != null && systemConfig.isEnabletneant() && systemConfig.isTenantconsole()
					&& !loginUser.isSuperuser()) {
				view = request(super.createRequestPageTempletResponse("redirect:/apps/tenant/index"));
			}
			List<UserRole> userRoleList = userRoleRes.findByOrgiAndUser(loginUser.getOrgi(), loginUser);
			if (userRoleList != null && userRoleList.size() > 0) {
				for (UserRole userRole : userRoleList) {
					loginUser.getRoleList().add(userRole.getRole());
				}
			}

			// 获取用户部门以及下级部门
			organs(loginUser, loginUser.getOrgan()); // 添加部门到myorgans中

			// 获取用户的角色权限，进行授权
			List<RoleAuth> roleAuthList = roleAuthRes.findAll(new Specification<RoleAuth>() {
				@Override
				public Predicate toPredicate(Root<RoleAuth> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					List<Predicate> list = new ArrayList<Predicate>();
					if (loginUser.getRoleList() != null && loginUser.getRoleList().size() > 0) {
						for (Role role : loginUser.getRoleList()) {
							list.add(cb.equal(root.get("roleid").as(String.class), role.getId()));
						}
					}
					Predicate[] p = new Predicate[list.size()];
					cb.and(cb.equal(root.get("orgi").as(String.class), loginUser.getOrgi()));
					return cb.or(list.toArray(p));
				}
			});

			if (roleAuthList != null) {
				for (RoleAuth roleAuth : roleAuthList) {
					loginUser.getRoleAuthMap().put(roleAuth.getDicvalue(), true);
				}
			}

			loginUser.setLastlogintime(new Date());
			if (!StringUtils.isBlank(loginUser.getId())) {
				userRepository.save(loginUser);
			}

			super.setUser(request, loginUser);
			// 当前用户 企业id为空 调到创建企业页面
			if (StringUtils.isBlank(loginUser.getOrgid())) {
				view = request(super.createRequestPageTempletResponse("redirect:/apps/organization/add.html"));
			}
		}
		return view;
	}

	@RequestMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		request.getSession().removeAttribute(MainContext.USER_SESSION_NAME);
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie != null && !StringUtils.isBlank(cookie.getName())
						&& !StringUtils.isBlank(cookie.getValue())) {
					if (cookie.getName().equals(MainContext.UKEFU_SYSTEM_COOKIES_FLAG)) {
						cookie.setMaxAge(0);
						response.addCookie(cookie);
					}
				}
			}
		}
		return "redirect:/";
	}

	@RequestMapping(value = "/register")
	@Menu(type = "apps", subtype = "user", access = true)
	public ModelAndView register(HttpServletRequest request, HttpServletResponse response, @Valid String msg) {
		ModelAndView view = request(super.createRequestPageTempletResponse("redirect:/"));
		if (request.getSession(true).getAttribute(MainContext.USER_SESSION_NAME) == null) {
			view = request(super.createRequestPageTempletResponse("/register"));
		}
		if (!StringUtils.isBlank(msg)) {
			view.addObject("msg", msg);
		}
		return view;
	}

	@RequestMapping("/addAdmin")
	@Menu(type = "apps", subtype = "user", access = true)
	public ModelAndView addAdmin(HttpServletRequest request, HttpServletResponse response, @Valid User user) {
		String msg = "";
		msg = validUser(user);
		if (!StringUtils.isBlank(msg)) {
			return request(super.createRequestPageTempletResponse("redirect:/register.html?msg=" + msg));
		} else {
			user.setUname(user.getUsername());
			user.setUsertype("0");
			if (!StringUtils.isBlank(user.getPassword())) {
//				user.setPassword(MainUtils.md5(user.getPassword()));
				// 因修改登录方法,所以修改加密方法为真MD5算法
				user.setPassword(DigestUtils.md5Hex(user.getPassword()));
			}
			user.setOrgi(super.getOrgiByTenantshare(request));
			/*
			 * if(!StringUtils.isBlank(super.getUser(request).getOrgid())) {
			 * user.setOrgid(super.getUser(request).getOrgid()); }else {
			 * user.setOrgid(MainContext.SYSTEM_ORGI); }
			 */
			userRepository.save(user);
			OnlineUserUtils.clean(super.getOrgi(request));

		}
		ModelAndView view = this.processLogin(request, response,
				request(super.createRequestPageTempletResponse("redirect:/")), user, "");
		// 当前用户 企业id为空 调到创建企业页面
		if (StringUtils.isBlank(user.getOrgid())) {
			view = request(super.createRequestPageTempletResponse("redirect:/apps/organization/add.html"));
		}
		return view;
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
		return msg;
	}

}