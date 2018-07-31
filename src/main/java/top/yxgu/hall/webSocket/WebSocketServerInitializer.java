package top.yxgu.hall.webSocket;

import javax.annotation.Resource;
import javax.net.ssl.SSLEngine;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;

@Component
public class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {
	@Resource
	private ClientMessageHandler clientMessageHandler;
	@Resource
	private WebSocketMessageCodec webSocketMessageCodec;
	
//	private ProtobufEncoder protobufEncoder;
//	private ProtobufDecoder protobufDecoder;
	
	@Resource
    private SslContext sslContext;
	
	@Value("${websocket.server.path:/ws}")
    private String websocketPath;
	@Value("${websocket.server.heartbeatTimeOut:90}")
	private int readerIdleTimeSeconds;


    public WebSocketServerInitializer() {
//    	protobufEncoder = new ProtobufEncoder();
//    	protobufDecoder = new ProtobufDecoder(Msg.getDefaultInstance());
    }
	
	@Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (sslContext != null) {
        	SSLEngine engine = sslContext.newEngine(ch.alloc()); 
        	engine.setNeedClientAuth(false);
        	engine.setUseClientMode(false);
        	pipeline.addFirst(new SslHandler(engine));
        }
        
        //HttpServerCodec: 针对http协议进行编解码
        pipeline.addLast(new HttpServerCodec());
        //HttpObjectAggregator会把多个消息转换为一个单一的FullHttpRequest或是FullHttpResponse
        pipeline.addLast(new HttpObjectAggregator(65536));
//        pipeline.addLast(new WebSocketServerCompressionHandler());
        pipeline.addLast(new WebSocketServerProtocolHandler(websocketPath, null, true));
        
        pipeline.addLast(webSocketMessageCodec);
//        pipeline.addLast(new LoggingHandler(LogLevel.INFO));
//        pipeline.addLast(protobufEncoder);
//        pipeline.addLast(protobufDecoder);
        
        pipeline.addLast(new IdleStateHandler(readerIdleTimeSeconds, 0, 0));
        pipeline.addLast(clientMessageHandler);
    }
}
