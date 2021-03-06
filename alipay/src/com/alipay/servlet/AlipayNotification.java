package com.alipay.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.eclipse.jdt.internal.compiler.lookup.UpdatedMethodBinding;

import com.alipay.dao.AlipayTradeDao;
import com.alipay.dao.PaymentRecorderDao;
import com.alipay.model.AlipayTrade;
import com.alipay.util.AlipayNotify;
import com.alipay.util.AlipayStatus;

/**
 * Servlet implementation class AlipayNotify
 */
public class AlipayNotification extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		// 获取支付宝POST过来反馈信息
		Map<String, String> params = new HashMap<String, String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			// 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			// valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
			params.put(name, valueStr);
		}

		// 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
		// 商户订单号

		String out_trade_no = request.getParameter("out_trade_no");

		// 支付宝交易号

		String trade_no = request.getParameter("trade_no");

		// 交易状态
		String trade_status = request.getParameter("trade_status");

		// 确认号
		PaymentRecorderDao dao;
		String cfNum = null;
		try {

			if (AlipayNotify.verify(params)) {// 验证成功
				System.out.println("yan zheng success!");
				dao = new PaymentRecorderDao();
				cfNum = (String) (dao.getCfNumberByOutTradeNu(out_trade_no))
						.get("confirmation_number");
				dao.updateByTradeNo(out_trade_no, trade_no);
				if (trade_status.equals("TRADE_SUCCESS")) {
					// 判断该笔订单是否在商户网站中已经做过处理
					// 如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
					// 如果有做过处理，不执行商户的业务程序

					// 注意：
					// 该种交易状态只在两种情况下出现
					// 1、开通了普通即时到账，买家付款成功后。
					// 2、开通了高级即时到账，从该笔交易成功时间算起，过了签约时的可退款时限（如：三个月以内可退款、一年以内可退款等）后。
					sendRequest(
							cfNum,
							AlipayStatus.ALIPAY_TRADE_STATUSES
									.get(AlipayStatus.ALIPAY_STATUS_TRADE_SUCCESS), out_trade_no, dao);
					dao.updateByOutTradeNu(
							out_trade_no,
							AlipayStatus.ALIPAY_TRADE_STATUSES
									.get(AlipayStatus.ALIPAY_STATUS_TRADE_SUCCESS));
				} else if (trade_status.equals("WAIT_BUYER_PAY")) {
					// 判断该笔订单是否在商户网站中已经做过处理
					// 如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
					// 如果有做过处理，不执行商户的业务程序

					// 注意：
					// 该种交易状态只在一种情况下出现——开通了高级即时到账，买家付款成功后。
					sendRequest(
							cfNum,
							AlipayStatus.ALIPAY_TRADE_STATUSES
									.get(AlipayStatus.ALIPAY_STATUS_WAIT_BUYER_PAY),out_trade_no,dao);
					dao.updateByOutTradeNu(
							out_trade_no,
							AlipayStatus.ALIPAY_TRADE_STATUSES
									.get(AlipayStatus.ALIPAY_STATUS_WAIT_BUYER_PAY));
				} else if (trade_status.equals("TRADE_PENDING")) {
					sendRequest(
							cfNum,
							AlipayStatus.ALIPAY_TRADE_STATUSES
									.get(AlipayStatus.ALIPAY_STATUS_WAIT_BUYER_PAY),out_trade_no, dao);
					dao.updateByOutTradeNu(
							out_trade_no,
							AlipayStatus.ALIPAY_TRADE_STATUSES
									.get(AlipayStatus.ALIPAY_STATUS_TRADE_PENDING));
				} else if (trade_status.equals("TRADE_CLOSED")) {
					sendRequest(
							cfNum,
							AlipayStatus.ALIPAY_TRADE_STATUSES
									.get(AlipayStatus.ALIPAY_STATUS_WAIT_BUYER_PAY),out_trade_no, dao);
					dao.updateByOutTradeNu(
							out_trade_no,
							AlipayStatus.ALIPAY_TRADE_STATUSES
									.get(AlipayStatus.ALIPAY_STATUS_TRADE_CLOSED));
				} else if (trade_status.equals("TRADE_FINISHED")) {
					sendRequest(
							cfNum,
							AlipayStatus.ALIPAY_TRADE_STATUSES
									.get(AlipayStatus.ALIPAY_STATUS_TRADE_SUCCESS),out_trade_no, dao);
					dao.updateByOutTradeNu(
							out_trade_no,
							AlipayStatus.ALIPAY_TRADE_STATUSES
									.get(AlipayStatus.ALIPAY_STATUS_TRADE_FINISHED));
				}
				out.println("success");	
				response.sendRedirect("payment_success.html");

			} else {// 验证失败
				out.println("fail");
				System.out.println("yan zheng failed!");
				response.sendRedirect("payment_fail.html");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendRequest(String cfNum, Integer tradeStatus, String out_trade_no, PaymentRecorderDao dao) {
		if (null != cfNum) {
			String url = "https://getregisterednow.com/FTFChina/reports/setpayment.asp?confirmationNumber='"+cfNum+"'&paymentStatus="+tradeStatus;
//			HttpClient client = new HttpClient();
//			HttpMethod method = new PostMethod(
//					"https://getregisterednow.com/FTFChina/reports/setpayment.asp");
//			method.setQueryString("confirmationNumber=" + cfNum);
//			method.setQueryString("paymentStatus=" + tradeStatus);
			try {
//				client.executeMethod(method);
				String result = dao.doGet(url, "UTF-8");
				int s = result.lastIndexOf("<body>");
				int e = result.indexOf("</body>");
				String statusNo = result.substring(s + 6, e).trim();
				Map<String, Object> args = new HashMap<String, Object>();
				args.put("status_no", statusNo);
				dao.update(out_trade_no, args);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

}
