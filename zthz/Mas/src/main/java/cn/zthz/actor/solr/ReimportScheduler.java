package cn.zthz.actor.solr;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import cn.zthz.actor.assemble.GlobalConfig;
import cn.zthz.tool.common.HttpUtils;

public class ReimportScheduler implements Job {
	
	private static final Log log = LogFactory.getLog(ReimportScheduler.class);

	private static final String USER_FULL_IMPORT_URL = "http://" + GlobalConfig.get("solr.innerAddress")
			+ "/solr/user/dataimport?command=full-import&verbose=true&clean=true&commit=true&optimize=true&entity=user&wt=json";
	private static final String REQUIREMENT_FULL_IMPORT_URL = "http://" + GlobalConfig.get("solr.innerAddress")
			+ "/solr/requirement/dataimport?command=full-import&verbose=true&clean=true&commit=true&entity=requirement&optimize=true&wt=json";

	
	public static void start() throws SchedulerException {
		if(log.isInfoEnabled())
		log.info("start ReimportScheduler");
		SchedulerFactory sf = new StdSchedulerFactory();
		Scheduler sched = sf.getScheduler();
		JobDetail userDeltaImportJob = newJob(ReimportScheduler.class).withIdentity("userDeltaImportJob", "group1").build();
		
		CronTrigger trigger = newTrigger().withIdentity("trigger1", "group1").withSchedule(cronSchedule("0 0 0/2 * * ?")).build();
		
		sched.scheduleJob(userDeltaImportJob, trigger);
		sched.start();
	}

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try {
			if(log.isInfoEnabled())
			log.info("reimport requirement and user index");
			String r = HttpUtils.doGet(REQUIREMENT_FULL_IMPORT_URL);
			String u = HttpUtils.doGet(USER_FULL_IMPORT_URL);
			if(log.isInfoEnabled()){
				log.info(r);
				log.info(u);
			}
		} catch (IOException e) {
			log.error("" ,e);
		}
	}
}
