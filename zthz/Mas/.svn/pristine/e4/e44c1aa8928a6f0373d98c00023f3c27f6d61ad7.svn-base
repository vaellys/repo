package cn.zthz.actor.rest;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import cn.zthz.tool.account.AccountException;
import cn.zthz.tool.account.AccountService;
import cn.zthz.tool.account.BalanceNotEnoughException;
import cn.zthz.tool.common.HzException;
import cn.zthz.tool.common.LogUtils;
import cn.zthz.tool.common.XmlUtils;

public class AccountRest extends FunctionalRest {

	/**
	 * 充值
	 * 
	 * @param request
	 * @param response
	 */
	public void charge(HttpServletRequest request, HttpServletResponse response) {

	}

	/**
	 * 提现
	 * 
	 * @param request
	 * @param response
	 */
	public void withdraw(HttpServletRequest request, HttpServletResponse response) {
		String userId = request.getParameter(USER_ID);

		Integer money = getInt(request, response, "money", true);
		String password = getString(request, response, "accountPassword", true);
		try {
			AccountService.instance.withdraw(userId, new BigDecimal(money), password);
			putSuccess(request, response);
		} catch (BalanceNotEnoughException e) {
			log.error("余额不足", e);
			putError(request, response, "余额不足", ErrorCodes.ACCOUNT_NOT_ENOUGH_BALANCE);
		} catch (AccountException e) {
			log.error("用户账户错误", e);
			putError(request, response, e.getMessage(), e.errorCode());
		}
	}

	public void info(HttpServletRequest request, HttpServletResponse response) {
		String userId = request.getParameter(USER_ID);
		try {
			putJson(request, response, AccountService.instance.getBasicInfo(userId));
		} catch (AccountException e) {
			log.error("", e);
			putError(request, response, e.getMessage(), ErrorCodes.ACCOUNT_EXCEPTION);
		}
	}

	public void history(HttpServletRequest request, HttpServletResponse response) {

	}

	/**
	 * 
	 "charset":["UTF-8"], "cupReserved":["{cardNumber=6222023602899998371}"],
	 * "exchangeDate":[""], "exchangeRate":[""], "merAbbr":["????"],
	 * "merId":["105550149170027"], "orderAmount":["3100"],
	 * "orderCurrency":["156"], "orderNumber":["201301291633552"],
	 * "qid":["201301291633557601012"], "respCode":["00"],
	 * "respMsg":["Success!"], "respTime":["20130129163008"],
	 * "settleAmount":["3100"], "settleCurrency":["156"], "settleDate":["0129"],
	 * "signMethod":["MD5"], "signature":["d0202376262be07271ab174c04a2297f"],
	 * "traceNumber":["760101"], "traceTime":["0129163355"], "transType":["01"],
	 * "version":["1.0.0"]
	 * 
	 * @param request
	 * @param response
	 */
	public void unionPayCallback(HttpServletRequest request, HttpServletResponse response) {
		log.info(LogUtils.format("params", request.getParameterMap()));
		putSuccess(request, response);
	}

	/**
	 * notify_data= <notify> <trade_status>TRADE_FINISHED</trade_status>
	 * <total_fee>0.90</total_fee> <subject>123456</subject> <out_trade_no>
	 * 1118060201-7555 </out_trade_no>
	 * <notify_reg_time>2010-11-1814:02:43.000</notify_reg_time>
	 * <trade_no>2010111800209965</trade_no> </notify> "sign":
	 * "TI06n6PougFHGRaNR8G6eYZEG/10OFVtOZLUnWA/5iq9hVe8V/40DryRJOh9fWuRK6uQmGs6itHggrqu0bynaRtvjxV+iNq0qrbE"
	 * ,"sign_type":"RSA"
	 * <notify><is_total_fee_adjust>Y</is_total_fee_adjust><price
	 * >0.10</price><out_trade_no
	 * >f25a6fa0af70426c9f28abf490c229ec</out_trade_no><gmt_create>2013-03-07
	 * 15:
	 * 39:04</gmt_create><seller_id>2088901028202974</seller_id><subject>账户充值<
	 * /subject
	 * ><trade_status>WAIT_BUYER_PAY</trade_status><discount>0.00</discount
	 * ></notify>
	 * 
	 * @param request
	 * @param response
	 *            success | failure
	 */
	public void alipayCallback(HttpServletRequest request, HttpServletResponse response) {
		// log.info(LogUtils.format("params", request.getParameterMap()));
		String notifyData = getString(request, response, "notify_data", true);
		String sign = getString(request, response, "sign", true);
		String signType = getString(request, response, "sign_type", true);
		try {
			AccountService.instance.alipayChargeCallback(notifyData, sign, signType);
			putJson(request, response, "success");
		} catch (HzException e) {
			log.error(e.getMessage(), e);
			putError(request, response, e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			putError(request, response, "failed to charge", ErrorCodes.SERVER_INNER_ERROR);
		}

	}

	public void getAlipayTradeNumber(HttpServletRequest request, HttpServletResponse response) {
		String userId = request.getParameter(USER_ID);
		try {
			putJson(request, response, genJson("outTradeNumber", AccountService.instance.getAlipayTradeNumber(userId)));
		} catch (HzException e) {
			log.error("", e);
			putError(request, response, e.getMessage(), e.errorCode());
		} catch (Exception e) {
			log.error("", e);
			putError(request, response, e);
		}
	}

	DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

	private Map<String, String> parseAlipayNotify(String data) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(data);
		document.getChildNodes();

		return null;
	}
}
