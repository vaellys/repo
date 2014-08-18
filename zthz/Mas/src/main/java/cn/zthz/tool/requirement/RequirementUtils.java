package cn.zthz.tool.requirement;


public class RequirementUtils {
	public static String publishTime(long currentTime , long expireTime){
		long remain = expireTime - currentTime;
		if(remain<=0){
			return "已过期";
		}
		long remainMins = (remain)/(1000*60);
		if(remainMins<=1){
			return "刚刚发布";
		}
		if(remainMins<60){
			return remainMins+"分钟前发布";
		}
		long remainHours = remainMins/60;
		long remainDays = remainHours/24;
		if(remainDays<1){
			return remainHours+"小时前发布";
		}
		
		return remainDays+"天前发布";
	}
	public static String remainTime(long currentTime , long expireTime){
		long remain = expireTime - currentTime;
		if(remain<=0){
			return "已过期";
		}
		long remainMins = (remain)/(1000*60);
		if(remainMins<=1){
			return "剩余不到1分钟";
		}
		if(remainMins<60){
			return "剩余"+remainMins+"分钟";
		}
		long remainHours = remainMins/60;
		long remainDays = remainHours/24;
		if(remainDays<1){
			return "剩余"+remainHours+"小时";
		}
		
		return "剩余"+remainDays+"天";
	}
}
