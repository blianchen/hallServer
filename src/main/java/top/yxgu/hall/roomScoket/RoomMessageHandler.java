package top.yxgu.hall.roomScoket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import App.Model.Net.MsgActionDefine;
import App.Model.Net.MsgOuterClass.RoomInfoRes;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import top.yxgu.hall.model.RoomServerData;
import top.yxgu.hall.model.RoomServerManager;
import top.yxgu.hall.model.UserData;
import top.yxgu.hall.model.UserManager;
import top.yxgu.utils.CommonFun;

@Controller
@Sharable
public class RoomMessageHandler extends SimpleChannelInboundHandler<ByteBuf> {
	
	private static final Logger log = LoggerFactory.getLogger(RoomMessageHandler.class);
	
	@Value("${server.group.id}")
	private int groupId;
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		RoomServerManager.remove(ctx.channel());
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
		int action = msg.readShort();
		switch (action) {
			case RoomMessageDefine.SYNC_ROOM_INF: { // 7
				int id = msg.readInt();
				RoomServerData roomServerData = RoomServerManager.get(id);
				roomServerData.currRoomNum = msg.readInt();
				roomServerData.currOnlineNum = msg.readInt();
				break;
			}
			case RoomMessageDefine.REQUEST_ROOM_RES: { // 3
				int roomServerId = msg.readInt();
				int userId = msg.readInt();
				int type = msg.readInt();
				int roomId = msg.readInt();
				RoomServerData roomServerData = RoomServerManager.get(roomServerId);
				if (roomServerData == null) {
					//TODO 发送重新登录给用户
					break;
				}
				roomServerData.currRoomNum = msg.readInt();
				roomServerData.currOnlineNum = msg.readInt();
				
				UserData userData = UserManager.get(userId);
				userData.roomId = roomId;
				userData.roomType = type;
				userData.isInRoom = true;
				
				RoomInfoRes.Builder resBd = RoomInfoRes.newBuilder();
				resBd.setFlag(1);
				resBd.setIp(roomServerData.url);
				resBd.setPort(0);
				resBd.setRoomId(roomId);
				resBd.setType(type);
				CommonFun.sendProtoBufMsg(userData.channel, MsgActionDefine.ROOMINFORES, resBd.build());
				break;
			}
			case RoomMessageDefine.REGISTER_REQ: { // 1  room server register
				RoomServerData data = new RoomServerData();
				data.url = CommonFun.readStr(msg);
//				data.port = msg.readInt();
				data.maxRoomNum = msg.readInt();
				data.currRoomNum = msg.readInt();
				data.currOnlineNum = msg.readInt();
				data.channel = ctx.channel();
				int roomServerId = RoomServerManager.add(data);
				
				ByteBuf sndMsg = ctx.alloc().buffer();
				sndMsg.writeShort(RoomMessageDefine.REGISTER_RES);
				sndMsg.writeInt(groupId);
				sndMsg.writeInt(roomServerId);
				ctx.writeAndFlush(sndMsg);
				// 同步配置文件消息
				sendSyncConfigMessage(ctx);
				break;
			}
			case RoomMessageDefine.SYNC_CONFIG_RES: { // 2
				break;
			}
			default: {
				log.warn("Unkonw RoomMessage action:"+action);
			}
		}
	}
	
	 @Override
	 public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		 ctx.flush();
	 }
	 
	 @Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		 log.warn(cause.toString());
		 ctx.close();
	}
	 
	private void sendSyncConfigMessage(ChannelHandlerContext ctx) {
//		String[] strArr = ConfigManager.sendRoomConfig;
		ByteBuf sndMsg = ctx.alloc().buffer();
		sndMsg.writeShort(RoomMessageDefine.SYNC_CONFIG_REQ);
//		sndMsg.writeInt(strArr.length);
//		for (int i=0; i<strArr.length; i++) {
//			CommonFun.writeStr(sndMsg, strArr[i]);
//		}
		ctx.writeAndFlush(sndMsg);
	}

}
