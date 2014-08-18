package com.alipay.util;

import java.util.HashMap;
import java.util.Map;

public class AlipayStatus {
	public static final String ALIPAY_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
	public static final String ALIPAY_STATUS_TRADE_CLOSED = "TRADE_CLOSED";
	public static final String ALIPAY_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";
	public static final String ALIPAY_STATUS_TRADE_PENDING = "TRADE_PENDING";
	public static final String ALIPAY_STATUS_TRADE_FINISHED = "TRADE_FINISHED";
	public static final Map<String, Integer> ALIPAY_TRADE_STATUSES = new HashMap<String, Integer>();
	static {
		ALIPAY_TRADE_STATUSES.put(ALIPAY_STATUS_WAIT_BUYER_PAY, 0);
		ALIPAY_TRADE_STATUSES.put(ALIPAY_STATUS_TRADE_FINISHED, 2);
		ALIPAY_TRADE_STATUSES.put(ALIPAY_STATUS_TRADE_SUCCESS, 1);
		ALIPAY_TRADE_STATUSES.put(ALIPAY_STATUS_TRADE_CLOSED, 3);
		ALIPAY_TRADE_STATUSES.put(ALIPAY_STATUS_TRADE_PENDING, 4);
	}
	//商品信息
	public static String FREESCALE_TECHNOLOGY_FORUM_BEIJING = "Freescale Technology Forum ShenZhen 2014";
}