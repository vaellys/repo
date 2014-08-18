package cn.zthz.actor.rest;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Security;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import cn.zthz.actor.assemble.GlobalConfig;
import cn.zthz.tool.account.AccountException;
import cn.zthz.tool.account.AccountService;
import cn.zthz.tool.common.JsonUtils;
import cn.zthz.tool.common.RSASignature;
import cn.zthz.tool.common.StringUtils;


/**
 * 
 * 安全支付服务器端处理程序
 * 
 * 1.将业务参数：合作商户ID、 外部交易号、商品名称、商品的具体描述、商品总价、卖家帐户、notify_url这些参数按照固定顺序签名
 * 2.将签名结果返回客户端
 * 
 **/
public class AlipayRest extends FunctionalRest{

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	public void getAlipayTrade(HttpServletRequest request, HttpServletResponse response) throws IOException, AccountException {
//		String basePath = "";
//		System.out.println("request in");
//		response.setCharacterEncoding("utf-8");
//		PrintWriter out = response.getWriter();
		// 得到应用服务器地址
		String path = request.getContextPath();
		String basePath ="http://" + GlobalConfig.get("server.outIp")+ "/alipay/notifyReceiver.json";

		String strReString = "";
		// 检查商户PartnerConfig.java文件的配置参数
		if (!checkInfo()) {
			strReString = "<result><is_success>F</is_success><error>缺少partner或者seller,"
					+ "请在com/alipay/client/base/PartnerConfig.java中增加</error></result>";
//			out.print(strReString);
			putJson(request, response, strReString);
			return;
		}

		String signData = getSignDate(request,response , basePath);
		String sign = sign(signData, PartnerConfig.RSA_PRIVATE);
		// 返回待签名数据和签名数据
//		strReString = "<result><is_success>T</is_success><content>" + signData + "</content><sign>" + sign + "</sign></result>";
		strReString = signData + "&sign_type=\"RSA\"&sign=\"" + URLEncoder.encode(sign, "utf-8") +"\"";
		// 对返回客户端的数据encode
//		out.print(URLEncoder.encode(strReString, "utf-8"));
		System.out.println(strReString);
		putJson(request, response, strReString);
//		putJson(request, response, URLEncoder.encode(strReString, "utf-8"));

//		return;
	}

	// 检查商户PartnerConfig.java文件的配置参数
	private boolean checkInfo() {
		String partner = PartnerConfig.PARTNER;
		String seller = PartnerConfig.SELLER;
		// 如果合作商户ID为空或者账号ID为空返回false
		if (StringUtils.isBlank(partner) || StringUtils.isBlank(seller))
			return false;

		return true;
	}

	/**
	 * 准备待签名的数据
	 * 
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws AccountException 
	 */
	private String getSignDate(HttpServletRequest request ,HttpServletResponse response, String notify_url) throws UnsupportedEncodingException, AccountException {
		request.setCharacterEncoding("utf-8");
		checkUserToken(request, response);
		String userId = request.getParameter(USER_ID).trim();
		// 合作商户ID
		String partner = PartnerConfig.PARTNER;
		// 卖家帐号
		String seller = PartnerConfig.SELLER;
		// 外部交易号 这里取当前时间，商户可根据自己的情况修改此参数，但保证唯一性
//		String outTradeNo = System.currentTimeMillis() + "";
		String outTradeNo = AccountService.instance.getAlipayTradeNumber(userId);
		// 商品名称
//		String subject = request.getParameter("subject").trim();
		String subject = getString(request, response, "subject",true).trim();
		// 商品具体描述
//		String body = request.getParameter("body").trim();
		String body = getString(request, response, "body",true).trim();
		// 商品总价
//		String totalFee = request.getParameter("total_fee").trim();
		String totalFee = getString(request, response, "total_fee",true).trim();
		// 接收支付宝发送的通知的url 商户可根据自己的情况修改此参数
//		String notify_url = basePath+ "servlet/RSANotifyReceiver";

		// 组装待签名数据
		String signData = "partner=" + "\"" + partner + "\"";
		signData += "&";
		signData += "seller=" + "\"" + seller + "\"";
		signData += "&";
		signData += "out_trade_no=" + "\"" + outTradeNo + "\"";
		signData += "&";
		signData += "subject=" + "\"" + subject + "\"";
		signData += "&";
		signData += "body=" + "\"" + body + "\"";
		signData += "&";
		signData += "total_fee=" + "\"" + totalFee + "\"";
		signData += "&";
		signData += "notify_url=" + "\"" + URLEncoder.encode(notify_url ,"utf-8")+ "\"";
		return signData;
	}

	/**
	 * 对参数进行签名
	 * 
	 * @param signData
	 *            待签名数据，key rsa商户私钥
	 * @return
	 */
	private String sign(String signData, String key) {
//		System.out.println("signData:" + signData);
		String sign = "";
		try {
			sign = RSASignature.sign(signData, key);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return sign;
	}
	
	/**
	 * 接收通知并处理
	 * 
	 * @author 3y
	 * @version $Id: NotifyReceiver.java, v 0.1 2011-8-15 下午03:11:58 3y Exp $
	 */
	public void notifyReceiver(HttpServletRequest request, HttpServletResponse response) throws IOException {

//    	System.out.println("接收到通知!");
        //获得通知参数
        Map map = request.getParameterMap();
        log.info("alipy callback:"+JsonUtils.toJsonString(map));
        //获得通知签名
//        String sign = (String) ((Object[]) map.get("sign"))[0];
        //获得待验签名的数据
        String notifyData = getString(request, response, "notify_data", true);
        String signType = getString(request, response, "sign_type", false);
        String sign = getString(request, response, "sign", false);
        String verifyData = getVerifyData(notifyData);
        //使用支付宝公钥验签名
        try {
           if(RSASignature.doCheck(verifyData, sign, PartnerConfig.RSA_ALIPAY_PUBLIC)){
        	   AccountService.instance.alipayChargeCallback(notifyData, sign, signType);
        	   log.info("charge success");
        	   putJson(request, response, "success");
           }else{
        	   log.error("charge failed");
        	   putJson(request, response, "fail");
           }
        } catch (Exception e) {
        	log.error("charge failed" ,e);
        	putJson(request, response, "fail");
            return;
        }
    }
	public static void main(String[] args) {
		String verifyData = "<notify><seller_email>bjzthzkjyxgs@163.com</seller_email><partner>2088901028202974</partner><payment_type>1</payment_type><buyer_email>18210165894</buyer_email><trade_no>2013030847678235</trade_no><buyer_id>2088702430259358</buyer_id><quantity>1</quantity><total_fee>0.01</total_fee><use_coupon>N</use_coupon><is_total_fee_adjust>Y</is_total_fee_adjust><price>0.01</price><out_trade_no>ad1358557aa348deaeed1bb7b060a31d</out_trade_no><gmt_create>2013-03-08 15:18:32</gmt_create><seller_id>2088901028202974</seller_id><subject>账户充值</subject><trade_status>WAIT_BUYER_PAY</trade_status><discount>0.00</discount></notify>";
		String sign="PK8yII9bmHO17syrtwaOkiVbqSeUptUfKiblzue0fS6zzlRvwaZSXtZxYHADdGZZxkB8yppaKsEHBnOg/icfSaowAcV3m49Gughf5xQSs8lcfLMDBR4ArproKVMDFG++TPByk6hShCJ/z8EBLqbjyugBgKYZHfgUPvAI4l+tx7o=";
		System.out.println(RSASignature.doCheck(getVerifyData(verifyData), sign, PartnerConfig.RSA_ALIPAY_PUBLIC));
	}
    /**
     * 获得验签名的数据
     * @param map
     * @return
     * @throws Exception 
     */
	private static String getVerifyData(String notifyData) {
		return "notify_data="+notifyData;
	}
//    private String getVerifyData(Map map) {
//    	String notify_data = (String) ((Object[]) map.get("notify_data"))[0];
//    	return "notify_data="+notify_data;
//    }
    
    //签名成功
    public static final int RESULT_CHECK_SIGN_FAILED = 1;
    //签名失败
    public static final int RESULT_CHECK_SIGN_SUCCEED =2;
    /**
     * 安全支付支付完成商户同步处理程序
     * 对支付宝返回订单信息验签名
     * @author 3y
     * @version $Id: RSACallBack.java, v 0.1 2011-8-16 下午05:16:26 3y Exp $
     */
	public void verifySign(HttpServletRequest request, HttpServletResponse response)throws UnsupportedEncodingException {
        //获得待签名数据和签名值
        String sign = URLDecoder.decode(request.getParameter("sign"),"utf-8");
        String content = URLDecoder.decode(request.getParameter("content"),"utf-8");
        
        int retVal = RESULT_CHECK_SIGN_FAILED;
        //使用支付宝公钥验签名
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
            System.out.println("验签名失败");
        }

    }

}
