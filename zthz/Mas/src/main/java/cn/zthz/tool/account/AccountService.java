package cn.zthz.tool.account;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.zthz.actor.assemble.Global;
import cn.zthz.actor.message.RequirementPushor;
import cn.zthz.actor.queue.QueueSubjects;
import cn.zthz.actor.rest.ErrorCodes;
import cn.zthz.tool.common.Formats;
import cn.zthz.tool.common.HashUtils;
import cn.zthz.tool.common.HzException;
import cn.zthz.tool.common.LogUtils;
import cn.zthz.tool.common.StringUtils;
import cn.zthz.tool.common.XmlUtils;
import cn.zthz.tool.db.Connections;
import cn.zthz.tool.db.QuickDB;
import cn.zthz.tool.db.ResultSetMap;
import cn.zthz.tool.requirement.AbstractService;
import cn.zthz.tool.requirement.ConnectionUtils;

public final class AccountService extends AbstractService {
	private static final Log log = LogFactory.getLog(AccountService.class);
	public static final AccountService instance = new AccountService();
	public static final int HZ_ACCOUNT_ID = 0;

	private void modifyAccountBalance(Statement statement, int accountId, BigDecimal money) throws SQLException, AccountException {
		String sql = "update Account set balance=" + money + " where id=" + accountId;
		if (log.isDebugEnabled()) {
			log.debug(sql);
		}
		statement.executeUpdate(sql);
	}

	/**
	 * id,userId,balance,status,createTime , ts , cardNumber,bankType,password
	 * 
	 * @param statement
	 * @param userId
	 * @return
	 * @throws SQLException
	 */
	private Map<String, Object> getUserAccount(Statement statement, String userId) throws SQLException {
		String sql = "select id,userId,balance,status,createTime, cardNumber,bankType,password from Account where userId='" + userId
				+ "' limit 1";
		return ResultSetMap.map(statement.executeQuery(sql));
	}

	private Map<String, Object> getAccount(Statement statement, int accountId) throws SQLException {
		String sql = "select id,userId,balance,status,createTime, cardNumber,bankType,password from Account where id=" + accountId
				+ " limit 1";
		return ResultSetMap.map(statement.executeQuery(sql));
	}

	private Map<String, Object> getSourceAccount(Statement statement, String requirementId) throws SQLException {
		String sql = "select status , balance , a.id from Account a where exists (select id from Prepay p where requirementId='"
				+ requirementId + "' and a.id=p.sourceAccountId limit 1)";
		return ResultSetMap.map(statement.executeQuery(sql));
	}

	private Map<String, Object> getPrepay(Statement statement, String requirementId) throws SQLException {
		String sql = "select money, sourceAccountId, targetAccountId, status from Prepay where requirementId='" + requirementId + "'";
		if (log.isDebugEnabled()) {
			log.debug(sql);
		}
		return ResultSetMap.map(statement.executeQuery(sql));
	}

	public static final String ALIPAY_STATUS_TRADE_INIT = "TRADE_INIT";
	public static final String ALIPAY_STATUS_TRADE_FINISHED = "TRADE_FINISHED";
	public static final String ALIPAY_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";
	public static final String ALIPAY_STATUS_TRADE_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
	public static final Map<String, Integer> ALIPAY_TRADE_STATUSES = new HashMap<>();
	static {
		ALIPAY_TRADE_STATUSES.put(ALIPAY_STATUS_TRADE_INIT, 0);
		ALIPAY_TRADE_STATUSES.put(ALIPAY_STATUS_TRADE_WAIT_BUYER_PAY, 1);
		ALIPAY_TRADE_STATUSES.put(ALIPAY_STATUS_TRADE_FINISHED, 2);
		ALIPAY_TRADE_STATUSES.put(ALIPAY_STATUS_TRADE_SUCCESS, 3);
	}
	
	public static boolean tradeSuccess(String tradeStatus){
		return tradeSuccess(ALIPAY_TRADE_STATUSES.get(tradeStatus));
	}
	public static boolean tradeSuccess(int tradeStatus){
		if(tradeStatus == 2 || tradeStatus == 3){
			return true;
		}
		return false;
	}

	/**
	 * 
	 notify_data= <notify> <trade_status>TRADE_FINISHED</trade_status>
	 * <total_fee>0.90</total_fee> <subject>123456</subject> <out_trade_no>
	 * 1118060201-7555 </out_trade_no>
	 * <notify_reg_time>2010-11-1814:02:43.000</notify_reg_time>
	 * <trade_no>2010111800209965</trade_no> </notify>
	 * 
	 * &sign=ZPZULntRpJwFmGNI
	 * VKwjLEF2Tze7bqs60rxQ22CqT5J1UlvGo575QK9z/+p+7E9cOoRoWzqR6xHZ6WVv3dloyGK
	 * DR0btvrdqPgUAoeaX/YOWzTh00vwcQ+HBtXE+vPTfAqjCTxiiSJEOY7ATCF1q7iP3sfQxhS
	 * 0nDUug1LP3OLk=
	 * 
	 * <notify><partner>2088901028202974</partner><discount>0.00</discount><
	 * payment_type>1</payment_type>
	 * <subject>????</subject><trade_no>2013030745821735
	 * </trade_no><buyer_email>18210165894</buyer_email> <gmt_create>2013-03-07
	 * 17:24:10</gmt_create><quantity>1</quantity>
	 * <out_trade_no>eb8878fc178a4a1b926dc289f4db48e6
	 * </out_trade_no><seller_id>2088901028202974</seller_id>
	 * <trade_status>TRADE_FINISHED
	 * </trade_status><is_total_fee_adjust>N</is_total_fee_adjust>
	 * <total_fee>0.01</total_fee><gmt_payment>2013-03-07 17:24:11</gmt_payment>
	 * <seller_email>bjzthzkjyxgs@163.com</seller_email><gmt_close>2013-03-07
	 * 17:24:11</gmt_close>
	 * <price>0.01</price><buyer_id>2088702430259358</buyer_id
	 * ><use_coupon>N</use_coupon></notify>
	 * 
	 * // String verifyData =
	 * "<notify><seller_email>bjzthzkjyxgs@163.com</seller_email>
	 * <partner>2088901028202974</partner><payment_type>1</payment_type>
	 * <buyer_email>18210165894</buyer_email>
	 * <trade_no>2013030847678235</trade_no
	 * ><buyer_id>2088702430259358</buyer_id><quantity>1</quantity>
	 * <total_fee>0.01
	 * </total_fee><use_coupon>N</use_coupon><is_total_fee_adjust>
	 * Y</is_total_fee_adjust
	 * ><price>0.01</price><out_trade_no>ad1358557aa348deaeed1bb7b060a31d
	 * </out_trade_no><gmt_create>2013-03-08
	 * 15:18:32</gmt_create><seller_id>2088901028202974
	 * </seller_id><subject>账户充值<
	 * /subject><trade_status>WAIT_BUYER_PAY</trade_status
	 * ><discount>0.00</discount></notify>";
	 * 
	 * @param rawData
	 * @param signType
	 * @param sign
	 * @throws AccountException
	 */
	public static void main(String[] args) throws AccountException {
		String rawData = "<notify><partner>2088901028202974</partner><discount>0.00</discount><payment_type>1</payment_type><subject>账户充值</subject><trade_no>2013041826059467</trade_no><buyer_email>owen.zhang@live.com</buyer_email><gmt_create>2013-04-18 19:22:37</gmt_create><quantity>1</quantity><out_trade_no>92ece5bb2338475e8334e01e64728f28</out_trade_no><seller_id>2088901028202974</seller_id><trade_status>TRADE_FINISHED</trade_status><is_total_fee_adjust>N</is_total_fee_adjust><total_fee>1.00</total_fee><gmt_payment>2013-04-18 19:22:38</gmt_payment><seller_email>bjzthzkjyxgs@163.com</seller_email><gmt_close>2013-04-18 19:22:38</gmt_close><price>1.00</price><buyer_id>2088002431052670</buyer_id><use_coupon>N</use_coupon></notify>";
		String sign="PqYvWgqogmue57fEKV41kWF/1omMMPTZ0dnwMAp9PMw9sfUKZljrhcLbsGer6vLJLJTn6MBBHofwxoBRYSgDVIn1t5B6SjzTSbQhbIna/gKEWJ6InAYJWWMdLhpyhoXhuuwmjPo/S9YNSGrwbGiV3/aw1Z//fUN3Vr0M+GZRQ8M=";
		String signType="RSA";
		instance.alipayChargeCallback(rawData, sign, signType );
	}
	public void alipayChargeCallback(String rawData, String sign, String signType) throws AccountException {
		if (log.isInfoEnabled()) {
			log.info("alipayChargeCallback rawData:" + rawData + " sign:" + sign + " signType:" + signType);
		}
		Connection connection = null;
		Statement statement = null;
		try {
			connection = Connections.instance.get();
			connection.setAutoCommit(false);
			// connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			statement = connection.createStatement();
			Map<String, List<String>> tradeData = XmlUtils.parseXml(rawData);
			String tradeStatus = tradeData.get("notify.trade_status").get(0).trim();
			String totalFee = tradeData.get("notify.total_fee").get(0).trim();
			String outTradeNo = tradeData.get("notify.out_trade_no").get(0).trim();
			String tradeNo = tradeData.get("notify.trade_no").get(0).trim();
			String subject = tradeData.get("notify.subject").get(0).trim();
			if(checkAndUpdateAlipayTrade(statement, outTradeNo, tradeNo, ALIPAY_TRADE_STATUSES.get(tradeStatus), totalFee, subject)){
				connection.commit();
				return;
			}
			if (ALIPAY_STATUS_TRADE_WAIT_BUYER_PAY.equals(tradeStatus)) {
				connection.commit();
				return;
			}
			String userId = (String) ResultSetMap.mapObject(statement.executeQuery("select userId from AlipayTrade where id='" + outTradeNo
					+ "'"));
			if (ALIPAY_STATUS_TRADE_FINISHED.equals(tradeStatus) || ALIPAY_STATUS_TRADE_SUCCESS.equals(tradeStatus)) {
				// validataAlipaySign(sign , signType,outTradeNo , userId);
				BigDecimal money = Formats.formatMoney(new BigDecimal(totalFee));
				charge(statement, userId, money);
			} else {
				throw new AccountException("trade status is:" + tradeStatus);
			}
			connection.commit();
		} catch (HzException e) {
			log.error("rawData:" + rawData + " sign:" + sign, e);
			ConnectionUtils.rollback(connection);
			throw new AccountException(e.getMessage(), e);
			// log.error("failed to alipayCharge ,userId:" + userId + " money:"
			// + formattedMoney, e);
			// throw new AccountException("failed to withdraw ,userId:" + userId
			// + " money:" + formattedMoney, e);
		} catch (Exception e) {
			log.error("rawData:" + rawData, e);
			ConnectionUtils.rollback(connection);
			throw new AccountException("data format error", e);
		} finally {
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}

	/**
	 * true has been finished trade
	 * @param statement
	 * @param outTradeNo
	 * @param tradeNo
	 * @param tradeStatus
	 * @param totalFee
	 * @param subject
	 * @return
	 * @throws SQLException
	 */
	public boolean checkAndUpdateAlipayTrade(Statement statement, String outTradeNo, String tradeNo, int tradeStatus, String totalFee, String subject)
			throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement= null;
		try{
			connection = Connections.instance.get();
			String selectSql = "select tradeStatus ,ts from AlipayTrade where id='" + outTradeNo + "'";
			Map<String, Object> rs= (Map<String, Object>) ResultSetMap.map(statement.executeQuery(selectSql));
			Integer oldStatus = (Integer) rs.get("tradeStatus");
			Timestamp ts = (Timestamp) rs.get("ts");
			if(oldStatus!=null && tradeSuccess(oldStatus)){
				return true;
			}
			String sql = "update AlipayTrade set tradeNo='" + tradeNo + "',tradeStatus=" + tradeStatus + ",totalFee=" + totalFee
					+ ",`subject`='" + subject + "' where id='" + outTradeNo + "' and ts=?";
			preparedStatement = connection.prepareStatement(sql );
			preparedStatement.setTimestamp(1, ts);
			//		statement.executeUpdate(sql);
			if(0==preparedStatement.executeUpdate()){
				return true;
			}
			return false;
		}finally{
			if(preparedStatement!=null)
				preparedStatement.close();
			if(null!=connection)
				connection.close();

		}
	}

	/**
	 * sign rsa outTradeNum--hz--userId eg. 131231af21312--hz--35aa312dd3344324
	 * 
	 * @param sign
	 * @param signType
	 * @param tradeNum
	 * @param userId
	 * @throws AccountException
	 */
	private void validataAlipaySign(String sign, String signType, String tradeNum, String userId) throws AccountException {
		if (!"RSA".equalsIgnoreCase(signType)) {
			throw new AccountException(ErrorCodes.ACCOUNT_SIGN_TYPE_ERROR, "signType error");
		}

		// String data = RsaUtils.decrypt(sign, PRIVATE_KEY_PKCS8);
		// log.info("decrypt data:"+data+" sign:"+sign+" signType:"+signType);
		// if(!(tradeNum+"--hz--"+userId).equals(data)){
		// throw new AccountException(ErrorCodes.ACCOUNT_SIGN_ERROR,
		// "sign error");
		// }

	}

	public String getAlipayTradeNumber(String userId) throws AccountException {
		if (log.isInfoEnabled()) {
			log.info("getAlipayTradeNumber userId:" + userId);
		}
		String id = HashUtils.systemUuid();
		String sql = "insert into AlipayTrade (id,userId,createTime,tradeStatus,ts) values(?,?,?,?,?)";
		try {
			Timestamp now = new Timestamp(System.currentTimeMillis());
			QuickDB.insert(sql, id, userId, now, ALIPAY_TRADE_STATUSES.get(ALIPAY_STATUS_TRADE_INIT),now);
			return id;
		} catch (Exception e) {
			throw new AccountException("failed to save alipay trade", e);
		}
	}

	public void unionPayChargeCallback(Map<String, String> rawData) throws AccountException {

	}

	/**
	 * update for password , bankType , cardNumber
	 * 
	 * @param userId
	 * @param args
	 * @throws AccountException
	 */
	public void updateAccount(String userId, Map<String, Object> args) throws AccountException {
		if (log.isInfoEnabled()) {
			log.info("updateAccountStatus userId:" + userId);
		}
		if (null == userId || null == args || args.isEmpty()) {
			return;
		}
		StringBuilder sql = new StringBuilder();
		sql.append("update Account set ");
		if (args.containsKey("password")) {
			sql.append("password=:password,");
		}
		if (args.containsKey("cardNumber")) {
			sql.append("cardNumber=:cardNumber,");
		}
		if (args.containsKey("bankType")) {
			sql.append("bankType=:bankType,");
		}
		if (args.containsKey("accountName")) {
			sql.append("accountName=:accountName,");
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(" where userId='");
		sql.append(userId);
		sql.append('\'');
		try {
			System.out.println(sql);
			QuickDB.update(sql.toString(), args);
		} catch (SQLException e) {
			log.error("failed to update user Account,userId:" + userId + LogUtils.format("args", args), e);
			throw new AccountException("failed to update user Account,userId:" + userId + LogUtils.format("args", args), e);
		}
	}

	/**
	 * zt manager
	 * 
	 * @param accountId
	 * @param status
	 * @throws AccountException
	 */
	public void updateAccountStatus(int accountId, int status) throws AccountException {
		if (log.isInfoEnabled()) {
			log.info("updateAccountStatus accountId:" + accountId + " status:" + status);
		}
		Connection connection = null;
		Statement statement = null;
		if (AccountStatus.ACCOUNT_FROZEN != status || AccountStatus.ACCOUNT_NORMAL != status) {
			throw new AccountException("The account status was not exist!");
		}
		try {
			connection = Connections.instance.get();
			statement = connection.createStatement();
			String modifyAccountSql = "update Account set status=" + status + " where id=" + accountId;
			if (log.isDebugEnabled()) {
				log.debug(modifyAccountSql);
			}
			statement.executeUpdate(modifyAccountSql);
		} catch (Exception e) {
			log.error("The account:" + accountId + " status:" + status + " of user modify failed!");
			throw new AccountException("The account:" + accountId + " status:" + status + " of user modify failed!", e);
		} finally {
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}

	public void createAccount(Statement statement, String userId) throws AccountException {
		if (log.isInfoEnabled()) {
			log.info("create account userId:" + userId);
		}
		if (StringUtils.isEmpty(userId)) {
			throw new AccountException("userId is empty!");
		}
		try {
			if (statement.getConnection().getAutoCommit()) {
				log.error("need transaction with no auto commit");
				throw new AccountException("need transaction with no auto commit");
			}
			String countSql = "select count(*) from Account where userId='" + userId + "'";
			int count = ResultSetMap.mapInt(statement.executeQuery(countSql));
			if (count > 0) {
				throw new AccountException("user:" + userId + " has already an account!");
			}
			StringBuilder sql = new StringBuilder();
			sql.append("insert into Account (userId, balance ,status, createTime) values('");
			sql.append(userId);
			sql.append("',");
			sql.append(0);
			sql.append(",");
			sql.append(AccountStatus.ACCOUNT_NORMAL);
			sql.append(",FROM_UNIXTIME(");
			sql.append(System.currentTimeMillis() / 1000);
			sql.append(") )");
			if (log.isDebugEnabled()) {
				log.debug(sql.toString());
			}
			statement.executeUpdate(sql.toString());
		} catch (Exception e) {
			log.error("failed to createAccount ,userId:" + userId, e);
			throw new AccountException("failed to ", e);
		}
	}

	public void createAccount(String userId, String password, int bankType, String cardNumber) throws AccountException {
		if (log.isInfoEnabled()) {
			log.info("create account userId:" + userId + " bankType:" + bankType + " cardNum:" + cardNumber);
		}
		Connection connection = null;
		Statement statement = null;
		if (StringUtils.isEmpty(userId)) {
			throw new AccountException("userId is empty!");
		}
		if (StringUtils.isEmpty(password)) {
			throw new AccountException("password is empty!");
		}
		if (StringUtils.isEmpty(cardNumber)) {
			throw new AccountException("cardNumber is empty!");
		}
		try {
			connection = Connections.instance.get();
			statement = connection.createStatement();
			String countSql = "select count(*) from Account where userId='" + userId + "'";
			int count = ResultSetMap.mapInt(statement.executeQuery(countSql));
			if (count > 0) {
				throw new AccountException("user:" + userId + " has already an account!");
			}
			StringBuilder sql = new StringBuilder();
			sql.append("insert into Account (userId, balance, password,bankType, cardNumber ,status, createTime) values('");
			sql.append(userId);
			sql.append("',");
			sql.append(0);
			sql.append(",'");
			sql.append(password);
			sql.append("',");
			sql.append(bankType);
			sql.append(",'");
			sql.append(cardNumber);
			sql.append("',");
			sql.append(AccountStatus.ACCOUNT_NORMAL);
			sql.append(",FROM_UNIXTIME(");
			sql.append(System.currentTimeMillis() / 1000);
			sql.append(") )");
			if (log.isDebugEnabled()) {
				log.debug(sql.toString());
			}
			statement.executeUpdate(sql.toString());
		} catch (SQLException e) {
			log.error("failed to createAccount ,userId:" + userId, e);
			throw new AccountException("failed to ", e);
		} finally {
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}
	

	private void charge(Statement statement, String userId, BigDecimal money) throws AccountException {
		if (log.isInfoEnabled()) {
			log.info("charge userId:" + userId + " money:" + money);
		}
		BigDecimal formattedMoney = Formats.formatMoney(money);
		try {
			if (statement.getConnection().getAutoCommit()) {
				log.error("need transaction with no auto commit");
				throw new AccountException("need transaction with no auto commit");
			}
			Map<String, Object> account = getUserAccount(statement, userId);
			if (AccountStatus.ACCOUNT_NORMAL != (Integer) account.get("status")) {
				log.error("the account of user:" + userId + " was abnormal");
				throw new AccountException("the account of user:" + userId + " was abnormal");
			}
			BigDecimal balance = Formats.formatMoney((BigDecimal) account.get("balance"));
			BigDecimal newBalance = Formats.formatMoney(balance.add(formattedMoney));
			int accountId = (int) account.get("id");
			String updateSql = "update Account set balance=" + newBalance + " where id=" + accountId;
			if (log.isDebugEnabled()) {
				log.debug(updateSql);
			}
			statement.executeUpdate(updateSql);
			newTransactionHistory(statement, accountId, accountId, formattedMoney, AccountAction.USER_ACTION_TYPE_ENTER_ACCOUNT,
					AccountAction.CHARGE, "用户充值");
			
			Map<String, Object> message = new HashMap<>();
			message.put("money", formattedMoney);
			message.put("userId", userId);
			log.info("publish charged message");
			Global.queue.publish(QueueSubjects.ACCOUNT_CHARGED_SUCCESS, message );
		} catch (SQLException e) {
			log.error("failed to withdraw ,userId:" + userId + " money:" + formattedMoney, e);
			throw new AccountException("failed to ", e);
		}
	}

	public BigDecimal getBalance(String userId) throws AccountException {
		String sql = "select balance from Account where userId='" + userId + "'";
		try {
			return (BigDecimal) QuickDB.getSingle(sql);
		} catch (SQLException e) {
			log.error("failed to query user:" + userId + " balance.", e);
			throw new AccountException("failed to query user:" + userId + " balance.", e);
		}
	}

	/**
	 * 
	 * @param userId
	 * @return balance , cardNumber ,bankType,accountName
	 * @throws AccountException
	 */
	public Map<String, Object> getBasicInfo(String userId) throws AccountException {
		String sql = "select balance , cardNumber , bankType , accountName from Account where userId='" + userId + "'";
		try {
			return QuickDB.get(sql);
		} catch (SQLException e) {
			log.error("failed to query user:" + userId + " basic info.", e);
			throw new AccountException("failed to query user:" + userId + " basic info.", e);
		}
	}

	public void withdraw(String userId, BigDecimal money, String password) throws AccountException {
		if (log.isInfoEnabled()) {
			log.info("withdraw userId:" + userId + " money:" + money);
		}
		BigDecimal formattedMoney = Formats.formatMoney(money);
		Connection connection = null;
		Statement statement = null;
		try {
			connection = Connections.instance.get();
			connection.setAutoCommit(false);
			statement = connection.createStatement();
			Map<String, Object> account = getUserAccount(statement, userId);
			if (StringUtils.isEmpty((String) account.get("cardNumber"))) {
				throw new AccountException("can't withdraw ,cardNumber is empty. user:" + userId + ",money:" + formattedMoney);
			}
			if (formattedMoney.compareTo(new BigDecimal("0")) < 0) {
				throw new BalanceNotEnoughException("can't with minus money from account user:" + userId + ",money:" + formattedMoney);
			}
			// check status of user's account
			if (AccountStatus.ACCOUNT_NORMAL != (Integer) account.get("status")) {
				log.error("the account of user:" + userId + " was abnormal");
				throw new AccountException(ErrorCodes.ACCOUNT_FROZEN, "the account of user:" + userId + " was abnormal");
			}

			// check balance of user's account
			BigDecimal balance = Formats.formatMoney((BigDecimal) account.get("balance"));
			BigDecimal newBalance = Formats.formatMoney(balance.subtract(formattedMoney));
			if (newBalance.compareTo(new BigDecimal("0")) < 0) {
				log.error("the balance of user:" + userId + " not more than " + formattedMoney);
				throw new BalanceNotEnoughException("the balance of user:" + userId + " not more than " + formattedMoney);
			}
			int accountId = (int) account.get("id");
			checkUserPassword(statement, accountId, password);
			modifyAccountBalance(statement, accountId, newBalance);
			StringBuilder newWithSql = new StringBuilder(100);
			newWithSql.append("insert into Withdraw (accountId,money,createTime,status) values(");
			newWithSql.append(accountId);
			newWithSql.append(",");
			newWithSql.append(formattedMoney);
			newWithSql.append(",FROM_UNIXTIME(");
			newWithSql.append(System.currentTimeMillis() / 1000);
			newWithSql.append("),");
			newWithSql.append(AccountStatus.WITHDRAW_UNHANDLED);
			newWithSql.append(")");
			if (log.isDebugEnabled()) {
				log.debug(newWithSql);
			}
			statement.executeUpdate(newWithSql.toString());
			newTransactionHistory(statement, accountId, accountId, formattedMoney, AccountAction.USER_ACTION_TYPE_OUT_ACCOUNT,
					AccountAction.WITHDRAW, "用户提现");
			connection.commit();
		} catch (AccountException e) {
			ConnectionUtils.rollback(connection);
			throw e;
		} catch (Exception e) {
			ConnectionUtils.rollback(connection);
			log.error("failed to withdraw ,userId:" + userId + " money:" + formattedMoney, e);
			throw new AccountException("failed to withdraw ,userId:" + userId + " money:" + formattedMoney, e);
		} finally {
			ConnectionUtils.closeStatement(statement);
			ConnectionUtils.closeConnection(connection);
		}
	}

	private void newTransactionHistory(Statement statement, int sourceAccountId, int targetAccountId, BigDecimal money, int type,
			int action, String description) throws SQLException {
		StringBuilder sql = new StringBuilder(100);
		sql.append("insert into TransactionHistory (sourceAccountId,targetAccountId,money,type,action,description,createTime) values(");
		sql.append(sourceAccountId);
		sql.append(',');
		sql.append(targetAccountId);
		sql.append(',');
		sql.append(money);
		sql.append(',');
		sql.append(type);
		sql.append(',');
		sql.append(action);
		sql.append(",'");
		sql.append(description);
		sql.append("',FROM_UNIXTIME(");
		sql.append(System.currentTimeMillis() / 1000);
		sql.append(")");
		sql.append(')');
		if (log.isDebugEnabled()) {
			log.debug(sql);
		}
		statement.executeUpdate(sql.toString());
	}

	public List<Map<String, Object>> viewTranactionHistory(String userId, int startIndex, int pageSize) throws AccountException {
		if (log.isInfoEnabled()) {
			log.info("view tranaction history userId:" + userId);
		}
		try {
			String sql = "select sourceAccountId,targetAccountId,money,type,action,description,createTime from TransactionHistory h where exists (select id from Account a where userId='"
					+ userId + "' and h.sourceAccountId=a.id limit 1) limit " + startIndex + "," + pageSize;
			log.debug(sql);
			return QuickDB.gets(sql);
		} catch (SQLException e) {
			log.error("", e);
			throw new AccountException("", e);
		}
	}

	public Map<String, Object> viewAccount(String userId) throws AccountException {
		if (log.isInfoEnabled()) {
			log.info("view account userId:" + userId);
		}
		String sql = "select * from Account where userId='" + userId + "' limit 1";
		try {
			return QuickDB.get(sql);
		} catch (SQLException e) {
			log.error("userId:" + userId, e);
			throw new AccountException("userId:" + userId, e);
		}
	}

	public void setPrepayTargetAccount(Statement statement, String targetUserId, String requirementId) throws AccountException {
		try {
			if (statement.getConnection().getAutoCommit()) {
				log.error("need transaction with no auto commit");
				throw new AccountException("need transaction with no auto commit");
			}
			String sql = "update Prepay set targetAccountId=(select a.id from Account a where a.userId='" + targetUserId
					+ "' limit 1) where requirementId='" + requirementId + "'";
			System.out.println(sql);
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			throw new AccountException("set prepay target Account error", e);
		}
	}

	/**
	 * put money into prePay
	 * 
	 * @param statement
	 * @param sourceUserId
	 * @param targetUserId
	 * @param money
	 * @param requirementId
	 * @throws AccountException
	 */
	public void prePay(Statement statement, String sourceUserId, BigDecimal money, String requirementId) throws AccountException {
		if (log.isInfoEnabled()) {
			log.info("prepay for requirement:" + requirementId + " money:" + money + " userId:" + sourceUserId);
		}
		try {
			if (statement.getConnection().getAutoCommit()) {
				log.error("need transaction with no auto commit");
				throw new AccountException("need transaction with no auto commit");
			}
			BigDecimal newBalance = null;
			String accountsSql = "select id,userId,balance,status,createTime,cardNumber,bankType,password from Account where userId='"
					+ sourceUserId + "' limit 1";
			if (log.isDebugEnabled()) {
				log.debug(accountsSql);
			}
			Map<Object, Map<String, Object>> accounts = ResultSetMap.map(statement.executeQuery(accountsSql), "userId");
			Map<String, Object> sourceAccount = accounts.get(sourceUserId);
			// Map<String, Object> targetAccount = accounts.get(targetUserId);
			int sourceAccountStatus = (int) sourceAccount.get("status");
			// int targetAccountStatus = (int) targetAccount.get("status");
			if (AccountStatus.ACCOUNT_NORMAL != sourceAccountStatus) {
				log.error("sourceUserId:" + sourceUserId + "  account was abnormal!");
				throw new AccountException("sourceUserId:" + sourceUserId + "  account was abnormal!");
			}
			// if (AccountStatus.ACCOUNT_NORMAL != targetAccountStatus) {
			// log.error("targetUserId:" + targetUserId +
			// "  account was abnormal!");
			// throw new AccountException("targetUserId:" + targetUserId +
			// "  account was abnormal!");
			// }
			BigDecimal balance = (BigDecimal) sourceAccount.get("balance");
			BigDecimal formattedBalance = Formats.formatMoney(balance);
			BigDecimal formattedMoney = Formats.formatMoney(money);
			newBalance = formattedBalance.subtract(formattedMoney);
			if (formattedBalance.compareTo(formattedMoney) < 0) {
				log.error("the account of sourceUserId:" + sourceUserId + "was not enough money!");
				throw new AccountException(ErrorCodes.ACCOUNT_NOT_ENOUGH_BALANCE, "the account of sourceUserId:" + sourceUserId
						+ " was not enough money!");
			}
			int sourceAccountId = (int) sourceAccount.get("id");
			// int targetAccountId = (int) targetAccount.get("id");
			String modifyAccountSql = "update Account set balance=" + newBalance + " where userId ='" + sourceUserId + "'";
			if (log.isDebugEnabled()) {
				log.debug(modifyAccountSql);
			}
			statement.executeUpdate(modifyAccountSql);
			newTransactionHistory(statement, sourceAccountId, sourceAccountId, formattedMoney, AccountAction.USER_ACTION_TYPE_OUT_ACCOUNT,
					AccountAction.PREPAY, "用户预支付");
			StringBuilder newPrepaySql = new StringBuilder(100);
			newPrepaySql.append("insert into Prepay(money, requirementId, createTime, sourceAccountId , status) values(");
			newPrepaySql.append(money);
			newPrepaySql.append(",'");
			newPrepaySql.append(requirementId);
			newPrepaySql.append("',FROM_UNIXTIME(");
			newPrepaySql.append(System.currentTimeMillis() / 1000);
			newPrepaySql.append(")");
			newPrepaySql.append(",");
			newPrepaySql.append(sourceAccountId);
			newPrepaySql.append(",");
			newPrepaySql.append(AccountStatus.PREPAY_START);
			newPrepaySql.append(")");
			if (log.isDebugEnabled()) {
				log.debug(newPrepaySql);
			}
			statement.executeUpdate(newPrepaySql.toString());
		} catch (SQLException e) {
			log.error("prepay failed!", e);
			throw new AccountException("userId:" + sourceUserId);
		}
	}

	// private void checkAccountPassword(Statement statement, int accountId,
	// String password) throws AccountException, SQLException {
	// String sql = "select count(*) from Account where id=" + accountId +
	// " and password='" + password + "' limit 1";
	// if (1 != ResultSetMap.mapInt(statement.executeQuery(sql))) {
	// log.error("account password incorrect! accountId:" + accountId);
	// throw new AccountException(ErrorCodes.ACCOUNT_PASSWORD_ERROR,
	// "account password incorrect! accountId:" + accountId);
	// }
	// }
	private void checkUserPassword(Statement statement, int accountId, String password) throws AccountException, SQLException {
		String sql = "select count(*) from User u where password='" + password + "' and exists(select id from Account a where a.id="
				+ accountId + " and a.userId=u.id limit 1) limit 1";
		if (0 >= ResultSetMap.mapInt(statement.executeQuery(sql))) {
			log.error("account password incorrect! accountId:" + accountId);
			throw new AccountException(ErrorCodes.USER_PASSWORD_ERROR, "account password incorrect! accountId:" + accountId);
		}
	}

	/**
	 * @param statement
	 * @param requirementId
	 * @throws AccountException
	 */
	public void pay(Statement statement, String requirementId, String password) throws AccountException {
		if (log.isInfoEnabled()) {
			log.info("pay for requirement:" + requirementId);
		}
		try {
			if (statement.getConnection().getAutoCommit()) {
				log.error("need transaction with no auto commit");
				throw new AccountException("need transaction with no auto commit");
			}
			Map<String, Object> prepay = getPrepay(statement, requirementId);
			int payStatus = (int) prepay.get("status");
			if (AccountStatus.PREPAY_START != payStatus) {
				log.debug("The account has already been payed or paybacked!");
				throw new AccountException("The account has already been payed or paybacked!");
			}
			int targetAccountId = (int) prepay.get("targetAccountId");
			int sourceAccountId = (int) prepay.get("sourceAccountId");
			checkUserPassword(statement, sourceAccountId, password);
			BigDecimal money = (BigDecimal) prepay.get("money");
			BigDecimal payMoney = Formats.formatMoney(money);
			Map<String, Object> targetAccount = getAccount(statement, targetAccountId);
			int targetUserStatus = (int) targetAccount.get("status");
			BigDecimal balance = (BigDecimal) targetAccount.get("balance");
			BigDecimal targetAccountBalance = Formats.formatMoney(balance);
			BigDecimal newBalance = targetAccountBalance.add(payMoney);
			if (AccountStatus.ACCOUNT_NORMAL != targetUserStatus) {
				log.debug("targetUserAccount was abnormal!");
				throw new AccountException("targetUserAccount was abnormal!");
			}
			String modifyTargetUserAccountSql = "update Account set balance=" + newBalance + " where id=" + targetAccountId;
			if (log.isDebugEnabled()) {
				log.debug(modifyTargetUserAccountSql);
			}
			statement.executeUpdate(modifyTargetUserAccountSql);
			String modifyPrePayStatusSql = "update Prepay set status=" + AccountStatus.PREPAY_COMPLETE + " where requirementId='"
					+ requirementId + "'";
			if (log.isDebugEnabled()) {
				log.debug(modifyPrePayStatusSql);
			}
			statement.executeUpdate(modifyPrePayStatusSql);
			// String sourceAccountSql =
			// "select id as sourceAccountId from Account where userId='" +
			// sourceUserId + "'";
			// if (log.isDebugEnabled()) {
			// log.debug(sourceAccountSql);
			// }
			// int sourceAccountId =
			// ResultSetMap.mapInt(statement.executeQuery(sourceAccountSql));
			// String targetAccountSql =
			// "select id as targetAccountId from Account where userId='" +
			// targetUserId + "'";
			// if (log.isDebugEnabled()) {
			// log.debug(targetAccountSql);
			// }
			// int targetAccountId =
			// ResultSetMap.mapInt(statement.executeQuery(targetAccountSql));
			newTransactionHistory(statement, sourceAccountId, targetAccountId, payMoney, AccountAction.USER_ACTION_TYPE_ENTER_ACCOUNT,
					AccountAction.PAY, "用户支付");
			
		} catch (SQLException e) {
			log.error("pay fail!", e);
			throw new AccountException("requirementId:'" + requirementId);
		}
	}

	public void quickPay(Statement statement, String requirementId, String password) throws AccountException {
		if (log.isInfoEnabled()) {
			log.info("pay for requirement:" + requirementId);
		}
		try {
			if (statement.getConnection().getAutoCommit()) {
				log.error("need transaction with no auto commit");
				throw new AccountException("need transaction with no auto commit");
			}

			String payMoneySql = "select price, userId, selectedCandidate from Requirement where id='" + requirementId + "'";
			if (log.isDebugEnabled()) {
				log.debug(payMoneySql);
			}
			Map<String, Object> requirement = ResultSetMap.map(statement.executeQuery(payMoneySql));
			String userId = (String) requirement.get("userId");
			String targetAccountUserId = (String) requirement.get("selectedCandidate");
			String sourceAccoutSql = "select id, balance,status,createTime, cardNumber,bankType,password, userId from Account where userId in ('"
					+ userId + "','" + targetAccountUserId + "') limit 2";
			Map<Object, Map<String, Object>> accounts = ResultSetMap.map(statement.executeQuery(sourceAccoutSql), "userId");
			// 根据userId取不同的用户的账户
			Map<String, Object> sourceAccount = accounts.get(userId);
			Map<String, Object> targetAccount = accounts.get(targetAccountUserId);
			// Map<String, Object> sourceAndtargetAccounts =
			// ResultSetMap.map(statement.executeQuery(sourceAccoutSql));
			int sourceAccountStatus = (int) sourceAccount.get("status");
			BigDecimal sourceAccountBalance = (BigDecimal) sourceAccount.get("balance");
			BigDecimal targetAccountBalance = (BigDecimal) targetAccount.get("balance");
			BigDecimal formatSourceAccountBalance = Formats.formatMoney(sourceAccountBalance);
			if (AccountStatus.ACCOUNT_FROZEN == sourceAccountStatus) {
				log.debug("the account was abnormal! requirementId:'" + requirementId + "'");
				throw new AccountException(ErrorCodes.ACCOUNT_FROZEN, "the account was abnormal! requirementId:'" + requirementId + "'");
			}
			
			BigDecimal payMoney = (BigDecimal) requirement.get("price");
			BigDecimal formatPayMoney = Formats.formatMoney(payMoney);
			int sourceAccountId = (int) sourceAccount.get("id");
			int targetAccountId = (int) targetAccount.get("id");
			checkUserPassword(statement, sourceAccountId, password);
			if (formatSourceAccountBalance.compareTo(formatPayMoney) < 0) {
				log.debug("source account was not enough money! requirementId:'" + requirementId + "'");
				throw new AccountException(ErrorCodes.ACCOUNT_NOT_ENOUGH_BALANCE, "source account was not enough money! requirementId:'" + requirementId + "'");
			}
			int targetAccountStatus = (int) targetAccount.get("status");
			if (AccountStatus.ACCOUNT_FROZEN == targetAccountStatus) {
				log.debug("target account status was abnormal!");
				throw new AccountException(ErrorCodes.ACCOUNT_FROZEN, "target account status was abnormal!");
			}
			BigDecimal newSourceAccountBalance = sourceAccountBalance.subtract(formatPayMoney);
			String modifySourceAccountSql = "update Account set balance=" + newSourceAccountBalance + " where id="+sourceAccountId;
			statement.executeUpdate(modifySourceAccountSql);
			BigDecimal newTargetAccountBalance = targetAccountBalance.add(formatPayMoney);
			String modifyTargetAccountSql = "update Account set balance=" + newTargetAccountBalance+" where id="+targetAccountId;
			statement.executeUpdate(modifyTargetAccountSql);
			newTransactionHistory(statement, sourceAccountId, targetAccountId, formatPayMoney, AccountAction.USER_ACTION_TYPE_OUT_ACCOUNT,
					AccountAction.NO_MANDATE_PAY, "用户快捷支付");
			Map<String, Object> message = new HashMap<>();;
			message.put("money", formatPayMoney);
			message.put("balance", newSourceAccountBalance);
			message.put("requirementId", requirementId);
			message.put("userId", userId);
			Global.queue.publish(QueueSubjects.REQUIREMENT_QUICK_PAY, message );
		} catch (SQLException e) {
			log.error("pay fail!", e);
			throw new AccountException("requirementId:'" + requirementId);
		}
	}

	/**
	 * put money into sourceAccount
	 * 
	 * @param statement
	 * @param requirementId
	 * @throws AccountException
	 */
	public void payback(Statement statement, String requirementId) throws AccountException {
		if (log.isInfoEnabled()) {
			log.info("pay back for requirement:" + requirementId);
		}
		try {
			if (statement.getConnection().getAutoCommit() == true) {
				log.debug("need transaction with no auto commit");
				throw new AccountException("need transaction with no auto commit");
			}
			Map<String, Object> prepay = getPrepay(statement, requirementId);
			if (null == prepay || prepay.isEmpty()) {
				return;
			}
			int prepayStatus = (int) prepay.get("status");
			if (AccountStatus.PREPAY_START != prepayStatus) {
				log.debug("the account of the user status was not start status!");
				throw new AccountException("the account of the user status was not start status!");
			}
			Map<String, Object> payback = getSourceAccount(statement, requirementId);
			int sourceUserAccountStatus = (int) payback.get("status");
			if (AccountStatus.ACCOUNT_NORMAL != sourceUserAccountStatus) {
				log.debug("user's account was been abnormal!");
				throw new AccountException("user's account was been abnormal!");
			}
			BigDecimal money = (BigDecimal) prepay.get("money");
			BigDecimal prepayMoney = Formats.formatMoney(money);
			BigDecimal balance = (BigDecimal) payback.get("balance");
			BigDecimal sourceAccountBalance = Formats.formatMoney(balance);
			BigDecimal newSourceAccountBalance = prepayMoney.add(sourceAccountBalance);
			int sourceAccountId = (int) payback.get("id");
			// int targetAccountId = (int)prepay.get("targetAccountId");
			String modifyAccountSql = "update Account set balance=" + newSourceAccountBalance + " where id=" + sourceAccountId;
			if (log.isDebugEnabled()) {
				log.debug(modifyAccountSql);
			}
			statement.executeUpdate(modifyAccountSql);
			String modifyPrepayStatusSql = "update Prepay set status=" + AccountStatus.PREPAY_BACK + " where requirementId='"
					+ requirementId + "'";
			if (log.isDebugEnabled()) {
				log.debug(modifyPrepayStatusSql);
			}
			statement.executeUpdate(modifyPrepayStatusSql);
			newTransactionHistory(statement, sourceAccountId, sourceAccountId, prepayMoney, AccountAction.USER_ACTION_TYPE_ENTER_ACCOUNT,
					AccountAction.PAYBACK, "用户退款");
		} catch (SQLException e) {
			log.error("user payback failed!;requirementId:'" + requirementId + "'");
			throw new AccountException("user payback failed!;requirementId:'" + requirementId + "'");
		}
	}

	public void exportWithdrawRecords(String userId, BigDecimal money) throws AccountException {

	}

}
