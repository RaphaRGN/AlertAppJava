package com.raphaelprojetos.sentinel.tray;

import com.raphaelprojetos.sentinel.dao.AlertaDAO;
import com.raphaelprojetos.sentinel.dao.UsuarioDAO;
import com.raphaelprojetos.sentinel.dto.AlertaDTO;
import com.raphaelprojetos.sentinel.dto.UsuarioDTO;
import com.raphaelprojetos.sentinel.rabbitmq.RabbitMQClient;
import org.jdesktop.swingx.JXTextField;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class JFrameManager extends JFrame {

    private JPanel cardPanel;
    private CardLayout cardLayout;
    private UsuarioDTO usuarioLogado;
    private JTable tabelaAlertas;

    public void showInterface() {
        SwingUtilities.invokeLater(this::initApplication);
    }

    public void initApplication() {
        JFrame telaPrincipal = new JFrame("Sentinel");
        telaPrincipal.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        telaPrincipal.setSize(1000, 800);
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

        JButton botaoLogout = new JButton("Logout");
        botaoLogout.setBounds(800, 10, 150, 30);
        botaoLogout.addActionListener(e -> {
           if(usuarioLogado == null) {
               cardLayout.show(cardPanel, "Login");
           }
        });
        panelMain.add(botaoLogout);

        // Tabela de alertas recentes
        tabelaAlertas = new JTable(new DefaultTableModel(new Object[]{"Código", "Título", "Descrição", "Tempo"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Desativa a edição
            }
        });
        JScrollPane scrollPane = new JScrollPane(tabelaAlertas);
        scrollPane.setBounds(50, 50, 900, 400);
        panelMain.add(scrollPane);


        // Campos para novo alerta
        JXTextField campoTituloOcorrencia = new JXTextField();
        campoTituloOcorrencia.setBounds(50, 500, 200, 30);
        campoTituloOcorrencia.setPrompt("Digite o título da ocorrência...");
        panelMain.add(campoTituloOcorrencia);

        JXTextField campoCodigoOcorrencia = new JXTextField();
        campoCodigoOcorrencia.setBounds(300, 500, 100, 30);
        campoCodigoOcorrencia.setPrompt("Selecione o código da ocorrência...");
        campoCodigoOcorrencia.setEditable(false);
        panelMain.add(campoCodigoOcorrencia);

        String[] opcoesCodigo = {"Amarelo", "Vermelho", "Verde", "Rosa"};
        JComboBox<String> seletor = new JComboBox<>(opcoesCodigo);
        seletor.setBounds(300, 470, 100, 25);
        seletor.addActionListener(e -> campoCodigoOcorrencia.setText((String) seletor.getSelectedItem()));
        panelMain.add(seletor);

        JXTextField campoDescricaoOcorrencia = new JXTextField();
        campoDescricaoOcorrencia.setBounds(450, 500, 200, 30);
        campoDescricaoOcorrencia.setPrompt("Digite uma descrição...");
        panelMain.add(campoDescricaoOcorrencia);

        JButton botaoEnviarOcorrencia = new JButton("Enviar alerta");
        botaoEnviarOcorrencia.setBounds(750, 500, 150, 30);
        botaoEnviarOcorrencia.addActionListener(e -> {
            if (usuarioLogado == null || !usuarioLogado.isAdmin()) {
                JOptionPane.showMessageDialog(null, "Acesso negado. Apenas administradores podem enviar alertas.");
                return;
            }

            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    try {
                        String titulo = campoTituloOcorrencia.getText();
                        String codigo = campoCodigoOcorrencia.getText();
                        String descricao = campoDescricaoOcorrencia.getText();

                        AlertaDTO alerta = new AlertaDTO(codigo, titulo, LocalDateTime.now(), descricao);

                        // Salvar no banco e enviar via RabbitMQ
                        AlertaDAO alertaDAO = new AlertaDAO();
                        alertaDAO.salvarAlerta(alerta);

                        RabbitMQClient rabbitClient = new RabbitMQClient();
                        rabbitClient.enviarALerta(alerta.toJson());

                        JOptionPane.showMessageDialog(null, "Alerta enviado com sucesso!");
                        atualizarTabelaAlertas();

                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Erro ao enviar o alerta: " + ex.getMessage());
                    }
                    return null;
                }
            };
            worker.execute();
        });
        panelMain.add(botaoEnviarOcorrencia);

        atualizarTabelaAlertas();
        return panelMain;
    }

    private JPanel createLoginPanel() {
        JPanel panelLogin = new JPanel(null);

        JXTextField campoUsuario = new JXTextField();
        campoUsuario.setBounds(150, 100, 200, 30);
        campoUsuario.setPrompt("Digite seu usuário...");
        panelLogin.add(campoUsuario);

        JPasswordField campoSenha = new JPasswordField();
        campoSenha.setBounds(150, 150, 200, 30);
        panelLogin.add(campoSenha);

        JButton botaoFazerLogin = new JButton("Fazer login");
        botaoFazerLogin.setBounds(150, 200, 200, 30);
        botaoFazerLogin.addActionListener(e -> {
            String nome = campoUsuario.getText();
            String senha = new String(campoSenha.getPassword());

            UsuarioDAO usuarioDAO = new UsuarioDAO();
            UsuarioDTO usuario = usuarioDAO.autenticarUsuario(nome, senha);

            if (usuario != null) {
                usuarioLogado = usuario;
                JOptionPane.showMessageDialog(null, "Login bem-sucedido! Bem-vindo, " + usuario.getNome());
                cardLayout.show(cardPanel, "Main");
            } else {
                JOptionPane.showMessageDialog(null, "Usuário ou senha inválidos.");
            }
        });
        panelLogin.add(botaoFazerLogin);

        return panelLogin;
    }

    private void atualizarTabelaAlertas() {
        try {
            AlertaDAO alertaDAO = new AlertaDAO();
            List<AlertaDTO> alertas = alertaDAO.buscarUltimosAlertas(10);

            DefaultTableModel model = (DefaultTableModel) tabelaAlertas.getModel();
            model.setRowCount(0); // Limpar tabela

            for (AlertaDTO alerta : alertas) {
                model.addRow(new Object[]{
                        alerta.getCodigo(),
                        alerta.getTitulo(),
                        alerta.getDescricao(),
                        alerta.getTempoFormatado()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao atualizar alertas: " + e.getMessage());
        }
    }
}
