package cn.zthz.actor.rest;

import cn.zthz.tool.common.ConfigUtils;

/**
 * �˻�����bjzthzkjyxgs@163.com
�˻�ID��2088901028202974
�����̻�ID��2088901028202974
 * @author uzoice
 *
 */
public class PartnerConfig {
	// �����̻�ID����ǩԼ֧�����˺ŵ�¼ms.alipay.com�����˻���Ϣҳ���ȡ��
	public static final String PARTNER = "2088901028202974";
	// �˻�ID����ǩԼ֧�����˺ŵ�¼ms.alipay.com�����˻���Ϣҳ���ȡ��
	public static final String SELLER = "2088901028202974";
	// �̻���RSA��˽Կ
	public static final String RSA_PRIVATE = ConfigUtils.getStringFileFromClasspath("rsa_private_key.pem");
	// ֧������RSA����Կ ��ǩԼ֧�����˺ŵ�¼ms.alipay.com������Կ����ҳ���ȡ��
	public static final String RSA_ALIPAY_PUBLIC = ConfigUtils.getStringFileFromClasspath("rsa_alipay_public.key");
	
	public static final String PUBLIC_KEY = ConfigUtils.getStringFileFromClasspath("rsa_public_key.pem");
	public static final String PRIVATE_KEY = ConfigUtils.getStringFileFromClasspath("rsa_private_key.pem");
	public static final String PRIVATE_KEY_PKCS8 = ConfigUtils.getStringFileFromClasspath("pkcs8-private-key.txt");

}
