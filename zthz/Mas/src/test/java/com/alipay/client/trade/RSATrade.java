/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2008 All Rights Reserved.
 */
package com.alipay.client.trade;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Security;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.alipay.client.base.PartnerConfig;
import com.alipay.client.security.RSASignature;
import com.alipay.client.util.StringUtil;

/**
 * 
 * ��ȫ֧���������˴������
 * 
 * 1.��ҵ������������̻�ID�� �ⲿ���׺š���Ʒ���ơ���Ʒ�ľ�����������Ʒ�ܼۡ������ʻ���notify_url��Щ�������չ̶�˳��ǩ��
 * 2.��ǩ��������ؿͻ���
 * @author 3Y
 */
public class RSATrade extends HttpServlet {

	private static final long serialVersionUID = -3035307235076650766L;
	static {
	      Security.addProvider(new BouncyCastleProvider());
	   }
	String basePath="";
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
	    System.out.println("request in");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter();
	    //�õ�Ӧ�÷�������ַ
	    String path = request.getContextPath();
	    basePath = request.getScheme() + "://" + request.getLocalAddr() + ":"
	                               + request.getServerPort() + path + "/";
	    
		String strReString="";
		//����̻�PartnerConfig.java�ļ������ò���
		if(!checkInfo()){
		    strReString="<result><is_success>F</is_success><error>ȱ��partner����seller," +
		    		"����com/alipay/client/base/PartnerConfig.java������</error></result>";
		    out.print(strReString);
		    return;
		}
		
		String signData=getSignDate(request);
		String sign = sign(signData,PartnerConfig.RSA_PRIVATE);
		//���ش�ǩ�����ݺ�ǩ������
		strReString="<result><is_success>T</is_success><content>"+signData+"</content><sign>"+sign+"</sign></result>";
		//�Է��ؿͻ��˵�����encode
		out.print(URLEncoder.encode(strReString,"utf-8")); 

        return;
	}
	
	

	//����̻�PartnerConfig.java�ļ������ò���
	private boolean checkInfo() {
        String partner = PartnerConfig.PARTNER;
        String seller = PartnerConfig.SELLER;
        //��������̻�IDΪ�ջ����˺�IDΪ�շ���false
        if (StringUtil.isBlank(partner) || StringUtil.isBlank(seller))
            return false;
        
        return true;
    }
	
	
	/**
	 * ׼����ǩ��������
	 * 
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String getSignDate(
			HttpServletRequest request) throws UnsupportedEncodingException {
		request.setCharacterEncoding("utf-8");
		
        //�����̻�ID
        String partner = PartnerConfig.PARTNER;
        //�����ʺ�
        String seller = PartnerConfig.SELLER;
		// �ⲿ���׺� ����ȡ��ǰʱ�䣬�̻��ɸ����Լ�������޸Ĵ˲���������֤Ψһ��
		String outTradeNo = System.currentTimeMillis() + "";
		// ��Ʒ����
        String subject = request.getParameter("subject").trim();
        // ��Ʒ��������
        String body = request.getParameter("body").trim();
        // ��Ʒ�ܼ�
        String totalFee = request.getParameter("total_fee").trim();
		// ����֧�������͵�֪ͨ��url �̻��ɸ����Լ�������޸Ĵ˲���
		String notify_url = basePath+"servlet/RSANotifyReceiver";
		
		//��װ��ǩ������
		String signData = "partner=" + "\"" + partner + "\"";
		signData += "&";
		signData += "seller=" + "\"" + seller + "\"";
		signData += "&";
		signData += "out_trade_no=" + "\"" + outTradeNo + "\"";
		signData += "&";
		signData += "subject=" + "\"" + subject+ "\"";
		signData += "&";
		signData += "body=" + "\"" + body + "\"";
		signData += "&";
		signData += "total_fee=" + "\""+ totalFee + "\"";
		signData += "&";
		signData += "notify_url=" + "\""+notify_url+ "\"";
		return signData;
	}

	
	
	/**
	 * �Բ�������ǩ��
	 * 
	 * @param signData ��ǩ�����ݣ�key rsa�̻�˽Կ
	 * @return
	 */
	private String sign(String signData,String key) {
		System.out.println("signData:"+signData);
		String sign = "";
		try {
			sign = RSASignature.sign(signData, key);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return sign;
	}

	
}
