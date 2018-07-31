package top.yxgu.hall.model;

import java.util.List;

import io.netty.channel.Channel;

public class UserData {
	public int userId;
	public int roomType;
	public int roomId;
	public String host;
	public int port;
	
//	taskInfo
	public List<ItemInfo> itemInfo;
	public int systemTime;
	public int canReliefTime;
	public int everydayActive;
	public int everyWeekActive;
	public int newbieGuideId;
//	monthSignActiveInfo
	public int isTodayFirstLogin;
	public int isTodayDraw;
	public String selfInviteCode;
	
	/////// PlayerInfo ////////////
	public String nickName;//玩家昵称
	public String iconUrl;//玩家头像
	public int gems;
	public long coins;
	public int position;	
	public int gunId;
	public int maxGunId;
	public List<Integer> items;//正在使用的锁定道具ID
//	public repeated LockRelation lockRelation;
	public int roleLevel;
	public int roleExp;
	public int vipLevel;
	public int batterySkinId;//炮台皮肤
	public int gunrestSkinId;//炮座皮肤
	public int coupon;//点券
	public int totalChargeRMB;//累计充值金额(单位是分)
	public int monthEndTime;//月卡过期时间
	public int gunPow; //威力
	
	public Channel channel;
	public boolean isInRoom = false;
}
