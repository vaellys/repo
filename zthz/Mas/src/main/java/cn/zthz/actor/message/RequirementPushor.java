package cn.zthz.actor.message;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.zthz.actor.assemble.Global;
import cn.zthz.actor.assemble.GlobalConfig;
import cn.zthz.actor.queue.QueueSubjects;
import cn.zthz.tool.account.AccountService;
import cn.zthz.tool.common.Formats;
import cn.zthz.tool.common.HttpUtils;
import cn.zthz.tool.common.HzException;
import cn.zthz.tool.common.JsonUtils;
import cn.zthz.tool.common.ResourceUtils;
import cn.zthz.tool.common.StringUtils;
import cn.zthz.tool.message.MessageTypes;
import cn.zthz.tool.message.RMessageService;
import cn.zthz.tool.proxy.UserProxy;
import cn.zthz.tool.push.PushException;
import cn.zthz.tool.push.PushService;
import cn.zthz.tool.queue.OnMessage;
import cn.zthz.tool.requirement.Requirement;
import cn.zthz.tool.share.QqShare;
import cn.zthz.tool.share.ShareAction;
import cn.zthz.tool.share.WeiboShare;
import cn.zthz.tool.user.User;

public class RequirementPushor {
	private static final Log log = LogFactory.getLog(RequirementPushor.class);
	public static final RequirementPushor instance = new RequirementPushor();

	private RequirementPushor() {
	}

	public void registerAll() {
		onRequirementPublished();
		onRequirementCompete();
		onRequirementComplete();
		onRequirementDrawback();
		onRequirementExpired();
		onRequirementSeletedCandidate();
		onRequirementClosed();
		onChargeSuccess();
	}

	public void onChargeSuccess() {
		Global.queue.subscribe(new OnMessage() {
			@Override
			public void handle(String subject, Object message) {
				Map<String, Object> map = (Map<String, Object>) message;
				if (log.isInfoEnabled()) {
					log.info("send charged message:" + JsonUtils.toJsonString(map));
				}
				// String userId = (String) map.get("userId");
				// String requirementId = (String) map.get("requirementId");
				BigDecimal money = (BigDecimal) map.get("money");
				String userId = (String) map.get("userId");
				// String candidateName = (String) map.get("candidateName");
				// String requirementTitle = (String)
				// map.get("requirementTitle");
				// String userName = (String) map.get("userName");
				try {
					String alert = "充值成功：您的账户成功充值" + money + "元,余额：" + AccountService.instance.getBalance(userId);
					map.put("t", MessageTypes.ACCOUNT_CHARGED_SUCCESS);
//					PushService.instance.push(Global.SYSTEM_USER_ID, userId, alert, map);
					pushMessage(null, userId, alert, alert, MessageTypes.ACCOUNT_CHARGED_SUCCESS);
				} catch (HzException e) {
					log.error("push requirement expired message failed!", e);
				}
			}
		}, QueueSubjects.ACCOUNT_CHARGED_SUCCESS);
	}
	public void onQuickPaySuccess() {
		Global.queue.subscribe(new OnMessage() {
			@Override
			public void handle(String subject, Object message) {
				Map<String, Object> map = (Map<String, Object>) message;
				if (log.isInfoEnabled()) {
					log.info("send quick pay message to sponsor :" + JsonUtils.toJsonString(map));
				}
				// String userId = (String) map.get("userId");
				String requirementId = (String) map.get("requirementId");
				BigDecimal money = (BigDecimal) map.get("money");
				BigDecimal balance = (BigDecimal) map.get("balance");
				String userId = (String) map.get("userId");
				// String candidateName = (String) map.get("candidateName");
				// String requirementTitle = (String)
				// map.get("requirementTitle");
				// String userName = (String) map.get("userName");
				try {
					String alert = "您已成功支付" + money + "元,余额："+balance;
					map.put("t", MessageTypes.REQUIREMENT_QUICK_PAY);
//					PushService.instance.push(Global.SYSTEM_USER_ID, userId, alert, map);
					pushMessage(requirementId, userId, alert, alert, MessageTypes.REQUIREMENT_QUICK_PAY);
				} catch (HzException e) {
					log.error("push requirement expired message failed!", e);
				}
			}
		}, QueueSubjects.REQUIREMENT_QUICK_PAY);
	}

	/**
	 * id , title , userId
	 */
	public void onRequirementExpired() {
		Global.queue.subscribe(new OnMessage() {
			@Override
			public void handle(String subject, Object message) {
				Map<String, Object> map = (Map<String, Object>) message;
				if (log.isInfoEnabled()) {
					log.info("send expire message:" + JsonUtils.toJsonString(map));
				}
				String userId = (String) map.get("userId");
				String requirementId = (String) map.get("id");
				// String candidateId = (String) map.get("selectedCandidate");
				BigDecimal price = (BigDecimal) map.get("price");
				String title = (String) map.get("title");
				boolean hasMandate = (boolean) map.get("hasMandate");
				String alert = null;
				String m = null;
				if (hasMandate) {
					alert = "您的需求:" + StringUtils.abbreviate(null != title ? title : "", 20) + "已经过期！已成功退款：" + price + "元";
					m = "您的需求:" + title + "已经过期！已成功退款：" + price + "元";
				} else {
					alert = "您的需求:" + StringUtils.abbreviate(null != title ? title : "", 20) + "已经过期！";
					m = "您的需求:" + title + "已经过期！";
				}
				try {
					// int mid = RMessageService.instance.save(requirementId,
					// userId, m, MessageTypes.REQUIREMENT_EXPIRED);
					// Map<String, Object> attach = new HashMap<String,
					// Object>();
					// attach.put("mid", mid);
					// attach.put("t", MessageTypes.REQUIREMENT_EXPIRED);
					// PushService.instance.push(Global.SYSTEM_USER_ID, userId,
					// alert, attach);
					pushMessage(requirementId, userId, alert, m, MessageTypes.REQUIREMENT_EXPIRED);
				} catch ( HzException e) {
					log.error("push requirement expired message failed!", e);
				}
			}
		}, QueueSubjects.REQUIREMENT_EXPIRED);
	}

	public void onRequirementPublished() {
		Global.queue.subscribe(new OnMessage() {
			@Override
			public void handle(String subject, Object message) {
				Requirement requirement = (Requirement) message;
				if(!requirement.getHasMandate()){
					shareWB(requirement);
					return; 
				}
				if (log.isInfoEnabled()) {
					log.info("send publish message:" + JsonUtils.toJsonString(requirement.getId()));
				}
				String userId = requirement.getUserId();
				String requirementId = requirement.getId();
				String title = requirement.getTitle();
				String atitle = StringUtils.ellipsisString(title, 20);
				
				// String userName = (String) map.get("userName");
				try {
					BigDecimal price = Formats.formatMoney(requirement.getPrice());
					BigDecimal balance = AccountService.instance.getBalance(userId);
					String alert ="您已成功发布托管需求:"+atitle+"；扣款:" +price + "元，余额："+ balance;
					String m ="您已成功发布托管需求:"+title+"；扣款:" +price + "元，余额："+ balance;
					// int mid = RMessageService.instance.save(requirementId,
					// userId, m, MessageTypes.REQUIREMENT_COMPETE);
					// Map<String, Object> attach = new HashMap<String,
					// Object>();
					// attach.put("mid", mid);
					// attach.put("t", MessageTypes.REQUIREMENT_COMPETE);
					// PushService.instance.push(Global.SYSTEM_USER_ID, userId,
					// alert, attach);
					pushMessage(requirementId, userId, alert, m, MessageTypes.REQUIREMENT_PUBLISHED);
//					String images = requirement.getMainPicture();
//					Map<String, Object> params = new HashMap<String, Object>();
//					if(images != null){
//						byte[] pic = HttpUtils.doGetInBytes(images);
//						params.put("pic", pic);
//					}
//					String url = GlobalConfig.get("share.requirement.url") + requirementId;
//					params.put("comment", body);
//					params.put("body", body);
//					params.put("images", images);
//					params.put("title", title);
//					params.put("summary", description);
//					params.put("url", url);
//					if(ShareAction.SHARE_QQ == shareQQ){
//						QqShare.instance.share(qqAccessToken, params);
//					}else if(ShareAction.SHARE_WB == shareWB){
//						WeiboShare.instance.share(weiboAccessToken, params);
//					}
					shareWB(requirement);
				} catch (PushException e) {
					log.error("push requirement expired message failed!", e);
				} catch (HzException e) {
					log.error("save requirement expired message failed!", e);
				}
			}
		}, QueueSubjects.REQUIREMENT_PUBLISHED);
	}
	/**
	 * requirementId ,requirementTitle,candidateId, candidateName ,userId
	 */
	public void onRequirementCompete() {
		Global.queue.subscribe(new OnMessage() {
			@Override
			public void handle(String subject, Object message) {
				Map<String, Object> map = (Map<String, Object>) message;
				if (log.isInfoEnabled()) {
					log.info("send compete message:" + JsonUtils.toJsonString(map));
				}
				String userId = (String) map.get("userId");
				String requirementId = (String) map.get("id");
				String candidateId = (String) map.get("candidateId");
				String title = (String) map.get("title");
				String candidateName = UserProxy.getUserInfo(candidateId).getName();

				// String userName = (String) map.get("userName");
				String alert = StringUtils.abbreviate(null != candidateName ? candidateName : "", 20) + "参与了:"
						+ StringUtils.abbreviate(null != title ? title : "", 20) + "需求的竞争！";
				String m = candidateName + "参与了:" + title + "需求的竞争！";
				try {
					// int mid = RMessageService.instance.save(requirementId,
					// userId, m, MessageTypes.REQUIREMENT_COMPETE);
					// Map<String, Object> attach = new HashMap<String,
					// Object>();
					// attach.put("mid", mid);
					// attach.put("t", MessageTypes.REQUIREMENT_COMPETE);
					// PushService.instance.push(Global.SYSTEM_USER_ID, userId,
					// alert, attach);
					pushMessage(requirementId, userId, alert, m, MessageTypes.REQUIREMENT_COMPETE);
				} catch (PushException e) {
					log.error("push requirement expired message failed!", e);
				} catch (HzException e) {
					log.error("save requirement expired message failed!", e);
				}
			}
		}, QueueSubjects.REQUIREMENT_COMPETE);
	}

	/**
	 * requirementId ,requirementTitle, userId ,userName , candidateId,
	 */
	public void onRequirementSeletedCandidate() {
		Global.queue.subscribe(new OnMessage() {
			@Override
			public void handle(String subject, Object message) {
				Map<String, Object> map = (Map<String, Object>) message;
				if (log.isInfoEnabled()) {
					log.info("send selected candidate message:" + JsonUtils.toJsonString(map));
				}
				String userId = (String) map.get("userId");
				String requirementId = (String) map.get("id");
				String candidateId = (String) map.get("selectedCandidate");
				String title = (String) map.get("title");
				String userName = UserProxy.getUserInfo(userId).getName();
				String alert = userName + "的需求:" + StringUtils.abbreviate(null != title ? title : "", 20) + "已经选定您为承接人！";
				String m = userName + "的需求:" + title + "已经选定您为承接人！";
				try {
					// int mid = RMessageService.instance.save(requirementId,
					// userId, m, MessageTypes.REQUIREMENT_SELECTED_CANDIDATE);
					// Map<String, Object> attach = new HashMap<String,
					// Object>();
					// attach.put("mid", mid);
					// attach.put("t",
					// MessageTypes.REQUIREMENT_SELECTED_CANDIDATE);
					// PushService.instance.push(Global.SYSTEM_USER_ID,
					// candidateId, alert, attach);
					pushMessage(requirementId, candidateId, alert, m, MessageTypes.REQUIREMENT_SELECTED_CANDIDATE);
				} catch (PushException e) {
					log.error("push requirement selected candidate message failed!", e);
				} catch (HzException e) {
					log.error("save requirement selected candidate message failed!", e);
				}
			}
		}, QueueSubjects.REQUIREMENT_SELECTED_CANDIDATE);
	}

	/**
	 * requirementId ,requirementTitle,candidateId,userId
	 */
	public void onRequirementComplete() {
		Global.queue.subscribe(new OnMessage() {
			@Override
			public void handle(String subject, Object message) {
				Map<String, Object> map = (Map<String, Object>) message;
				if (log.isInfoEnabled()) {
					log.info("send complete message:" + JsonUtils.toJsonString(map));
				}
				String userId = (String) map.get("userId");
				String requirementId = (String) map.get("id");
				String candidateId = (String) map.get("selectedCandidate");
				String title = (String) map.get("title");
				// String candidateName = (String) map.get("candidateName");
				// String userName = (String) map.get("userName");
				String alert = "您承接的需求：" + StringUtils.abbreviate(null != title ? title : "", 20) + " 已经完成，对方已付款。";
				String m = "您承接的需求：" + title + " 已经完成，对方已付款。";
				try {
					UserProxy.refreshUserCaches(userId);
					pushMessage(requirementId, candidateId, alert, m, MessageTypes.REQUIREMENT_COMPLETE);
				} catch (PushException e) {
					log.error("push requirement complete  message failed!", e);
				} catch (HzException e) {
					log.error("save requirement complete message failed!", e);
				}
			}

		}, QueueSubjects.REQUIREMENT_COMPLETE);
	}

	private void pushMessage(String requirementId, String receiverId, String alert, String m, int messageType) throws HzException,
			PushException {
		if (PushService.instance.allowPush(receiverId)) {
			int mid = RMessageService.instance.save(requirementId, receiverId, m, messageType, 1);
			Map<String, Object> attach = new HashMap<String, Object>();
			attach.put("mid", mid);
			attach.put("t", messageType);
			PushService.instance.push(Global.SYSTEM_USER_ID, receiverId, alert, attach);
		} else {
			RMessageService.instance.save(requirementId, receiverId, m, messageType, 0);
		}
	}
	public static void main(String[] args) throws PushException, HzException {
		RequirementPushor.instance.pushMessage("402880173dce7ef6013dd82cc987006d", "402880173dce7ef6013dd25ea0ca0057", "", "", 11);
	}

	public void onRequirementClosed() {
		Global.queue.subscribe(new OnMessage() {
			@Override
			public void handle(String subject, Object message) {
				Map<String, Object> map = (Map<String, Object>) message;
				if (log.isInfoEnabled()) {
					log.info("send close message:" + JsonUtils.toJsonString(map));
				}
				String userId = (String) map.get("userId");
				String requirementId = (String) map.get("id");
				String candidateId = (String) map.get("selectedCandidate");
				BigDecimal money = (BigDecimal) map.get("price");
				boolean hasMandate = (boolean) map.get("hasMandate");
				// String candidateName = (String) map.get("candidateName");
				String title = (String) map.get("title");
				// String userName = (String) map.get("userName");
				String atitle = StringUtils.abbreviate(null != title ? title : "", 20);
				String alert = atitle + "已经关闭！";
				String m = title + "已经关闭！";
				try {
					UserProxy.refreshUserCaches(userId);
					// int mid = RMessageService.instance.save(requirementId,
					// userId, m, MessageTypes.REQUIREMENT_CLOSED);
					// Map<String, Object> attach = new HashMap<String,
					// Object>();
					// attach.put("mid", mid);
					// attach.put("t", MessageTypes.REQUIREMENT_CLOSED);
					// PushService.instance.push(Global.SYSTEM_USER_ID,
					// candidateId, alert, attach);
					String apaybackMessage = "您已关闭需求：" + atitle + ",已成功退款：" + money + "元";
					String paybackMessage = "您已关闭需求：" + title + ",已成功退款：" + money + "元";
					if (StringUtils.isNotBlank(candidateId)) {
						pushMessage(requirementId, candidateId, alert, m, MessageTypes.REQUIREMENT_CLOSED);
					}
					if(hasMandate){
						pushMessage(requirementId, userId, apaybackMessage, paybackMessage, MessageTypes.REQUIREMENT_CLOSED_ME);
					}
				} catch (PushException e) {
					log.error("close requirement complete  message failed!", e);
				} catch (HzException e) {
					log.error("close requirement complete message failed!", e);
				}
			}
		}, QueueSubjects.REQUIREMENT_CLOSED);
	}

	/**
	 * requirementId ,requirementName, userId ,userName , candidateId,
	 */
	public void onRequirementDrawback() {
		Global.queue.subscribe(new OnMessage() {
			@Override
			public void handle(String subject, Object message) {
				Map<String, Object> map = (Map<String, Object>) message;
				if (log.isInfoEnabled()) {
					log.info("send drawback message:" + JsonUtils.toJsonString(map));
				}
				String userId = (String) map.get("userId");
				String requirementId = (String) map.get("id");
				String candidateId = (String) map.get("selectedCandidate");
				String title = (String) map.get("title");
				String userName = UserProxy.getUserInfo(userId).getName();
				String alert = userName + "对需求:" + StringUtils.abbreviate(null != title ? title : "", 20) + "已经申请退款！";
				String m = userName + "对需求:" + title + "已经申请退款！";
				try {
					// int mid = RMessageService.instance.save(requirementId,
					// userId, m, MessageTypes.REQUIREMENT_DRAWBACK);
					// Map<String, Object> attach = new HashMap<String,
					// Object>();
					// attach.put("mid", mid);
					// attach.put("t", MessageTypes.REQUIREMENT_DRAWBACK);
					// PushService.instance.push(Global.SYSTEM_USER_ID,
					// candidateId, alert, attach);
					pushMessage(requirementId, candidateId, alert, m, MessageTypes.REQUIREMENT_CLOSED);
				} catch ( HzException e) {
					log.error("push requirement expired message failed!", e);
				}
			}
		}, QueueSubjects.REQUIREMENT_DRAWBACK);
	}
	
	private void shareWB(Requirement requirement){
		String userId = requirement.getUserId();
		BigDecimal money = requirement.getPrice();
		BigDecimal formatMoney = money.setScale(0,RoundingMode.DOWN);
		User user = UserProxy.getUserInfo(userId);
		String qqAccessToken = user.getQqAccessToken();
		String weiboAccessToken = user.getWeiboAccessToken();
		String requirementId = requirement.getId();
		String title = requirement.getTitle();
		String body = title;
		String description = requirement.getDescription();
		Integer shareqq = requirement.getShareQQ();
		int shareQQ = (null != shareqq) ? (int)shareqq : 0;
		Integer sharewb = requirement.getShareWB();
		int shareWB = (null != sharewb) ? (int)sharewb : 0;
		String images = requirement.getMainPicture();
		Map<String, Object> params = new HashMap<String, Object>();
		try{
			if(images != null){
				byte[] pic = HttpUtils.doGetInBytes(images);
				params.put("pic", pic);
			}
			String url = GlobalConfig.get("share.requirement.url") + requirementId;
			String abody = StringUtils.abbreviate(title, 40);
			String atitle = StringUtils.abbreviate(title, 20);
			params.put("comment", abody);
//			StringBuilder avbody = new StringBuilder();
//			avbody.append("#分享任务# ");
//			avbody.append(body);
//			avbody.append(" 【￥");
//			avbody.append(formatMoney);
//			avbody.append("】 | ");
			String avbody = ResourceUtils.getContent(body, formatMoney).toString();
			//avbody.append(description);
			String rebody = StringUtils.abbreviate(avbody + description, 100);
			params.put("body", rebody + url);
			params.put("images", images);
			StringBuilder retitle = ResourceUtils.getContent(atitle, formatMoney);
			String reatitle = retitle.delete(retitle.toString().length() - 3, retitle.toString().length()).toString();
			params.put("title", reatitle);
			String adescription = StringUtils.abbreviate(null != description?description:"", 40);
			params.put("summary", adescription + " " + url);
			params.put("url", url);
			if(ShareAction.SHARE_QQ == shareQQ){
				QqShare.instance.share(qqAccessToken, params);
			}
			if(ShareAction.SHARE_WB == shareWB){
				WeiboShare.instance.share(weiboAccessToken, params);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
}
