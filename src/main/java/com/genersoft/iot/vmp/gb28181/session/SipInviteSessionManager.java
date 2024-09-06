package com.genersoft.iot.vmp.gb28181.session;

import com.genersoft.iot.vmp.common.VideoManagerConstants;
import com.genersoft.iot.vmp.conf.UserSetting;
import com.genersoft.iot.vmp.gb28181.bean.SsrcTransaction;
import com.genersoft.iot.vmp.utils.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 视频流session管理器，管理视频预览、预览回放的通信句柄
 */
@Component
public class SipInviteSessionManager {

	@Autowired
	private UserSetting userSetting;

	@Autowired
	private RedisTemplate<Object, Object> redisTemplate;

	/**
	 * 添加一个点播/回放的事务信息
	 */
	public void put(SsrcTransaction ssrcTransaction){
		redisTemplate.opsForValue().set(VideoManagerConstants.SIP_INVITE_SESSION_STREAM + userSetting.getServerId()
				+ ":" + ssrcTransaction.getStream(), ssrcTransaction);

		redisTemplate.opsForValue().set(VideoManagerConstants.SIP_INVITE_SESSION_CALL_ID + userSetting.getServerId()
				+ ":" + ssrcTransaction.getCallId(), ssrcTransaction);
	}

	public SsrcTransaction getSsrcTransactionByStream(String stream){
		String key = VideoManagerConstants.SIP_INVITE_SESSION_STREAM + userSetting.getServerId() + ":" + stream;
		return (SsrcTransaction)redisTemplate.opsForValue().get(key);
	}

	public SsrcTransaction getSsrcTransactionByCallId(String callId){
		String key = VideoManagerConstants.SIP_INVITE_SESSION_CALL_ID + userSetting.getServerId() + ":" + callId;
		return (SsrcTransaction)redisTemplate.opsForValue().get(key);
	}

	public List<SsrcTransaction> getSsrcTransactionByDeviceId(String deviceId){
		String key = VideoManagerConstants.SIP_INVITE_SESSION_CALL_ID + userSetting.getServerId() + ":*";

		List<Object> scanResult = RedisUtil.scan(redisTemplate, key);
		if (scanResult.isEmpty()) {
			return new ArrayList<>();
		}
		List<SsrcTransaction> result = new ArrayList<>();
		for (Object keyObj : scanResult) {
			SsrcTransaction ssrcTransaction = (SsrcTransaction)redisTemplate.opsForValue().get(keyObj);
			if (ssrcTransaction != null && ssrcTransaction.getDeviceId().equals(deviceId)) {
				result.add(ssrcTransaction);
			}
		}
		return result;

	}
	
	public void removeByStream(String stream) {
		SsrcTransaction ssrcTransaction = getSsrcTransactionByStream(stream);
		if (ssrcTransaction == null ) {
			return;
		}
		redisTemplate.delete(VideoManagerConstants.SIP_INVITE_SESSION_STREAM + userSetting.getServerId() + ":" +  stream);
		if (ssrcTransaction.getCallId() != null) {
			redisTemplate.delete(VideoManagerConstants.SIP_INVITE_SESSION_CALL_ID + userSetting.getServerId() + ":" +  ssrcTransaction.getCallId());
		}
	}

	public void removeByCallId(String callId) {
		SsrcTransaction ssrcTransaction = getSsrcTransactionByCallId(callId);
		if (ssrcTransaction == null ) {
			return;
		}
		redisTemplate.delete(VideoManagerConstants.SIP_INVITE_SESSION_CALL_ID + userSetting.getServerId() + ":" +  callId);
		if (ssrcTransaction.getStream() != null) {
			redisTemplate.delete(VideoManagerConstants.SIP_INVITE_SESSION_STREAM + userSetting.getServerId() + ":" +  ssrcTransaction.getStream());
		}
	}

	public List<SsrcTransaction> getAll() {
		String key = VideoManagerConstants.SIP_INVITE_SESSION_CALL_ID + userSetting.getServerId() + ":*";

		List<Object> scanResult = RedisUtil.scan(redisTemplate, key);
		if (scanResult.isEmpty()) {
			return new ArrayList<>();
		}
		List<SsrcTransaction> result = new ArrayList<>();
		for (Object keyObj : scanResult) {
			SsrcTransaction ssrcTransaction = (SsrcTransaction)redisTemplate.opsForValue().get(keyObj);
			result.add(ssrcTransaction);
		}
		return result;
	}
}
