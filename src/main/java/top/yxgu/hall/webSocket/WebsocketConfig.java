package top.yxgu.hall.webSocket;

import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

@Configuration
public class WebsocketConfig {
	@Value("${websocket.server.ssl}")
	private boolean isSsl;
	
	@Bean
	public SslContext sslContext() {
		SslContext sslCtx = null;
    	if (isSsl) {
    		try (FileInputStream cerfi = new FileInputStream("E:\\soft\\java\\eclipsests\\workspace\\netty.jks")) {
	        	KeyManagerFactory keyManagerFactory = null;
	            KeyStore keyStore = KeyStore.getInstance("JKS");
	            keyStore.load(cerfi, "blch123".toCharArray());
	            keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
	            keyManagerFactory.init(keyStore,"blch123".toCharArray());
	            sslCtx = SslContextBuilder.forServer(keyManagerFactory).build();
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	} 
    	return sslCtx;
	}
}
