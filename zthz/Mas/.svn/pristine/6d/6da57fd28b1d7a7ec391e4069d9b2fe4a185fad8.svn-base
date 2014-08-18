package cn.zthz.tool.push;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;

import cn.zthz.actor.assemble.GlobalConfig;
import cn.zthz.tool.common.LogUtils;

import javapns.communication.exceptions.KeystoreException;
import javapns.devices.exceptions.InvalidDeviceTokenFormatException;
import javapns.notification.PushNotificationPayload;
import javapns.notification.transmission.PushQueue;

public class IosJavaPnsPush extends AbstractPush {
	private static final Log log = LogFactory.getLog(IosJavaPnsPush.class);
	protected PushQueue queue = null;

	public IosJavaPnsPush() {
		try {
			queue = javapns.Push.queue(GlobalConfig.get("push.ios.keystore"), GlobalConfig.get("push.ios.password"),
					GlobalConfig.getBoolean("push.ios.production"), GlobalConfig.getInt("push.ios.threads"));
		} catch (KeystoreException e) {
			throw new RuntimeException(e);
		}
		queue.start();
	}

	@Override
	public void push(String alert, Map<String, Object> attach, String deviceToken) throws PushException {
		PushNotificationPayload payload = PushNotificationPayload.complex();
		try {
			payload.addAlert(alert);
			payload.addSound("default");
			if (null != attach && !alert.isEmpty()) {
				Object value =  null;
				for (Map.Entry<String, Object> entry : attach.entrySet()) {
					value = entry.getValue();
					if(value instanceof String){
						payload.addCustomDictionary(entry.getKey(), (String)entry.getValue());
					}
					if(value instanceof Integer){
						payload.addCustomDictionary(entry.getKey(), (Integer)entry.getValue());
					}
				}
			}
			queue.add(payload, deviceToken);
		} catch (InvalidDeviceTokenFormatException | JSONException e) {
			log.error(LogUtils.format("alert" , alert , "attach" , attach , "deviceToken" , deviceToken) , e);
			throw new PushException(e);
		}
	}

}
