package com.chatopera.cc.constant;

/**
 * 公共常量
 * @author Wayne
 */
public class CommonConstant {

	//////////----->状态常量    by Wayne on 2019/9/13 18:51   start--->
	public static final int DELETE_STATUS = 0;
	public static final int NORMAL_STATUS = 1;
	public static final int LOCKED_STATUS = 2;
	//////////<-----状态常量    by Wayne on 2019/9/13 18:51    <---end


	//////////----->用户相关常量    by Wayne on 2019/9/13 18:52   start--->
	// usertype---> 0 Admin User  : !0  Other User
	public static final String ADMIN_USER_TYPE = "0";
	//////////<-----用户相关常量    by Wayne on 2019/9/13 18:52    <---end


	//////////----->支付相关    by Wayne on 2019/9/17 15:44   start--->
	public static final String ALIPAY_NAME = "支付宝";
	public static final String WECHATPAY_NAME = "微信";
	public static final String BANKCANDPAY_NAME = "银行";

	public static final String RECHARGE_AUTO_REPLY = "请点击下方小卡片进行充值，充值成功后请把截图发我一下，我给您加金币哦！";

//	public static final String PAY_IMG_LABLE = "<a href='payInfoView.html?kefuPin=%s&payFunctionId=%s'><img src='%s' height='100'></a>";

	public static final String PAY_IMG_LABLE = "<img src='%s' height='100' onclick=\"openLayer('payInfoView.html?kefuPin=%s&payFunctionId=%s')\">";

	public static final String ALIPAY_IMG_URL = "/images/pay/AliPay.png";
	public static final String WECHATPAY_IMG_URL = "/images/pay/WeChatPay.png";
	public static final String BANKCANDPAY_IMG_URL = "/images/pay/BankCardPay.png";
	//////////<-----支付相关    by Wayne on 2019/9/17 15:44    <---end


	//////////----->路径相关    by Wayne on 2019/9/20 17:37   start--->
	// 客服头像路径
	public static final String KEFU_AVATAR_PATH = "static/images/user";
	// 玩家头像路径
	public static final String PLAYER_AVATAR_PATH = "static/images/player";
	//////////<-----路径相关    by Wayne on 2019/9/20 17:37    <---end


	//////////----->颜色相关    by Wayne on 2019/9/25 10:55   start--->
	public static final String ALIPAY_COLOR = "#0088CC ";
	public static final String WECHATPAY_COLOR = "#00B300 ";
	public static final String BANKCANDPAY_COLOR = "#D2453C";
	//////////<-----颜色相关    by Wayne on 2019/9/25 10:55    <---end


}
