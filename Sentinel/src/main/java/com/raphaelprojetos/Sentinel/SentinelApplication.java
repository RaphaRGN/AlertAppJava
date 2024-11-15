package com.raphaelprojetos.Sentinel;

import com.raphaelprojetos.Sentinel.socket.SocketClient;
import com.raphaelprojetos.Sentinel.socket.SocketServer;
import com.raphaelprojetos.Sentinel.tray.TrayManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SentinelApplication {
	public static void main(String[] args) {
		SpringApplication.run(SentinelApplication.class, args);

		TrayManager trayManager = new TrayManager();
		SocketServer serverHandler = new SocketServer();

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



