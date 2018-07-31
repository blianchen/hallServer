package top.yxgu.hall.service;

import java.util.Iterator;

import org.springframework.stereotype.Service;

import App.Model.Net.MsgActionDefine;
import App.Model.Net.MsgOuterClass.Msg;
import App.Model.Net.MsgOuterClass.RoomInfoRes;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import top.yxgu.hall.model.RoomServerData;
import top.yxgu.hall.model.RoomServerManager;
import top.yxgu.hall.model.UserData;
import top.yxgu.hall.roomScoket.RoomMessageDefine;

@Service
public class RoomInfoService {
//	@Autowired
//	private Pool<Jedis> jedisPool;
	
	public boolean selectRoom(int userId, int type, int roomId) {
		Iterator<RoomServerData> it = RoomServerManager.getRoomServers();
		float minRate = 1;
		float rate;
		RoomServerData sel = null;
		RoomServerData tmp;
		while (it.hasNext()) {
			tmp = it.next();
			if (tmp.currOnlineNum % 4 != 0) { //有人数未满的
				sel = tmp;
				break;
			}
			rate = (float)tmp.currRoomNum / tmp.maxRoomNum;
			if (rate < minRate) { //负载小的
				minRate = rate;
				sel = tmp;
			}
		}
		
		if (sel == null) {
			return false; // 服务器满
		} else {
			sendRequestRoomMsg(sel.channel, userId, type);
			return true;
		}
	}
	
	 public void sendRequestRoomMsg(Channel c, int userId, int type) {
		 ByteBuf msg = c.alloc().buffer();
		 msg.writeShort(RoomMessageDefine.REQUEST_ROOM_REQ);
		 msg.writeInt(userId);
		 msg.writeInt(type);
		 c.writeAndFlush(msg);
	}
	
	public void sendRoomInfoRes(UserData data) {
		RoomInfoRes.Builder bd = RoomInfoRes.newBuilder();
		bd.setRoomId(data.roomId);
		bd.setType(data.roomType);
		bd.setIp(data.host);
		bd.setPort(data.port);
		RoomInfoRes res = bd.build();
	 
		Msg.Builder mb = Msg.newBuilder();
		mb.setAction(MsgActionDefine.ROOMINFORES);
		mb.setMsgBody(res.toByteString());
		
		Channel c = data.channel;
		c.writeAndFlush(mb.build());
	}
}
