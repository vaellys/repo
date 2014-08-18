package com.alipay.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.alipay.dao.base.BaseDao;
import com.alipay.model.AlipayTrade;
import com.alipay.model.PaymentRecorder;

public class AlipayTradeDao extends BaseDao {
	
	private Connection conn;
	private PreparedStatement ps;
	
	public AlipayTradeDao() throws Exception {
		super();
	}

	public void saveAlipayTrade(AlipayTrade bean){
		try {
			conn = getConnection();
			Timestamp now = new Timestamp(System.currentTimeMillis());
			String sql = "insert into alipay_trade(id,trade_status,create_time) values(?,?,?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, bean.getId());
			ps.setString(2, bean.getTrade_status());
			ps.setTimestamp(3, now);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				ps.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
}
