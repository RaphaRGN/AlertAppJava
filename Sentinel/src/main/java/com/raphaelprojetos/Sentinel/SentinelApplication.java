package com.raphaelprojetos.Sentinel;

import com.raphaelprojetos.Sentinel.socket.SocketManager;
import com.raphaelprojetos.Sentinel.tray.TrayManager;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SentinelApplication {

	public static void main(String[] args) {

		TrayManager trayManager = new TrayManager();
		SocketManager manager = new SocketManager();
		manager.conect();

	}
}



