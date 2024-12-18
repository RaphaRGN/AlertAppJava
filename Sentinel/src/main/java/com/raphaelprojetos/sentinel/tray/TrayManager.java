package com.raphaelprojetos.sentinel.tray;

import jakarta.annotation.PostConstruct;

import javax.swing.*;
import java.awt.*;

public class TrayManager {

    private final JFrameManager jFrameManager = new JFrameManager();

    public void initTray(){

        if (!SystemTray.isSupported()) {
            System.out.println("Funcionalidade de ícone de bandeja não suportada");
            return;
        }

        SystemTray tray = SystemTray.getSystemTray();
        PopupMenu popupMenu = new PopupMenu();

        ImageIcon icon = new ImageIcon(getClass().getResource("/images/SimboloBrigada.png"));
        Image image = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);

        TrayIcon trayIcon = new TrayIcon(image, "Sentinel", popupMenu);

        MenuItem abrirOSentinel = new MenuItem("Abrir o Sentinel");
        popupMenu.add(abrirOSentinel);

        abrirOSentinel.addActionListener(e -> jFrameManager.showInterface());

        try {
            tray.add(trayIcon);
            System.out.println("Ícone adicionado à bandeja");
        } catch (AWTException e) {
            System.err.println("Erro ao adicionar ícone à bandeja");
        }
    }
    @PostConstruct
    public void showInterface() {
        SwingUtilities.invokeLater(this::showInterface);
    }

}

