package cn.zthz.tool.common;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Formats {

	public static BigDecimal formatMoney(BigDecimal bigDecimal) {
		return bigDecimal.setScale(2, RoundingMode.DOWN);
	}
	
	public static void main(String[] args) {
		System.out.println(formatMoney(new BigDecimal("1")));
	}

}
