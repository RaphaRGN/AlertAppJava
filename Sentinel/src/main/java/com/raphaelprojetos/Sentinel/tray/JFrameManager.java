package com.raphaelprojetos.Sentinel.tray;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class JFrameManager extends JFrame {

    private JFrame telaPrincipal;
    private JPanel cardPanel;
    private CardLayout cardLayout;

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

        abrirOSentinel.addActionListener(e -> showInterface());

        try {
            tray.add(trayIcon);
            System.out.println("Ícone adicionado à bandeja");
        } catch (AWTException e) {
            System.err.println("Erro ao adicionar ícone à bandeja");
        }
    }

    @PostConstruct
    public void showInterface() {
        SwingUtilities.invokeLater(this::initApplication);
    }

    public void initApplication() {
        telaPrincipal = new JFrame("Sentinel");
        telaPrincipal.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        telaPrincipal.setSize(500, 500);
        telaPrincipal.setLocationRelativeTo(null);


        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        telaPrincipal.add(cardPanel);


        JPanel mainPanel = createMainPanel();
        JPanel loginPanel = createLoginPanel();

        cardPanel.add(mainPanel, "Main");
        cardPanel.add(loginPanel, "Login");

        telaPrincipal.setVisible(true);
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(null);

        JButton botaoLogin = new JButton("Ir para Login");
        botaoLogin.setBounds(200, 300, 150, 30);
        botaoLogin.addActionListener(e -> cardLayout.show(cardPanel, "Login"));

        panel.add(botaoLogin);
        return panel;
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(null);

        JLabel labelUsuario = new JLabel("Usuário:");
        labelUsuario.setBounds(80, 100, 70, 25);
        panel.add(labelUsuario);

        JTextField campoUsuario = new JTextField();
        campoUsuario.setBounds(150, 100, 200, 25);
        panel.add(campoUsuario);

        JLabel labelSenha = new JLabel("Senha:");
        labelSenha.setBounds(80, 150, 70, 25);
        panel.add(labelSenha);

        JPasswordField campoSenha = new JPasswordField();
        campoSenha.setBounds(150, 150, 200, 25);
        panel.add(campoSenha);

        JButton botaoVoltar = new JButton("Voltar");
        botaoVoltar.setBounds(100, 300, 100, 30);
        botaoVoltar.addActionListener(e -> cardLayout.show(cardPanel, "Main"));
        panel.add(botaoVoltar);

        JButton botaoLogin = new JButton("Fazer login");
        botaoLogin.setBounds(250, 300, 200, 30);
        botaoLogin.addActionListener(e -> {
            String usuario = campoUsuario.getText();
            String senha = new String(campoSenha.getPassword());
            System.out.println("Usuário: " + usuario + ", Senha: " + senha);
        });
        panel.add(botaoLogin);

        return panel;
    }
}
