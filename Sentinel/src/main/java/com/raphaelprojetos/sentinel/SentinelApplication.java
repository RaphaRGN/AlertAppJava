package com.raphaelprojetos.sentinel;

import com.raphaelprojetos.sentinel.tray.JFrameManager;
import com.raphaelprojetos.sentinel.tray.TrayManager;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class SentinelApplication {
	public static void main(String[] args) {
		var context = new SpringApplicationBuilder(SentinelApplication.class)
				.headless(false)
				.web(WebApplicationType.NONE)
				.run(args);

		JFrameManager jFrameManager = context.getBean(JFrameManager.class);
		TrayManager trayManager = new TrayManager();
		trayManager.initTray();

	}
}