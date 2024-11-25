package com.raphaelprojetos.Sentinel;

import com.raphaelprojetos.Sentinel.socket.SocketClient;
import com.raphaelprojetos.Sentinel.socket.SocketServer;
import com.raphaelprojetos.Sentinel.tray.JFrameManager;
import com.raphaelprojetos.Sentinel.tray.TrayManager;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class SentinelApplication {
	public static void main(String[] args) {

		var context = new SpringApplicationBuilder(JFrameManager.class)
				.headless(false)
				.web(WebApplicationType.NONE)  // Impede a inicialização do servidor web
				.run(args);

		context.getBean(JFrameManager.class);

		SocketServer serverHandler = new SocketServer();
		context.getAutowireCapableBeanFactory().autowireBean(serverHandler);


		JFrameManager jFrameManager = context.getBean(JFrameManager.class);
		TrayManager trayManager = new TrayManager();
		trayManager.initTray();

		ExecutorService executor = Executors.newFixedThreadPool(2);
		executor.submit(serverHandler::startServer);
		executor.submit(() -> {
			SocketClient clientManager = context.getBean(SocketClient.class);
			clientManager.connect();
		});

		try {
			Thread.sleep(1000); // 1 segundo

		} catch (InterruptedException e) {
			e.printStackTrace();

		}

		SocketClient manager = new SocketClient();
		manager.connect();
	}
}