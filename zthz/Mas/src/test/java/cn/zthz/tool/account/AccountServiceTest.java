package cn.zthz.tool.account;

import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import cn.zthz.tool.common.LogUtils;
import cn.zthz.tool.common.RsaUtils;
import cn.zthz.tool.db.Connections;
import cn.zthz.tool.requirement.ConnectionUtils;

public class AccountServiceTest {

	@Test
	public void testUnionPayChargeCallback() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetBasicInfo() throws AccountException {
		System.out.println(AccountService.instance.getBasicInfo("1"));
	}

	@Test
	public void testGetBalance() throws AccountException {
		Assert.assertEquals(new BigDecimal("1.57"), AccountService.instance.getBalance("1"));
	}

	@Test
	public void testWithdraw() throws AccountException {
		BigDecimal money = new BigDecimal(0.5);
		AccountService.instance.withdraw("1", money, "1");
	}

	@Test
	public void testAlipayChargeCallback() throws AccountException {
//		String verifyData = "<notify><seller_email>bjzthzkjyxgs@163.com</seller_email><partner>2088901028202974</partner><payment_type>1</payment_type><buyer_email>18210165894</buyer_email><trade_no>2013030847678235</trade_no><buyer_id>2088702430259358</buyer_id><quantity>1</quantity><total_fee>0.01</total_fee><use_coupon>N</use_coupon><is_total_fee_adjust>Y</is_total_fee_adjust><price>0.01</price><out_trade_no>ad1358557aa348deaeed1bb7b060a31d</out_trade_no><gmt_create>2013-03-08 15:18:32</gmt_create><seller_id>2088901028202974</seller_id><subject>账户充值</subject><trade_status>WAIT_BUYER_PAY</trade_status><discount>0.00</discount></notify>";
		String verifyData = "<notify><seller_email>bjzthzkjyxgs@163.com</seller_email><partner>2088901028202974</partner><payment_type>1</payment_type><buyer_email>18210165894</buyer_email><trade_no>2013030847678235</trade_no><buyer_id>2088702430259358</buyer_id><quantity>1</quantity><total_fee>0.01</total_fee><use_coupon>N</use_coupon><is_total_fee_adjust>Y</is_total_fee_adjust><price>0.01</price><out_trade_no>ad1358557aa348deaeed1bb7b060a31d</out_trade_no><gmt_create>2013-03-08 15:18:32</gmt_create><seller_id>2088901028202974</seller_id><subject>账户充值</subject><trade_status>TRADE_FINISHED</trade_status><discount>0.00</discount></notify>";
		String sign="PK8yII9bmHO17syrtwaOkiVbqSeUptUfKiblzue0fS6zzlRvwaZSXtZxYHADdGZZxkB8yppaKsEHBnOg/icfSaowAcV3m49Gughf5xQSs8lcfLMDBR4ArproKVMDFG++TPByk6hShCJ/z8EBLqbjyugBgKYZHfgUPvAI4l+tx7o=";
//		String sign = RsaUtils.encrypt("123456", AccountService.PUBLIC_KEY);
//		String signType = "RSA";
		// Map<String, String> data = new HashMap<>();
		// data.put("trade_status" ,
		// AccountService.ALIPAY_STATUS_TRADE_FINISHED);
		// data.put("total_fee" , "1.2");
		// data.put("userId" , "1");
		AccountService.instance.alipayChargeCallback(verifyData, sign, "RSA");
	}

	@Test
	public void testCreateAccount() throws AccountException {
		AccountService.instance.createAccount("5", "password", 0, "1");
	}

	@Test
	public void testViewTranactionHistory() throws AccountException {

		List<Map<String, Object>> result = AccountService.instance.viewTranactionHistory("2", 0, 10);
		System.out.println(LogUtils.format("result", result));
	}

	@Test
	public void testViewAccount() {
		fail("Not yet implemented");
	}

	@Test
	public void testSet() throws SQLException, AccountException {
		Connection connection = Connections.instance.get();
		Statement statement = connection.createStatement();
		try {
			connection.setAutoCommit(false);
			AccountService.instance.setPrepayTargetAccount(statement, "1", "402880173c898ea3013c8a9db630000b");
			connection.commit();
		} catch (Exception e) {
			ConnectionUtils.rollback(connection);
		} finally {
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}

	}

	@Test
	public void testPrePay() throws SQLException, AccountException {
		Connection connection = Connections.instance.get();
		Statement statement = connection.createStatement();
		try {
			connection.setAutoCommit(false);
			AccountService.instance.prePay(statement, "2", new BigDecimal("0.14"), "402880173c898ea3013c8a9db630000b");
			connection.commit();
		} catch (Exception e) {
			ConnectionUtils.rollback(connection);
		} finally {
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}

	}

	@Test
	public void testPrePay2() throws SQLException, AccountException {
		Connection connection = Connections.instance.get();
		Statement statement = connection.createStatement();
		try {
			connection.setAutoCommit(false);
			AccountService.instance.prePay(statement, "2", new BigDecimal("0.14"), "402880173c898ea3013c8");
			connection.commit();
		} catch (Exception e) {
			ConnectionUtils.rollback(connection);
		} finally {
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}

	}

	@Test
	public void testPay() throws SQLException, AccountException {
		Connection connection = Connections.instance.get();
		Statement statement = connection.createStatement();
		try {
			connection.setAutoCommit(false);
			AccountService.instance.pay(statement, "402880173c898ea3013c8a9db630000b", "0");
			connection.commit();
		} catch (Exception e) {
			System.out.println("failed to pay ... , rollback for this tranaction; args=....");
			ConnectionUtils.rollback(connection);
		} finally {
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}

	@Test
	public void testPay2() throws SQLException, AccountException {
		Connection connection = Connections.instance.get();
		Statement statement = connection.createStatement();
		try {
			connection.setAutoCommit(false);
			AccountService.instance.pay(statement, "402880173c898ea3013c8a9d", "0");
			connection.commit();
		} catch (Exception e) {
			System.out.println("failed to pay ... , rollback for this tranaction; args=....");
			ConnectionUtils.rollback(connection);
		} finally {
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}

	@Test(expected = Exception.class)
	public void testPayback() throws Exception {
		Connection connection = Connections.instance.get();
		Statement statement = connection.createStatement();
		try {
			connection.setAutoCommit(false);
			AccountService.instance.payback(statement, "402880173c898ea3013c8a9db630000b");
			connection.commit();
		} catch (Exception e) {
			System.out.println("failed payback ... , rollback for this transaction;");
			ConnectionUtils.rollback(connection);
			throw e;
		} finally {
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}

	@Test
	public void testPayback2() throws SQLException {
		Connection connection = Connections.instance.get();
		Statement statement = connection.createStatement();
		try {
			connection.setAutoCommit(false);
			AccountService.instance.payback(statement, "402880173c898ea3013c8a");
			connection.commit();
		} catch (Exception e) {
			System.out.println("failed payback ... , rollback for this transaction;");
			ConnectionUtils.rollback(connection);
		} finally {
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}

	@Test
	public void testUpdateAccountStatus() throws AccountException {
		AccountService.instance.updateAccountStatus(1, 0);
	}

	@Test
	public void testUpdateAccountStatus2() throws AccountException {
		AccountService.instance.updateAccountStatus(68, 0);
	}

	@Test
	public void testExportWithdrawRecords() {
		fail("Not yet implemented");
	}

	@Test
	public void testLong() {
		System.out.println((Integer.MAX_VALUE + "").length());
	}
	@Test
	public void testQuickPay() throws Exception{
		Connection connection = Connections.instance.get();
		Statement statement = connection.createStatement();
		try {
			connection.setAutoCommit(false);
			AccountService.instance.quickPay(statement, "402880173d334d2c013d334d2c7f0000", "123456");
			connection.commit();
		} catch (Exception e) {
			System.out.println("failed quickpay ... , rollback for this transaction;");
			ConnectionUtils.rollback(connection);
		} finally {
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}

}
