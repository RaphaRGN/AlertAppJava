    package com.raphaelprojetos.sentinel.tray;

    import com.formdev.flatlaf.FlatLightLaf;
    import com.raphaelprojetos.sentinel.dao.AlertaDAO;
    import com.raphaelprojetos.sentinel.dao.UsuarioDAO;
    import com.raphaelprojetos.sentinel.dto.AlertaDTO;
    import com.raphaelprojetos.sentinel.dto.UsuarioDTO;
    import com.raphaelprojetos.sentinel.rabbitmq.RabbitMQClient;
    import org.jdesktop.swingx.JXButton;
    import org.jdesktop.swingx.JXTable;
    import org.jdesktop.swingx.JXTextField;
    import org.springframework.stereotype.Component;

    import javax.swing.*;
    import javax.swing.table.DefaultTableModel;
    import java.awt.*;
    import java.awt.event.*;
    import java.time.LocalDateTime;
    import java.util.List;

    @Component
    public class SwingManager extends JFrame {

        private JPanel cardPanel;
        private CardLayout cardLayout;
        private UsuarioDTO usuarioLogado;
        private JXTable tabelaAlertas;
        private JXTable tabelaUsuarios;

        public void showInterface() {
            SwingUtilities.invokeLater(this::initApplication);
            try {
                UIManager.setLookAndFeel(new FlatLightLaf()); //Carrega o Look and feel
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //Método principal
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
            JPanel configPanel = createConfigPanel();
            JPanel userPanel = createUserPanel();
            JPanel ReportsPanel = createReportsPanel();

            cardPanel.add(mainPanel, "Main");
            cardPanel.add(loginPanel, "Login");
            cardPanel.add(configPanel, "Config");
            cardPanel.add(ReportsPanel, "Reports");
            cardPanel.add(userPanel, "User");


            telaPrincipal.addWindowListener(new WindowAdapter() {
                @Override
                public void windowActivated(WindowEvent e) {
                    atualizarNomeBotao((JXButton) mainPanel.getComponent(0)); // Atualiza o botão de login
                    atualizarTabelaAlertas(); // Atualiza a tabela de alertas
                    adicionarTabelaUsuarios();
                }
            });

            telaPrincipal.setVisible(true);
        }

        private JPanel createMainPanel() {
            JPanel panelMain = new JPanel(null);

            //Botão para fazer login
            JXButton botaoLogin = new JXButton();
            botaoLogin.setBounds(800, 10, 150, 30);
            atualizarNomeBotao(botaoLogin);
            botaoLogin.addActionListener(e -> {
                if (usuarioLogado == null) {
                    cardLayout.show(cardPanel, "Login");
                } else {
                    int confirmacao = JOptionPane.showOptionDialog(null, "Deseja sair do usuário ?", "Confirmação",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"Sim", "Não"}, "Não");

                    if (confirmacao == JOptionPane.YES_OPTION) {
                        usuarioLogado = null;
                        JOptionPane.showMessageDialog(null, "Você foi desconectado !");
                        atualizarNomeBotao(botaoLogin);
                        cardLayout.show(cardPanel, "Main");
                    }
                }
            });
            panelMain.add(botaoLogin);

            // Tabela para puxar alertas recentes
            tabelaAlertas = new JXTable(new DefaultTableModel(new Object[]{"Código", "Título", "Descrição", "Tempo"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; //Não permite editar a tabela
                }
            });

            //Botão de configuração
            JXButton botaoConfiguracao = new JXButton("Configurações");
            botaoConfiguracao.setBounds(620, 10, 150, 30);
            botaoConfiguracao.addActionListener(e -> cardLayout.show(cardPanel, "Config"));
            panelMain.add(botaoConfiguracao);


            JScrollPane scrollPane = new JScrollPane(tabelaAlertas);
            scrollPane.setBounds(50, 50, 900, 400);
            panelMain.add(scrollPane);


            // Campos para novo alerta
            JXTextField campoTituloOcorrencia = new JXTextField();
            campoTituloOcorrencia.setBounds(50, 500, 200, 30);
            campoTituloOcorrencia.setPrompt("Digite o título da ocorrência...");
            panelMain.add(campoTituloOcorrencia);

            //Campo para seleção de ocorrências
            JXTextField campoCodigoOcorrencia = new JXTextField();
            campoCodigoOcorrencia.setBounds(300, 500, 100, 30);
            campoCodigoOcorrencia.setPrompt("Selecione o código da ocorrência...");
            campoCodigoOcorrencia.setEditable(false);
            panelMain.add(campoCodigoOcorrencia);

            //Seletor de códigos
            String[] opcoesCodigo = {"Amarelo", "Vermelho", "Verde", "Rosa"};
            JComboBox<String> seletor = new JComboBox<>(opcoesCodigo);
            seletor.setBounds(300, 470, 100, 25);
            seletor.addActionListener(e -> campoCodigoOcorrencia.setText((String) seletor.getSelectedItem()));
            panelMain.add(seletor);

            JXTextField campoDescricaoOcorrencia = new JXTextField();
            campoDescricaoOcorrencia.setBounds(450, 500, 200, 30);
            campoDescricaoOcorrencia.setPrompt("Digite uma descrição...");
            panelMain.add(campoDescricaoOcorrencia);

            JPopupMenu popupMenuMain = new JPopupMenu();
            JMenuItem item1Main = new JMenuItem("Deletar todos alertas");
            popupMenuMain.add(item1Main);
            item1Main.addActionListener(e -> {
                AlertaDAO alertaDAO = new AlertaDAO();
                alertaDAO.deletarTodosAlertas();

            });


            tabelaAlertas.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    showPopup(e);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    showPopup(e);
                }

                private void showPopup(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        popupMenuMain.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            });

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

                            if (campoCodigoOcorrencia.getText().trim().isEmpty() || campoTituloOcorrencia.getText().trim().isEmpty()) {
                                JOptionPane.showMessageDialog(null, "Preencha os campos obrigatórios 'código' e 'título'");
                                return null;

                            }
                            AlertaDTO alerta = new AlertaDTO(codigo, titulo, LocalDateTime.now(), descricao);


                            AlertaDAO alertaDAO = new AlertaDAO();
                            alertaDAO.salvarAlerta(alerta);

                            RabbitMQClient rabbitClient = new RabbitMQClient();
                            rabbitClient.enviarALerta(alerta.toJson());

                            atualizarTabelaAlertas();
                            trazerTelaNoFocus((JFrame) SwingUtilities.getWindowAncestor(cardPanel), "Config");

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
            botaoFazerLogin.setBounds(150, 210, 200, 30);
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

            JXButton botaoVoltarParaAlertas = new JXButton("Voltar");
            botaoVoltarParaAlertas.setBounds(175, 260, 150, 30);
            panelLogin.add(botaoVoltarParaAlertas);
            botaoVoltarParaAlertas.addActionListener(e ->
                    cardLayout.show(cardPanel, "Main")
            );

            return panelLogin;
        }

        private JPanel createConfigPanel() {
            JPanel panelConfig = new JPanel(null);

            tabelaUsuarios = new JXTable(new DefaultTableModel(new Object[]{"ID", "Nome", "Administrador"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; //Não permite editar a tabela
                }
            });

            JScrollPane scrollPane = new JScrollPane(tabelaUsuarios);
            scrollPane.setBounds(50, 50, 900, 400);
            panelConfig.add(scrollPane);


            JXButton botaoVoltarMain = new JXButton("Voltar para a tela principal");
            botaoVoltarMain.setBounds(150, 460, 200, 30);
            botaoVoltarMain.addActionListener(e -> cardLayout.show(cardPanel, "Main"));
            panelConfig.add(botaoVoltarMain);

            JPopupMenu popupMenuConfig = new JPopupMenu();
            JMenuItem item1Config = new JMenuItem("Criar novo usuário");
            popupMenuConfig.add(item1Config);
            tabelaUsuarios.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    showPopup(e);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    showPopup(e);
                }

                private void showPopup(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        popupMenuConfig.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            });


            item1Config.addActionListener(e -> {
                cardLayout.show(cardPanel, "User");

            });

            return panelConfig;
        }

        private JPanel createUserPanel() {
            JPanel panelCriacaoUsuario = new JPanel(null);
            panelCriacaoUsuario.setBounds(200, 200, 400, 300);

            JXTextField campoNovoUsuario = new JXTextField();
            campoNovoUsuario.setPrompt("Digite o nome do novo usuário...");
            campoNovoUsuario.setBounds(150, 50, 200, 30);
            panelCriacaoUsuario.add(campoNovoUsuario);

            JPasswordField campoSenhaUsuario = new JPasswordField();
            campoSenhaUsuario.setBounds(150, 100, 200, 30);
            panelCriacaoUsuario.add(campoSenhaUsuario);

            JCheckBox checkboxAdmin = new JCheckBox();
            checkboxAdmin.setBounds(150, 150, 200, 30);
            panelCriacaoUsuario.add(checkboxAdmin);

            JXButton botaoCriarUsuario = new JXButton("Criar usuário");
            botaoCriarUsuario.setBounds(150, 200, 150, 30);
            botaoCriarUsuario.addActionListener(e -> {

                String nomeUsuario = campoNovoUsuario.getText();
                String senhaUsuario = new String(campoSenhaUsuario.getPassword());
                boolean novoAdmin = checkboxAdmin.isSelected();

                if (nomeUsuario.trim().isEmpty() || senhaUsuario.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Por favor, preencha os campos !");
                    return;
                }
                try {
                    UsuarioDTO novoUsuario = new UsuarioDTO();
                    novoUsuario.setNome(nomeUsuario);
                    novoUsuario.setSenha(senhaUsuario);
                    novoUsuario.setAdmin(novoAdmin);

                    UsuarioDAO usuarioDAO = new UsuarioDAO();
                    usuarioDAO.salvarUsuario(novoUsuario);

                    JOptionPane.showMessageDialog(null, "Usuário criado com sucesso!");
                    cardLayout.show(cardPanel, "Config");

                } catch (Exception exception) {
                    JOptionPane.showMessageDialog(null, "Erro ao criar usuário !" + exception.getMessage());

                }

            });
            panelCriacaoUsuario.add(botaoCriarUsuario);

            JButton botaoVoltar = new JButton("Voltar");
            botaoVoltar.setBounds(150, 250, 150, 30);
            botaoVoltar.addActionListener(e -> cardLayout.show(cardPanel, "Config"));
            panelCriacaoUsuario.add(botaoVoltar);

            return panelCriacaoUsuario;
        }

        private JPanel createReportsPanel(){
            JPanel panelReports = new JPanel(null);


            return panelReports;
        }

       //METODOS UNIVERSAIS

        private void atualizarTabelaAlertas() {

            if (usuarioLogado == null || !usuarioLogado.isAdmin()) {
                return;
            }

            try {
                AlertaDAO alertaDAO = new AlertaDAO();
                List<AlertaDTO> alertas = alertaDAO.buscarUltimosAlertas(10);

                DefaultTableModel model = (DefaultTableModel) tabelaAlertas.getModel();
                model.setRowCount(0);

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

        private void adicionarTabelaUsuarios(){

            if (usuarioLogado == null || !usuarioLogado.isAdmin()) {
                return;
            }
            try{
                UsuarioDAO usuarios = new UsuarioDAO();
                List<UsuarioDTO> listaUsuarios = usuarios.buscarTodosUsuarios();

                DefaultTableModel model = (DefaultTableModel) tabelaUsuarios.getModel();
                model.setRowCount(0);

                for (UsuarioDTO usuarioDTO : listaUsuarios){

                    model.addRow(new Object[]{
                            usuarioDTO.getId(),
                            usuarioDTO.getNome(),
                            usuarioDTO.isAdmin(),
                    });
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro ao checar usuários: " + e.getMessage());

            }
        }

        private void trazerTelaNoFocus (JFrame telaPrincipal, String painel){

            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(cardPanel);
            if (frame != null) {
                frame.setVisible(true);
                frame.setState(JFrame.NORMAL);
                frame.toFront();
                frame.requestFocus();
            }
            // Garante que o painel Main seja exibido
            cardLayout.show(cardPanel, "Main");

        }

        private void atualizarNomeBotao(JXButton botao){
            if(usuarioLogado == null){
                botao.setText("Login");

            }
            else{
                botao.setText(usuarioLogado.getNome());

            }
        }
    }