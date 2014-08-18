package cn.zthz.common;

import java.util.ArrayList;
import java.util.List;

import qileke.jer.server.JServer;
import cn.zthz.actor.assemble.GlobalConfig;

public class StartServer {
	public static void main(String[] args) {
		List<String> handlerFiles = new ArrayList<>(1);
		List<String> filterFiles = new ArrayList<>(1);
		handlerFiles.add("mas-map.properties");
		filterFiles.add("mas-filters.item");
		SyncHandlerTest hander = new SyncHandlerTest("application/json" , ".json" , handlerFiles);
		JServer jServer = new JServer(GlobalConfig.getInt("mas.port") , hander );
		try {
			jServer.start(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
