/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2008 All Rights Reserved.
 */
package com.alipay.client.callback;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alipay.client.base.PartnerConfig;
import com.alipay.client.security.RSASignature;

/**
 * ��ȫ֧��֧������̻�ͬ���������
 * ��֧�������ض�����Ϣ��ǩ��
 * @author 3y
 * @version $Id: RSACallBack.java, v 0.1 2011-8-16 ����05:16:26 3y Exp $
 */
public class RSACallBack extends HttpServlet {


    private static final long                          serialVersionUID = -2234271646410251381L;
    
    //ǩ���ɹ�
    public static final int RESULT_CHECK_SIGN_FAILED = 1;
    //ǩ��ʧ��
    public static final int RESULT_CHECK_SIGN_SUCCEED =2;
    
    @SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest request, HttpServletResponse response)throws UnsupportedEncodingException {
        //��ô�ǩ�����ݺ�ǩ��ֵ
        String sign = URLDecoder.decode(request.getParameter("sign"),"utf-8");
        String content = URLDecoder.decode(request.getParameter("content"),"utf-8");
        
        int retVal = RESULT_CHECK_SIGN_FAILED;
        //ʹ��֧������Կ��ǩ��
        try {
            PrintWriter out = response.getWriter();
            if(RSASignature.doCheck(content, sign, PartnerConfig.RSA_ALIPAY_PUBLIC)){
                retVal=RESULT_CHECK_SIGN_SUCCEED;
            }
            
            response.setContentType("text/html");
            out.print(retVal);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("��ǩ��ʧ��");
        }

    }
}
