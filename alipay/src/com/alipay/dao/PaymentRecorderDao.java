package com.alipay.dao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alipay.dao.base.BaseDao;
import com.alipay.model.PaymentRecorder;
import com.alipay.util.QuickDB;

public class PaymentRecorderDao extends BaseDao {
	private static final Log log = LogFactory.getLog(PaymentRecorderDao.class);
	
	private Connection conn;
	private PreparedStatement ps;
	
	public PaymentRecorderDao() throws Exception {
		super();
	}

	public void savePaymentRecorder(PaymentRecorder bean){
		try {
			conn = getConnection();
			Timestamp now = new Timestamp(System.currentTimeMillis());
			String sql = "insert into payment_recorder(invoice_title,first_name,last_name,confirmation_number,pay_money,create_time,alipay_number,trade_status) values(?,?,?,?,?,?,?,?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, bean.getInvoice_title());
			ps.setString(2, bean.getFirst_name());
			ps.setString(3, bean.getLast_name());
			ps.setString(4, bean.getConfirmation_number());
			ps.setBigDecimal(5, bean.getPay_money());
			ps.setTimestamp(6, now);
			ps.setString(7, bean.getAlipay_number());
			ps.setInt(8, bean.getTrade_status());
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
	
	public Map<String, Object> getCfNumberByOutTradeNu(String outTradeNu){
		Map<String, Object> map = null;
		try{
			String sql = "select confirmation_number from payment_recorder where alipay_number='"+outTradeNu+"'";
			map = QuickDB.get(sql);
		}catch(Exception e){
			e.printStackTrace();
		} 
		return map;
	}
	
	public boolean updateByOutTradeNu(String outTradeNu, Integer tradeStatus){
		String sql = "update payment_recorder set trade_status=" + tradeStatus + " where alipay_number='" + outTradeNu + "'";
		try {
			int count = QuickDB.update(sql);
			if(1 == count){
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean updateByTradeNo(String outTradeNu, String tradeNo){
		String sql = "update payment_recorder set trade_no='" + tradeNo + "' where alipay_number='" + outTradeNu + "'";
		try {
			int count = QuickDB.update(sql);
			if(1 == count){
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static void main(String[] args) throws Exception {
//		HttpClient client = new HttpClient();
//		HttpMethod method = new GetMethod(
//				"https://GetRegisteredNow.com/FTFChina/reports/setpayment.asp");
//		method.setQueryString("confirmationNumber=Z7V-Y2J-X4W");
//		method.setQueryString("paymentStatus=1");
//		try {
//			client.executeMethod(method);
//			String result = method.getResponseBodyAsString();
//			int s = result.lastIndexOf("<body>");
//			int e = result.indexOf("</body>");
//			String str = result.substring(s + 6, e).trim();
//			System.out.println(str);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		String url = "https://getregisterednow.com/FTFChina/reports/setpayment.asp?confirmationNumber=S7R-D8B-Q7R&paymentStatus=1";
		PaymentRecorderDao dao = new PaymentRecorderDao();
		try {
			String result = dao.doGet(url, "UTF-8");
			int s = result.lastIndexOf("<body>");
			int e = result.indexOf("</body>");
			String str = result.substring(s + 6, e).trim();
			System.out.println(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void update(String outTradeNu, Map<String, Object> args){
		if(outTradeNu == null){
			return;
		}
		StringBuilder sql = new StringBuilder();
		sql.append("update payment_recorder set");
		if (args.containsKey("status_no")) {
			sql.append(" status_no=:status_no,");
		}
		if (args.containsKey("first_name")){
			sql.append(" first_name=:first_name,");
		}
		if (args.containsKey("last_name")){
			sql.append(" last_name=:last_name,");
		}
		if (args.containsKey("create_time")){
			sql.append(" create_time=:create_time,");
		}
		if (args.containsKey("invoice_title")){
			sql.append(" invoice_title=:invoice_title,");
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(" where alipay_number='");
		sql.append(outTradeNu);
		sql.append('\'');
		try {
			QuickDB.update(sql.toString(), args);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	
//	public static void main(String[] args) {
//		PaymentRecorderDao dao;
//		try {
//			dao = new PaymentRecorderDao();
//			Map<String, Object> map = new HashMap<String, Object>();
//			map.put("status_no", 1);
//			dao.update("2a9f64c758a146478629462597c9a14f", map);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
	public Map<String, Object> getByInvoiceAndCfnum(String confirmationNumber){
		Map<String, Object> map = null;
		try{
			String sql = "select * from payment_recorder where confirmation_number='"+confirmationNumber+"'";
			map = QuickDB.get(sql);
			if(null != map){
				return map;
			}
		}catch(Exception e){
			e.printStackTrace();
		} 
		return map;
	}
	
	public  String doGet(String url, String charset) throws IOException {
		InputStream inputStream = null;
		// OutputStream outputStream = null;
		URL u = new URL(url);
		URLConnection connection = u.openConnection();
		inputStream = connection.getInputStream();
		// outputStream = connection.getOutputStream();
		int size = connection.getContentLength();
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream(
				size <= 0 ? 1024 : size);
		IOUtils.copy(inputStream, arrayOutputStream);
		// outputStream.close();
		inputStream.close();
		return new String(arrayOutputStream.toByteArray(), charset);
	}
	
}
