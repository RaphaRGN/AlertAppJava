package com.raphaelprojetos.Sentinel.tray;

import javax.swing.*;
import java.awt.*;
import java.awt.TrayIcon;


// TODO: corrigir a questão da imagem
public class TrayManager extends JFrame {

    public TrayManager() {

        PopupMenu popup = new PopupMenu();

        if (!SystemTray.isSupported()) {

            System.out.println("Funcionalidade de ícone de bandeja não suportada");

        }

        SystemTray tray = SystemTray.getSystemTray();
        Image image = Toolkit.getDefaultToolkit().createImage("SimboloBrigada2.png");

        TrayIcon trayIcon = new TrayIcon(image, "Sentinel");

        trayIcon.setPopupMenu(popup);

        Menu displayMenu = new Menu("Display");
        popup.add(displayMenu);


        MenuItem abrirOSentinel = new MenuItem("Abrir o Sentinel");
        displayMenu.add(abrirOSentinel);


        abrirOSentinel.addActionListener(e -> {

           JFrame tela1 = new JFrame("Sentinel");
           tela1.setVisible(true);
           tela1.setResizable(false);
           tela1.setSize(500, 500);
           tela1.setLocationRelativeTo(null);
           tela1.setLayout(null);


            JPanel painelComImagem = new JPanel() {

                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    ImageIcon imagemFundo = new ImageIcon("SimboloBrigada2.png"); // Caminho da imagem
                    g.drawImage(imagemFundo.getImage(), 0, 0, getWidth(), getHeight(), this);
                }
            };

            painelComImagem.setLayout(null);
            tela1.setContentPane(painelComImagem);

           //Label e campo usuário

           JLabel labelUsuario = new JLabel("Usuário:");
           labelUsuario.setVisible(true);
           labelUsuario.setBounds(80, 100, 70, 25);
           tela1.add(labelUsuario);

            JTextField campoUsuario = new JTextField();
            campoUsuario.setBounds(150, 100, 200, 25);  // Define posição e tamanho
            campoUsuario.setVisible(true);
            tela1.add(campoUsuario);

            //Label e campo para senha

            JLabel labelSenha = new JLabel("Senha:");
            labelSenha.setBounds(80, 150, 70, 25);
            labelSenha.setVisible(true);
            tela1.add(labelSenha);

            JPasswordField campoSenha = new JPasswordField();
            campoSenha.setBounds(150, 150, 200, 25);  // Define posição e tamanho
            campoSenha.setVisible(true);
            tela1.add(campoSenha);

            //Botão de login

           JButton butaoLogin = new JButton("Fazer login");
           butaoLogin.setBounds(200, 300, 100, 30);
           butaoLogin.setSize(100,30);
           tela1.add(butaoLogin);

        });

        try {

            tray.add(trayIcon);
            System.out.println("ícone adicionado a bandeja");

        }

        catch (AWTException e) {
            System.err.println("Erro ao adicionar ícone a bandeja");

        }
    }
}

