package top.yxgu.hall;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import top.yxgu.hall.model.ConfigManager;
import top.yxgu.hall.model.RoomServerManager;
import top.yxgu.hall.model.UserManager;
import top.yxgu.hall.roomScoket.RoomSocketServer;
import top.yxgu.hall.webSocket.WebSocketServer;
import top.yxgu.utils.OSInfo;

@SpringBootApplication
public class HallServerApplication {
	
	private static final Logger logger = LoggerFactory.getLogger(HallServerApplication.class);

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(HallServerApplication.class);
		app.setWebApplicationType(WebApplicationType.SERVLET);
		ApplicationContext ctx = app.run(args);
		
		ConfigManager.loadJsonConfig();
		
		runService(ctx);
		cmd(ctx);
	}
	
	private static void runService(ApplicationContext ctx) {
		RoomSocketServer roomServer = (RoomSocketServer)ctx.getBean(RoomSocketServer.class);
		WebSocketServer webSocketServer = (WebSocketServer)ctx.getBean(WebSocketServer.class);
		if (roomServer == null || webSocketServer == null) {
			logger.error("服务器启动出错");
			return ;
		}
		
		new Thread() {
			@Override
	        public void run() {
				roomServer.run();
			}
		}.start();
		
		new Thread() {
			@Override
	        public void run() {
				// 有RoomServer后再开启
				while (true) {
					if (RoomServerManager.size() > 0) {
						webSocketServer.run();
						return;
					} else {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				
			}
		}.start();
	}
	
	private static void cmd(ApplicationContext ctx) {
		RoomSocketServer roomServer = (RoomSocketServer)ctx.getBean(RoomSocketServer.class);
		WebSocketServer webSocketServer = (WebSocketServer)ctx.getBean(WebSocketServer.class);
		
		///// 接收命令
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
//		System.out.print("yxgu>");
		String inputStr;
		String scmd = null;
		String[] cmds;
		try {
			//初始调用一次，第一次调用会返回无效值
//			OSInfo.getCpuRatio();
			
			while ( (inputStr = br.readLine()) != null ) {
				inputStr = inputStr.trim().toLowerCase();
				cmds = inputStr.split(" ");
				scmd = cmds[0];
				if ("q".equals(scmd) || "quit".equals(scmd)) {
					String reason = cmds.length == 1 ? "" : cmds[1];
					webSocketServer.stop(reason);
					roomServer.stop(reason);
					System.exit(SpringApplication.exit(ctx));
					break;
				} else if ("ka".equals(scmd) || "killall".equals(scmd)) {
//					as.killAllProcess();
				} else if ("inf".equals(scmd)) {
					System.out.println("yxgu>	System Information:");
					System.out.println("                    OS:	"+ OSInfo.getOSArch() + "  " + OSInfo.getOSName() + " V" + OSInfo.getOSVersion());
					System.out.println("          CPU Core Num:	"+ OSInfo.getProcessorNum());
					System.out.println("      Total JVM Memory:	"+ OSInfo.getJVMTotalMemorySize() + "M");
					System.out.println("       Free JVM Memory:	"+ OSInfo.getJVMFreeMemorySize() + "M");
					System.out.println("       Online User Num:	"+ UserManager.size());
					System.out.println("       Room Server NUm:	"+ RoomServerManager.size());
					System.out.print("yxgu>");
				} else if ("?".equals(scmd) || "help".equals(scmd)) {
					System.out.println("yxgu>	command include:\n" +
							"             q/quit:	Exit server.\n" +
							"         ka/killall:	Kill all native process.\n" +
							"                inf:	Show system information.");
					System.out.print("yxgu>");
				} else {
					System.out.println("yxgu>Invalid command. Input \"help\" view command information.");
					System.out.print("yxgu>");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
