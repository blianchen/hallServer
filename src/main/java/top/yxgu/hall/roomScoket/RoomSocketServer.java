package top.yxgu.hall.roomScoket;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

@Service
public class RoomSocketServer {
	
	@Value("${roomSocket.server.port}")
	private int port;
	
	@Resource
	private RoomScoketServerInitializer roomScoketServerInitializer;
	
    private static final Logger log = LoggerFactory.getLogger(RoomSocketServer.class);
    private EventLoopGroup bossGroup;
    private EventLoopGroup workGroup;
	
	public RoomSocketServer() {
	}
	
	public void run() {
		bossGroup = new NioEventLoopGroup(1);
		workGroup = new NioEventLoopGroup(2);
		
		try {
			ServerBootstrap server = new ServerBootstrap();
			server.group(bossGroup, workGroup).channel(NioServerSocketChannel.class)
//				  .handler(new LoggingHandler(LogLevel.INFO))
				  .childHandler(roomScoketServerInitializer);
			
			Channel ch = server.bind(port).sync().channel();
			
			log.info("RoomSocket Server start at:"+port);
			ch.closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			bossGroup.shutdownGracefully();
			workGroup.shutdownGracefully();
		}
	}
	
	public void stop(String reason) {
		log.info("Stop RoomSocket Server["+port+"]："+reason);
		bossGroup.shutdownGracefully();
		workGroup.shutdownGracefully();
	}
}
