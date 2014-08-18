/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2008 All Rights Reserved.
 */
package com.alipay.client.notify;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alipay.client.base.PartnerConfig;
import com.alipay.client.security.RSASignature;

/**
 * ����֪ͨ������
 * 
 * @author 3y
 * @version $Id: NotifyReceiver.java, v 0.1 2011-8-15 ����03:11:58 3y Exp $
 */
public class RSANotifyReceiver extends HttpServlet {


    private static final long                          serialVersionUID = 7216412938937049671L;

    @SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    	System.out.println("���յ�֪ͨ!");
        //���֪ͨ����
        Map map = request.getParameterMap();
        //���֪ͨǩ��
        String sign = (String) ((Object[]) map.get("sign"))[0];
        //��ô���ǩ��������
        String verifyData = getVerifyData(map);
        boolean verified = false;
        //ʹ��֧������Կ��ǩ��
        try {
            verified = RSASignature.doCheck(verifyData, sign, PartnerConfig.RSA_ALIPAY_PUBLIC);
        } catch (Exception e) {
            e.printStackTrace();
        }
        PrintWriter out = response.getWriter();
        //��֤ǩ��ͨ��
        if (verified) {
        	//���ݽ���״̬����ҵ���߼�
        	//������״̬�ɹ�������ҵ���߼��ɹ�����дsuccess
        	
        	out.print("success");
        } else {
        	System.out.println("����֧����ϵͳ֪ͨ��֤ǩ��ʧ�ܣ����飡");
            out.print("fail");
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doPost(request, response);
    }

    /**
     * �����ǩ��������
     * @param map
     * @return
     * @throws Exception 
     */
    @SuppressWarnings("unchecked")
	private String getVerifyData(Map map) {
        String notify_data = (String) ((Object[]) map.get("notify_data"))[0];
        return "notify_data="+notify_data;
    }
}
