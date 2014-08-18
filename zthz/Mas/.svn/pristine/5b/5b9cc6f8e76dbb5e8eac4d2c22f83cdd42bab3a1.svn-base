package cn.zthz.tool.push;

import java.util.List;

import cn.zthz.tool.common.LogUtils;
import javapns.Push;
import javapns.devices.Device;
import javapns.notification.Payload;
import javapns.notification.PushNotificationManager;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;
import javapns.notification.PushedNotifications;

public abstract class IosPush implements cn.zthz.tool.push.Push {

	public static void main(String[] args) throws Exception {
		String devices = "cf4c1e73137ca3b80180210aa82cf08822963d9effe336ba0d7482c2f8eaa185";
		boolean production = false;
		String password = "hello";
		Object keystore = "push_development.p12";
		for (int i = 0; i < 1000; i++) {
			// PushedNotifications response =
			// Push.combined("hello zhuangzhuang", i,
			// "default", "push_development.p12", "hello", false,
			// devices);
			PushNotificationPayload payload = PushNotificationPayload.complex();
			payload.addAlert("Hello World!");
			payload.addCustomDictionary("mykey1", "My Value 1");
			payload.addCustomDictionary("mykey2", 2);
			Push.payload(payload, keystore, password, production, devices);
			List<PushedNotification> notifications = Push.payload(payload, keystore, password, production, devices);
			System.out.println(LogUtils.format("notifications", notifications));
			// System.out.println(LogUtils.format("response", response));
		}
		// System.out.println(token.length());
	}

	public void rb() {
		/*
		 * try { // 从客户端获取的deviceToken String deviceToken =
		 * "3a20764942e9cb4c4f6249274f12891946bed26131b686b8aa95322faff0ad46";
		 * System.out.println("Push Start deviceToken:" + deviceToken); //
		 * 定义消息模式 PayLoad payLoad = new PayLoad(); payLoad.addAlert("消息推送测试！");
		 * payLoad.addBadge(4); payLoad.addSound("default"); // 注册deviceToken
		 * PushNotificationManager pushManager =
		 * PushNotificationManager.getInstance();
		 * pushManager.addDevice("iPhone", deviceToken); // 连接APNS String host =
		 * "gateway.sandbox.push.apple.com"; int port = 2195; String path =
		 * "/Users/iMilo/Work.localized/iShop/project/service/iPush/"; String
		 * certificatePath = (path + "src/ipush/iPush.p12"); // certificatePath
		 * 步骤一中生成的*.p12文件位置 String certificatePassword = "Love24mm";
		 * pushManager.initializeConnection(host, port, certificatePath,
		 * certificatePassword, SSLConnectionHelper.KEYSTORE_TYPE_PKCS12); //
		 * 发送推送 Device client = pushManager.getDevice("iPhone");
		 * pushManager.sendNotification(client, payLoad); // 停止连接APNS
		 * pushManager.stopConnection(); // 删除deviceToken
		 * pushManager.removeDevice("iPhone"); System.out.println("Push End"); }
		 * catch (Exception ex) { ex.printStackTrace(); }
		 */
	}
}
