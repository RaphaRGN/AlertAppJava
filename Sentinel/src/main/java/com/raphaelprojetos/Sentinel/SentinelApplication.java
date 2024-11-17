package com.raphaelprojetos.Sentinel;

import com.raphaelprojetos.Sentinel.socket.SocketClient;
import com.raphaelprojetos.Sentinel.socket.SocketServer;
import com.raphaelprojetos.Sentinel.tray.JFrameManager;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class SentinelApplication {
	public static void main(String[] args) {

		var context = new SpringApplicationBuilder(JFrameManager.class)
				.headless(false)
				.web(WebApplicationType.NONE)  // Impede a inicialização do servidor web
				.run(args);

		context.getBean(JFrameManager.class);

		SocketServer serverHandler = new SocketServer();

		//Icone bandeja
		JFrameManager jFrameManager = new JFrameManager();
		jFrameManager.initTray();

		new Thread(serverHandler::startServer).start();

		try {
			Thread.sleep(1000); // 1 segundo
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		SocketClient manager = new SocketClient();
		manager.connect();

	}
}



