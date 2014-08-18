package cn.zthz.actor.message;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.zthz.actor.assemble.Global;
import cn.zthz.tool.common.JsonUtils;
import cn.zthz.tool.queue.OnMessage;

public class AccountPushor {
	private static final Log log = LogFactory.getLog(AccountPushor.class);
	public static final AccountPushor instance = new AccountPushor();
	
	public void registerAll(){
	}
	public void onRequirementComplete1() {
		Global.queue.subscribe( new OnMessage() {
			@Override
			public void handle(String subject, Object message) {
				System.out.println(JsonUtils.toJsonString(message));
			}
		},"h");
	}
	

}
