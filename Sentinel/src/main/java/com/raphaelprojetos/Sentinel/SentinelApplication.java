package com.raphaelprojetos.Sentinel;

import com.raphaelprojetos.Sentinel.socket.SocketClient;
import com.raphaelprojetos.Sentinel.socket.SocketServer;
import com.raphaelprojetos.Sentinel.tray.TrayManager;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SentinelApplication {
	public static void main(String[] args) {

		TrayManager trayManager = new TrayManager();
		SocketServer serverHandler = new SocketServer();

		// Inicia o servidor em uma nova thread
		new Thread(serverHandler::startServer).start();

		// Aguarda um momento para garantir que o servidor já está escutando
		try {
			Thread.sleep(1000); // 1 segundo
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		SocketClient manager = new SocketClient();
		manager.connect();

	}
}



