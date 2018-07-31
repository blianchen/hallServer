package top.yxgu.hall.webSocket;

import static io.netty.buffer.Unpooled.wrappedBuffer;

import java.util.List;

import org.springframework.stereotype.Component;

import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;

import App.Model.Net.MsgOuterClass.Msg;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

@Component
@Sharable
public class WebSocketMessageCodec extends MessageToMessageCodec<WebSocketFrame, MessageLiteOrBuilder> {

	public WebSocketMessageCodec() {
	}
	
	@Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> out) throws Exception {
        if (frame instanceof BinaryWebSocketFrame) {
        	ByteBuf msg = frame.content();
            final byte[] array;
            final int offset;
            final int length = msg.readableBytes();
            if (msg.hasArray()) {
                array = msg.array();
                offset = msg.arrayOffset() + msg.readerIndex();
            } else {
                array = new byte[length];
                msg.getBytes(msg.readerIndex(), array, 0, length);
                offset = 0;
            }
            
            out.add(Msg.parser().parseFrom(array, offset, length));
        } else {
        	System.out.println("Not BinaryWebSocketFrame: "+frame.getClass().getName());
        }
    }

	@Override
	protected void encode(ChannelHandlerContext ctx, MessageLiteOrBuilder msg, List<Object> out) throws Exception {
		ByteBuf buf = null;
		if (msg instanceof MessageLite) {
			buf = wrappedBuffer(((MessageLite) msg).toByteArray());
        } else if (msg instanceof MessageLite.Builder) {
        	buf = wrappedBuffer(((MessageLite.Builder) msg).build().toByteArray());
        } else {
        	return;
        }
        
		BinaryWebSocketFrame frame = new BinaryWebSocketFrame(buf);
		out.add(frame.retain());
	}
}
