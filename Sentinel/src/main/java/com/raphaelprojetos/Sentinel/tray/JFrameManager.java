package com.raphaelprojetos.Sentinel.tray;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.TrayIcon;


@Component
public class JFrameManager extends JFrame {

    public void initTray() {
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

        abrirOSentinel.addActionListener(e -> showLoginUI());

        try {
            tray.add(trayIcon);
            System.out.println("Ícone adicionado à bandeja");
        } catch (AWTException e) {
            System.err.println("Erro ao adicionar ícone à bandeja");
        }
    }

    @PostConstruct
    public void showLoginUI() {
        SwingUtilities.invokeLater(() -> {
            JFrame tela1 = new JFrame("Sentinel");
            tela1.setVisible(true);
            tela1.setResizable(false);
            tela1.setSize(500, 500);
            tela1.setLocationRelativeTo(null);
            tela1.setLayout(null);


            JLabel labelUsuario = new JLabel("Usuário:");
            labelUsuario.setBounds(80, 100, 70, 25);
            tela1.add(labelUsuario);

            JTextField campoUsuario = new JTextField();
            campoUsuario.setBounds(150, 100, 200, 25);
            tela1.add(campoUsuario);


            JLabel labelSenha = new JLabel("Senha:");
            labelSenha.setBounds(80, 150, 70, 25);
            tela1.add(labelSenha);

            JPasswordField campoSenha = new JPasswordField();
            campoSenha.setBounds(150, 150, 200, 25);
            tela1.add(campoSenha);


            JButton botaoLogin = new JButton("Fazer login");
            botaoLogin.setBounds(200, 300, 100, 30);
            tela1.add(botaoLogin);

            botaoLogin.addActionListener(e -> {

                String usuario = campoUsuario.getText();
                String senha = new String(campoSenha.getPassword());
                System.out.println("Usuário: " + usuario + ", Senha: " + senha);
            });
        });
    }
}

