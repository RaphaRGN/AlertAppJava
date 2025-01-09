    package com.raphaelprojetos.sentinel.tray;

    import com.fasterxml.jackson.databind.ObjectMapper;
    import com.formdev.flatlaf.FlatLightLaf;
    import com.raphaelprojetos.sentinel.dao.AlertaDAO;
    import com.raphaelprojetos.sentinel.dao.UsuarioDAO;
    import com.raphaelprojetos.sentinel.dto.AlertaDTO;
    import com.raphaelprojetos.sentinel.dto.UsuarioDTO;
    import com.raphaelprojetos.sentinel.rabbitmq.AlertaConsumer;
    import com.raphaelprojetos.sentinel.rabbitmq.RabbitMQClient;
    import com.raphaelprojetos.sentinel.report.ExcelReportGenerator;
    import com.raphaelprojetos.sentinel.weather.WeatherManager;
    import org.jdesktop.swingx.JXButton;
    import org.jdesktop.swingx.JXTable;
    import org.jdesktop.swingx.JXTextField;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.context.annotation.Lazy;
    import org.springframework.stereotype.Component;

    import javax.swing.*;
    import javax.swing.table.DefaultTableModel;
    import java.awt.*;
    import java.awt.event.*;
    import java.time.LocalDateTime;
    import java.util.List;

    @Component
    public class SwingManager extends JFrame implements AlertaConsumer.ConsumerCallback {

        @Lazy
        private WeatherManager weatherManager = new WeatherManager();

        private JPanel cardPanel;
        private AlertaDTO alerta;
        private CardLayout cardLayout;
        private UsuarioDTO usuarioLogado;
        private JXTable tabelaAlertas;
        private JXTable tabelaUsuarios;
        private AlertaConsumer alertaConsumer;
        private JFrame popupFrame;
        private ExcelReportGenerator excelGenerator = new ExcelReportGenerator();
        private Timer tempoRestartDashboard;

        //Método principal
        public void initApplication() {
            JFrame telaPrincipal = new JFrame("Sentinel");
            telaPrincipal.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            telaPrincipal.setSize(1000, 800);
            telaPrincipal.setLocationRelativeTo(null);

            cardLayout = new CardLayout();
            cardPanel = new JPanel(cardLayout);
            telaPrincipal.add(cardPanel);

            JPanel mainPanel = createMainPanel(); //Main
            JPanel loginPanel = createLoginPanel(); // Tela de login
            JPanel configPanel = createConfigPanel();
            JPanel activeUsersPanel = createactiveUsersPanel(); //Tabela usuários
            JPanel newUserPanel = createnewUserPanel(); // Criar novo usuário
            JPanel reportsPanel = createReportsPanel(); // Excel, PDF etc.


            cardPanel.add(mainPanel, "Main");
            cardPanel.add(loginPanel, "Login");
            cardPanel.add(configPanel, "Config");
            cardPanel.add(activeUsersPanel, "activeUsers");
            cardPanel.add(reportsPanel, "Reports");
            cardPanel.add(newUserPanel, "newUser");


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

        public static AlertaDTO desserializarAlerta(String mensagemJson) throws Exception {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(mensagemJson, AlertaDTO.class);
        }

        public void showInterface() {
            SwingUtilities.invokeLater(this::initApplication);
            try {
                UIManager.setLookAndFeel(new FlatLightLaf()); //Carrega o Look and feel
                alertaConsumer = new AlertaConsumer(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onMessageReceived(String mensagemJson) {
            SwingUtilities.invokeLater(() -> {
                try {
                    AlertaDTO alerta = desserializarAlerta(mensagemJson);

                    mostrarPopup(alerta);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                atualizarTabelaAlertas();
            });
        }

        private void mostrarPopup(AlertaDTO alerta) {
            if (popupFrame != null && popupFrame.isVisible()) {
                popupFrame.dispose();
            }
            JFrame bloqueioFrame = new JFrame();
            bloqueioFrame.setUndecorated(true);
            bloqueioFrame.setAlwaysOnTop(true);
            bloqueioFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);


            bloqueioFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            bloqueioFrame.setBackground(new Color(0, 0, 0, 200)); // Fundo semitransparente

            JLabel mensagemLabel = new JLabel("<html>" +
                    "<h1 style='text-align:center;'>" + alerta.getTitulo() + "</h1>" +
                    "<p><strong style= 'font-size: 16rem;'>Código:</strong> " + alerta.getCodigo() + "</p>" +
                    "<p><strong>Descrição:</strong> " + alerta.getDescricao() + "</p>" +
                    "<p><strong>Data/Hora:</strong> " + alerta.getTempoFormatado() + "</p>" +
                    "</html>");

            mensagemLabel.setHorizontalAlignment(SwingConstants.CENTER);
            bloqueioFrame.add(mensagemLabel);

            bloqueioFrame.setVisible(true);

            Timer cooldownTimer = new Timer(15000, e -> {
                bloqueioFrame.dispose(); // Fecha a tela de bloqueio
            });
            cooldownTimer.setRepeats(false); // Garante execução única
            cooldownTimer.start();
        }

        SwingWorker<Void, Void> consumerWorker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                alertaConsumer = new AlertaConsumer(SwingManager.this);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Erro ao iniciar o consumidor: " + e.getMessage());
                }
                consumerWorker.execute();
            }
        };


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
                        atualizarTabelaAlertas();
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

            JXButton botaoReports = new JXButton("Gerar relatórios");
            botaoReports.setBounds(440, 10, 150, 30);
            botaoReports.addActionListener(e -> cardLayout.show(cardPanel, "Reports"));
            panelMain.add(botaoReports);


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
            botaoEnviarOcorrencia.setToolTipText("Clique aqui para enviar o alerta");
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
                    JOptionPane.showMessageDialog(null, "Bem-vindo, " + usuario.getNome() + " !");
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

            JXButton botaoUsuarios = new JXButton("Ver usuários");
            botaoUsuarios.setBounds(150, 460, 200, 30);
            panelConfig.add(botaoUsuarios);
            botaoUsuarios.addActionListener(e -> {

                cardLayout.show(cardPanel, "activeUsers");

            });

            JCheckBox checkBoxDashborad = new JCheckBox("Modo Dashboard");
            checkBoxDashborad.setBounds(150, 150, 200, 30);
            panelConfig.add(checkBoxDashborad);
            boolean ativarModoDashboard = checkBoxDashborad.isSelected();
            checkBoxDashborad.addActionListener(e->{

                if (checkBoxDashborad.isSelected()) {
                    JOptionPane.showMessageDialog(null, "Modo Dashboard ativo ! " +
                            "Agora o painel irá atualizar de 20 em 20 minutos, ideal para colocar na TV !");
                    System.out.println(tempoRestartDashboard);
                    tempoRestartDashboard = new Timer(20000, event -> {
                        atualizarTabelaAlertas();
                    });
                    tempoRestartDashboard.start();

                } else {
                    if (tempoRestartDashboard != null && tempoRestartDashboard.isRunning() && !checkBoxDashborad.isSelected()) {

                        tempoRestartDashboard.stop();
                        tempoRestartDashboard = null;
                        JOptionPane.showMessageDialog(null, "Modo Dashboard desativado !");

                    }
                }
            });

            String weather = weatherManager.parseWeather(weatherManager.getactualWeather("Blumenau"));
            JLabel weatherLabel = new JLabel("<html>Clima atual:<br>" + weather + "</html>");
            weatherLabel.setBounds(50, 50, 900, 400);
            panelConfig.add(weatherLabel);


            return panelConfig;
        }

        private JPanel createactiveUsersPanel() {
            JPanel panelUsuariosAtivos = new JPanel(null);

            tabelaUsuarios = new JXTable(new DefaultTableModel(new Object[]{"ID", "Nome", "Administrador"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; //Não permite editar a tabela
                }
            });

            JScrollPane scrollPane = new JScrollPane(tabelaUsuarios);
            scrollPane.setBounds(50, 50, 900, 400);
            panelUsuariosAtivos.add(scrollPane);

            JXButton botaoVoltarMain = new JXButton("Voltar para a tela principal");
            botaoVoltarMain.setBounds(150, 460, 200, 30);
            botaoVoltarMain.addActionListener(e -> cardLayout.show(cardPanel, "Main"));
            panelUsuariosAtivos.add(botaoVoltarMain);

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
                cardLayout.show(cardPanel, "newUser");

            });
            JMenuItem item2EditarUser = new JMenuItem("Editar usuário");
            popupMenuConfig.add(item2EditarUser);

            item2EditarUser.addActionListener(e -> {
               int linhaSelecionada = tabelaUsuarios.getSelectedRow();

                if(tabelaUsuarios.getSelectedRow() == -1){

                    return;
                }
                Long idUsuario = (Long) tabelaUsuarios.getValueAt(linhaSelecionada, 0);

                JPanel editUserPanel = createEditUserPanel(idUsuario);
                cardPanel.add(editUserPanel, "Edit");
                cardLayout.show(cardPanel, "Edit");

            });

            JMenuItem item3EditarUser = new JMenuItem("Deletar usuário");
            popupMenuConfig.add(item3EditarUser);

            item3EditarUser.addActionListener( e -> {
                UsuarioDAO usuarioDao = new UsuarioDAO();
                int linhaSelecionada = tabelaUsuarios.getSelectedRow();

                if(tabelaUsuarios.getSelectedRow() == -1){

                    return;
                }
                Long idUsuario = (Long) tabelaUsuarios.getValueAt(linhaSelecionada, 0);

               int confirmacao = JOptionPane.showOptionDialog(null, "Deseja deletar o usuário ?", "Confirmação",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"Sim", "Não"}, "Não");

                    if (confirmacao == JOptionPane.YES_NO_OPTION){
                        usuarioDao.deletarUsuarioPorId(idUsuario);
                        JOptionPane.showMessageDialog(null, "Usuário deletado com sucesso !");
                    }
            });

            return panelUsuariosAtivos;
        }

        private JPanel createnewUserPanel() {
            JPanel panelCriacaoUsuario = new JPanel(null);
            panelCriacaoUsuario.setBounds(200, 200, 400, 300);

            JXTextField campoNovoUsuario = new JXTextField();
            campoNovoUsuario.setPrompt("Digite o nome do novo usuário...");
            campoNovoUsuario.setBounds(150, 50, 200, 30);
            panelCriacaoUsuario.add(campoNovoUsuario);

            JPasswordField campoSenhaUsuario = new JPasswordField();
            campoSenhaUsuario.setBounds(150, 100, 200, 30);
            panelCriacaoUsuario.add(campoSenhaUsuario);

            JCheckBox checkboxAdmin = new JCheckBox("Usuário administrador");
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
                    cardLayout.show(cardPanel, "activeUsers");

                } catch (Exception exception) {
                    JOptionPane.showMessageDialog(null, "Erro ao criar usuário !" + exception.getMessage());

                }

            });
            panelCriacaoUsuario.add(botaoCriarUsuario);

            JButton botaoVoltar = new JButton("Voltar");
            botaoVoltar.setBounds(150, 250, 150, 30);
            botaoVoltar.addActionListener(e -> cardLayout.show(cardPanel, "activeUsers"));
            panelCriacaoUsuario.add(botaoVoltar);

            return panelCriacaoUsuario;
        }

        private JPanel createEditUserPanel(Long idUsuario) {
           JPanel panelEditarUserAtual = new JPanel(null);
            panelEditarUserAtual.setBounds(200, 200, 400, 300);

            UsuarioDAO usuarioDAO = new UsuarioDAO();
            int linhaSelecionada = tabelaUsuarios.getSelectedRow();

            if (linhaSelecionada == -1){

               return panelEditarUserAtual;
            }

            UsuarioDTO usuarioSelecionado = usuarioDAO.buscarUsuarioPorId(idUsuario);

                String nomeAtualUsuario = usuarioSelecionado.getNome();
                JXTextField campoUsuarioAtual = new JXTextField();
                campoUsuarioAtual.setText(nomeAtualUsuario);
                campoUsuarioAtual.setBounds(150, 50, 200, 30);
                panelEditarUserAtual.add(campoUsuarioAtual);

                String senhaAtual = usuarioSelecionado.getSenha();
                JPasswordField campoSenhaUsuarioAtual = new JPasswordField();
                campoSenhaUsuarioAtual.setBounds(150, 100, 200, 30);
                panelEditarUserAtual.add(campoSenhaUsuarioAtual);

                boolean userEAdmin = usuarioSelecionado.isAdmin();
                JCheckBox checkboxAdmin = new JCheckBox("Usuário administrador");
                checkboxAdmin.setBounds(150, 150, 200, 30);
                panelEditarUserAtual.add(checkboxAdmin);

                checkboxAdmin.setSelected(userEAdmin);


                JXButton botaoIrParaMain = new JXButton("Voltar");
                botaoIrParaMain.setBounds(310, 200, 150, 30);
                panelEditarUserAtual.add(botaoIrParaMain);
                botaoIrParaMain.addActionListener(e->{
                    cardLayout.show(cardPanel, "activeUsers");

                        });

                JXButton botaoSalvarUsuario = new JXButton("Salvar Usuário");
                botaoSalvarUsuario.setBounds(150, 200, 150, 30);
                panelEditarUserAtual.add(botaoSalvarUsuario);
                botaoSalvarUsuario.addActionListener(e -> {

                    String nome = campoUsuarioAtual.getText();
                    String senha = new String(campoSenhaUsuarioAtual.getPassword());
                    boolean isAdmin = checkboxAdmin.isSelected();

                    if (nome.trim().isEmpty()){
                        JOptionPane.showMessageDialog(null, "O campo 'nome' não pode ser vazios !");

                    }

                    if (senha.isEmpty()) {
                        senha = senhaAtual;

                    }
                    else {
                        usuarioSelecionado.setSenha(senha);

                    }

                        usuarioSelecionado.setNome(nome);
                        usuarioSelecionado.setSenha(senha);
                        usuarioSelecionado.setAdmin(isAdmin);

                        usuarioDAO.atualizarUsuario(usuarioSelecionado);


                        JOptionPane.showMessageDialog(null, "Usuário salvo com sucesso!");

                });
           return panelEditarUserAtual;
        }
        private JPanel createReportsPanel(){
            JPanel panelReports = new JPanel(null);

            JXButton botaoGerarPDF = new JXButton("Gerar PDF");
            botaoGerarPDF.setBounds(250, 200, 150, 30);
            panelReports.add(botaoGerarPDF);

            botaoGerarPDF.addActionListener(e ->{


            });

            JXButton botaoGerarExcel = new JXButton("Gerar Excel");
            botaoGerarExcel.setBounds(450, 200, 150, 30);
            panelReports.add(botaoGerarExcel);

            botaoGerarExcel.addActionListener(e->{

                excelGenerator.gerarRelatorioExcel(10);

            });

            JXButton botaoVoltarMain = new JXButton("Voltar para a tela principal");
            botaoVoltarMain.setBounds(325, 250, 200, 30);
            panelReports.add(botaoVoltarMain);

            botaoVoltarMain.addActionListener(e -> {

                cardLayout.show(cardPanel, "Main");

            });

            return panelReports;
        }

       //MÉTODOS UNIVERSAIS

        private void atualizarTabelaAlertas() {
            DefaultTableModel model = (DefaultTableModel) tabelaAlertas.getModel();

            if (usuarioLogado == null || !usuarioLogado.isAdmin()) {
               model.setRowCount(0);
                return;
            }

            try {
                AlertaDAO alertaDAO = new AlertaDAO();
                List<AlertaDTO> alertas = alertaDAO.buscarUltimosAlertas(10);

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
            DefaultTableModel model = (DefaultTableModel) tabelaUsuarios.getModel();

            if (usuarioLogado == null || !usuarioLogado.isAdmin()) {
                model.setRowCount(0);
                return;
            }
            try{
                UsuarioDAO usuarios = new UsuarioDAO();
                List<UsuarioDTO> listaUsuarios = usuarios.buscarTodosUsuarios();

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