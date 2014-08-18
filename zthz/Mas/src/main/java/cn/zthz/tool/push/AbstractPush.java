package cn.zthz.tool.push;

public abstract class AbstractPush implements Push{
	
	private static final IosJavaPnsPush IOS_JAVA_PNS_PUSH = new IosJavaPnsPush();
	private static final AndroidJPush ANDROID_J_PUSH = new AndroidJPush();
	public static AbstractPush getInstance(int type){
		if(0 == type){
			return IOS_JAVA_PNS_PUSH;
		}
		if(1 == type){
			return ANDROID_J_PUSH;
		}
		throw new RuntimeException("not support this device type:"+type);
		
	}

}
