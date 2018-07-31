package top.yxgu.hall.model;

import io.netty.channel.Channel;

public class RoomServerData {
	public int id;
	public Channel channel;
	
//	public String host;
//	public int port;
	public int maxRoomNum;
	public int currRoomNum;
	public int currOnlineNum;
	
	public String url;
}
