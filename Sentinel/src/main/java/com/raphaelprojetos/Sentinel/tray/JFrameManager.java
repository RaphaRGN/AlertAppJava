package com.raphaelprojetos.Sentinel.tray;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

@Component
public class JFrameManager extends JFrame {

    private JPanel cardPanel;
    private CardLayout cardLayout;


    @PostConstruct
    public void showInterface() {
        SwingUtilities.invokeLater(this::initApplication);
    }

    public void initApplication() {
        JFrame telaPrincipal = new JFrame("Sentinel");
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
        JPanel panelMain = new JPanel(null);

        JButton botaoLogin = new JButton("Ir para Login");
        botaoLogin.setBounds(200, 300, 150, 30);
        botaoLogin.addActionListener(e -> cardLayout.show(cardPanel, "Login"));
        panelMain.add(botaoLogin);

        JButton botaoMonitoramento = new JButton ("Configurações");

        return panelMain;
    }

    private JPanel createLoginPanel() {
        JPanel panelLogin = new JPanel(null);

        JLabel labelUsuario = new JLabel("Usuário:");
        labelUsuario.setBounds(80, 100, 70, 25);
        panelLogin.add(labelUsuario);

        JTextField campoUsuario = new JTextField();
        campoUsuario.setBounds(150, 100, 200, 25);
        panelLogin.add(campoUsuario);

        JLabel labelSenha = new JLabel("Senha:");
        labelSenha.setBounds(80, 150, 70, 25);
        panelLogin.add(labelSenha);

        JPasswordField campoSenha = new JPasswordField();
        campoSenha.setBounds(150, 150, 200, 25);
        panelLogin.add(campoSenha);

        JButton botaoVoltar = new JButton("Voltar");
        botaoVoltar.setBounds(100, 300, 100, 30);
        botaoVoltar.addActionListener(e -> cardLayout.show(cardPanel, "Main"));
        panelLogin.add(botaoVoltar);

        JButton botaoLogin = new JButton("Fazer login");
        botaoLogin.setBounds(250, 300, 200, 30);
        botaoLogin.addActionListener(e -> {
            String usuario = campoUsuario.getText();
            String senha = new String(campoSenha.getPassword());
            System.out.println("Usuário: " + usuario + ", Senha: " + senha);
        });
        panelLogin.add(botaoLogin);

        return panelLogin;
    }
}
