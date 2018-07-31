package top.yxgu.hall.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import App.Model.Net.MsgOuterClass.LoginRes;
import App.Model.Net.MsgOuterClass.PlayerInfo;
import redis.clients.jedis.Jedis;

@Service
public class UserInfoService {
	private static final String userKeyPrefix = "user.info.";
	
	private static Map<String, String> defaultUserData;
	static {
		defaultUserData = new HashMap<String, String>();
		defaultUserData.put("iconUrl", "");
		defaultUserData.put("gems", String.valueOf(55555));
		defaultUserData.put("coins", String.valueOf(88889999));
		defaultUserData.put("gunId", String.valueOf(19));
		defaultUserData.put("maxGunId", String.valueOf(1));
		defaultUserData.put("roleLevel", String.valueOf(1));
		defaultUserData.put("roleExp", String.valueOf(100));
		defaultUserData.put("batterySkinId", String.valueOf(20000));
		defaultUserData.put("gunrestSkinId", String.valueOf(80001));
	}
	
	@Resource
	private RedisPool redisPool;
	
	public UserInfoService() {
	}
	
	public LoginRes getLoginRes(int userId) {
		PlayerInfo pi = getPlayer(userId);
		LoginRes.Builder lb = LoginRes.newBuilder();
		lb.setPlayerInfo(pi);
		lb.setSystemTime( (int)(System.currentTimeMillis()/1000) );
		LoginRes res = lb.build();
		return res;
	}
	
	public PlayerInfo getPlayer(int userId) {
		String uid = String.valueOf(userId);
		Map<String, String> map;
		try (Jedis jedis = redisPool.getResource()) {
			map = jedis.hgetAll(uid);
		}
		if (map.isEmpty()) {
			try (Jedis jedis = redisPool.getResource(RedisPool.T_WRITE)) {
				map = defaultUserData;
				map.put("userId", uid);
				map.put("nickName", "rand"+uid);
				jedis.hmset(userKeyPrefix+uid, map);
			}
		}
		return mapToPlayer(map);
	}
	
	public PlayerInfo mapToPlayer(Map<String, String> map) {
		PlayerInfo.Builder pb = PlayerInfo.newBuilder();
		pb.setUserId(Integer.parseInt(map.get("userId")));
		pb.setNickName(map.get("nickName"));
		pb.setIconUrl(map.get("iconUrl"));
		pb.setGems(Integer.parseInt(map.get("gems")));
		pb.setCoins(Integer.parseInt(map.get("coins")));
		//uint32 position
		pb.setGunId(Integer.parseInt(map.get("gunId")));
		pb.setMaxGunId(Integer.parseInt(map.get("maxGunId")));
//		repeated uint32 items //正在使用的锁定道具ID
//		repeated LockRelation lockRelation
		pb.setRoleLevel(Integer.parseInt(map.get("roleLevel")));
		pb.setRoleExp(Integer.parseInt(map.get("roleExp")));
//		uint32 vipLevel
		pb.setBatterySkinId(Integer.parseInt(map.get("batterySkinId")));//炮台皮肤
		pb.setGunrestSkinId(Integer.parseInt(map.get("gunrestSkinId")));//炮座皮肤
//	    uint32 coupon = 16;//点券
//	    uint32 totalChargeRMB = 17;//累计充值金额(单位是分)
//	    uint32 monthEndTime = 18;//月卡过期时间
//	    uint32 gunPow = 19; //威力
		return pb.build();
	}
}
 