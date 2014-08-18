package cn.zthz.actor.daemon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class DaemonEntry {
	private static final Log log = LogFactory.getLog(DaemonEntry.class);
	
	public static void startDaemon(){
		log.info("start requirementExpirer daemon");
		RequirementExpirer requirementExpirer = new RequirementExpirer();
		requirementExpirer.start();
	}

}
