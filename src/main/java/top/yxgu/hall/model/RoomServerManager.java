package top.yxgu.hall.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;

public class RoomServerManager {
	public static final AttributeKey<Integer> ROOM_SERVER_ID = AttributeKey.valueOf("room_server_id");
	
	private static volatile int roomServerUid = 0;
	
	private static final Map<Integer, RoomServerData> roomServerMap = new HashMap<>();
	private static ChannelGroup allChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	
	public static synchronized int add(RoomServerData data) {
		int id = roomServerUid++;
		roomServerMap.put(id, data);
		data.id = id;
		Channel c = data.channel;
		Attribute<Integer> roomId = c.attr(ROOM_SERVER_ID);
		roomId.set(id);
		allChannels.add(c);
		return id;
	}
	
	public static synchronized void remove(Channel c) {
		Attribute<Integer> roomId = c.attr(ROOM_SERVER_ID);
		int id = roomId.get();
		roomServerMap.remove(id);
		allChannels.remove(c);
	}
	
	public static synchronized void removeById(int id) {
		Channel c = roomServerMap.get(id).channel;
		roomServerMap.remove(id);
		allChannels.remove(c);
	}
	
	public static RoomServerData get(int id) {
		return roomServerMap.get(id);
	}
	
	public static Iterator<RoomServerData> getRoomServers() {
		return roomServerMap.values().iterator();
	}
	
	public static int size() {
		return roomServerMap.size();
	}
}
