package top.yxgu.hall.model;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;

public class UserManager {
	public static final AttributeKey<Integer> CH_ID = AttributeKey.valueOf("ch_id");
	
	private static ChannelGroup allChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	private static ConcurrentMap<Integer, UserData> userMap = new ConcurrentHashMap<>();
	
	public static void add(UserData data) {
		Channel c = data.channel;
		allChannels.add(c);
		UserData rc = userMap.put(data.userId, data);
		if (rc!= null && rc != c) {
			allChannels.remove(c);
			c.close();
		}
		Attribute<Integer> userIdAttr = c.attr(CH_ID);
		userIdAttr.setIfAbsent(data.userId);
	}
	
	public static boolean contains(Channel c) {
		return allChannels.contains(c);
	}
	
	public static boolean containsById(int id) {
		return userMap.containsKey(id);
	}
	
	public static void remove(Channel c) {
//		if (!allChannels.contains(c)) return;
		Attribute<Integer> userIdAttr = c.attr(CH_ID);
		Integer userId = userIdAttr.get();
		if (userId != null) {
			userMap.remove(userId);
			allChannels.remove(c);
		}
	}
	
	public static void removeById(int id) {
		UserData data = userMap.get(id);
		allChannels.remove(data.channel);
		userMap.remove(id);
	}
	
	public static UserData get(int id) {
		return userMap.get(id);
	}
	
	public static void writeAndFlush(Object message) {
		allChannels.writeAndFlush(message);
	}
	
	public static ChannelGroupFuture close() {
		return allChannels.close();
	}
	
	public static int size() {
		return allChannels.size();
	}
}
