package cn.zthz.actor.rest;

import cn.zthz.tool.common.ConfigUtils;

/**
 * 账户名：bjzthzkjyxgs@163.com
账户ID：2088901028202974
合作商户ID：2088901028202974
 * @author uzoice
 *
 */
public class PartnerConfig {
	// 合作商户ID。用签约支付宝账号登录ms.alipay.com后，在账户信息页面获取。
	public static final String PARTNER = "2088901028202974";
	// 账户ID。用签约支付宝账号登录ms.alipay.com后，在账户信息页面获取。
	public static final String SELLER = "2088901028202974";
	// 商户（RSA）私钥
	public static final String RSA_PRIVATE = ConfigUtils.getStringFileFromClasspath("rsa_private_key.pem");
	// 支付宝（RSA）公钥 用签约支付宝账号登录ms.alipay.com后，在密钥管理页面获取。
	public static final String RSA_ALIPAY_PUBLIC = ConfigUtils.getStringFileFromClasspath("rsa_alipay_public.key");
	
	public static final String PUBLIC_KEY = ConfigUtils.getStringFileFromClasspath("rsa_public_key.pem");
	public static final String PRIVATE_KEY = ConfigUtils.getStringFileFromClasspath("rsa_private_key.pem");
	public static final String PRIVATE_KEY_PKCS8 = ConfigUtils.getStringFileFromClasspath("pkcs8-private-key.txt");

}
