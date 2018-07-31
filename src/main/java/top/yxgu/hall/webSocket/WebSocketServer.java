package top.yxgu.hall.webSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

@Service
public class WebSocketServer {
	
	@Value("${websocket.server.ssl}")
	private boolean isSsl;
	@Value("${websocket.server.path:/ws}")
    private String websocketPath;
   
    @Value("${websocket.server.port:8000}")
    private int port;
    
    @Autowired
    private WebSocketServerInitializer webSocketServerInitializer;
    
    private static final Logger log = LoggerFactory.getLogger(WebSocketServer.class);
    private EventLoopGroup bossGroup;
    private EventLoopGroup workGroup;
	
	public void run() {
		bossGroup = new NioEventLoopGroup(2);
		workGroup = new NioEventLoopGroup(4);
		
		try {
			ServerBootstrap server = new ServerBootstrap();
			server.group(bossGroup, workGroup).channel(NioServerSocketChannel.class)
//					.handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(webSocketServerInitializer);

			Channel ch = server.bind(port).sync().channel();
			log.info("WebSocket Server started, Connect by " + (isSsl? "wss" : "ws") + "://hostname:" + port + this.websocketPath);
			ch.closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			bossGroup.shutdownGracefully();
			workGroup.shutdownGracefully();
		}
	}
	
	public void stop(String reason) {
		log.info("Stop WebSocket Server["+port+"]："+reason);
		bossGroup.shutdownGracefully();
		workGroup.shutdownGracefully();
	}
}
