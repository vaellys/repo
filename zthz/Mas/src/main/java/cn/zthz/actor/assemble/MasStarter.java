package cn.zthz.actor.assemble;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.SchedulerException;

import qileke.jer.server.JServer;
import qileke.jer.server.SyncHandler;
import cn.zthz.actor.cache.user.UserCacheUpdater;
import cn.zthz.actor.daemon.DaemonEntry;
import cn.zthz.actor.message.Messagor;
import cn.zthz.actor.message.RequirementPushor;
import cn.zthz.actor.message.UserLoginCountor;
import cn.zthz.actor.rest.MasRestExceptionHandler;
import cn.zthz.actor.solr.ReimportScheduler;
import cn.zthz.tool.cache.RedisCache;

public class MasStarter {
	private static final Log log = LogFactory.getLog(MasStarter.class);
	public static void main(String[] args) throws Exception {
		checkRedisServer();
		startDQueue();
		startDaemon();
		startScheduler();
		initImageServer();
		startJServer();
	}
	
	private static void startScheduler() throws SchedulerException {
		ReimportScheduler.start();
	}

	private static void startJServer() throws Exception {
		List<String> handlerFiles = new ArrayList<>(1);
		List<String> filterFiles = new ArrayList<>(1);
		handlerFiles.add("mas-map.properties");
		filterFiles.add("mas-filters.item");
		SyncHandler hander = new SyncHandler("application/json" , ".json" , handlerFiles ,filterFiles);
		hander.exceptionHandler = new MasRestExceptionHandler();
		JServer jServer = new JServer(GlobalConfig.getInt("mas.port") , hander );
		jServer.start(true);
		
	}
	
	private static void initImageServer(){
		log.info("init image server");
		String locagImageRootDir = GlobalConfig.get("image.localRootDir");
		File file = new File(locagImageRootDir);
		if(!file.exists()){
			log.info("imageLocalRootDir not exists, create it:"+locagImageRootDir);
			file.mkdirs();
		}
	}
	
	private static void checkRedisServer(){
		log.info("check redis server cache!");
		RedisCache.instance.exists("test");
		log.info("check redis server sucess!");
	}
	
	private static void startDQueue(){
		log.info("start DQueue");
		Global.queue.start();
		registerQueueSubscriber();
	}
	
	private static void registerQueueSubscriber(){
		log.info("register messagor on user login");
		Messagor.instance.subscribe();
		log.info("register login countor on user login");
		UserLoginCountor.instance.onUserLoginForLoginCount();
		log.info("register all requirement pushor");
		RequirementPushor.instance.registerAll();
		log.info("register user cache updater");
		UserCacheUpdater.registerAll();
		log.info("register solr cache updater");
		cn.zthz.actor.solr.SolrUpdater.registerAll();
	}
	private static void startDaemon(){
		log.info("start daemon threads");
		DaemonEntry.startDaemon();
	}

}
