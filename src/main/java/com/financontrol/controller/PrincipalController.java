package com.financontrol.controller;

import com.financontrol.MainApp;
import com.financontrol.dao.CategoriaDAO;
import com.financontrol.dao.TransacaoDAO;
import com.financontrol.dao.UsuarioDAO;
import com.financontrol.exception.ValidacaoException;
import com.financontrol.model.Categoria;
import com.financontrol.model.Despesa;
import com.financontrol.model.Receita;
import com.financontrol.model.Transacao;
import com.financontrol.model.Usuario;
import com.financontrol.util.ConexaoDB;
import javafx.animation.ScaleTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrincipalController {

    @FXML private Label lblUsuarioLogado;
    @FXML private Label lblSaldo;
    @FXML private Label lblReceitas;
    @FXML private Label lblDespesas;
    @FXML private Label lblDivida;
    @FXML private Label lblDetalheGrafico;
    @FXML private PieChart graficoDespesas;

    @FXML private ComboBox<String> cbTipoTransacao;
    @FXML private ComboBox<Categoria> cbCategoriaTransacao;
    @FXML private TextField txtDescTransacao;
    @FXML private TextField txtValorTransacao;
    @FXML private DatePicker dpDataTransacao;
    @FXML private TableView<Transacao> tabelaTransacoes;
    @FXML private TableColumn<Transacao, String> colTransData;
    @FXML private TableColumn<Transacao, String> colTransDesc;
    @FXML private TableColumn<Transacao, String> colTransCat;
    @FXML private TableColumn<Transacao, String> colTransTipo;
    @FXML private TableColumn<Transacao, String> colTransValor;

    @FXML private TextField txtNomeCategoria;
    @FXML private ComboBox<String> cbTipoCategoria;
    @FXML private TableView<Categoria> tabelaCategorias;
    @FXML private TableColumn<Categoria, String> colCatId;
    @FXML private TableColumn<Categoria, String> colCatNome;
    @FXML private TableColumn<Categoria, String> colCatTipo;

    @FXML private TextField txtNomeUsuario;
    @FXML private TextField txtEmailUsuario;
    @FXML private PasswordField txtNovaSenhaUsuario;
    @FXML private TableView<Usuario> tabelaUsuarios;
    @FXML private TableColumn<Usuario, String> colUsuId;
    @FXML private TableColumn<Usuario, String> colUsuNome;
    @FXML private TableColumn<Usuario, String> colUsuEmail;

    private final TransacaoDAO transacaoDAO = new TransacaoDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private double saldoAtual = 0.0;

    @FXML
    public void initialize() {
        Usuario logado = ConexaoDB.getInstancia().getUsuarioLogado();
        lblUsuarioLogado.setText("Usuário: " + logado.getNome() + " (" + logado.getEmail() + ")");

        cbTipoTransacao.setItems(FXCollections.observableArrayList("RECEITA", "DESPESA"));
        cbTipoTransacao.setValue("DESPESA");
        cbTipoTransacao.setOnAction(e -> atualizarComboCategorias());
        dpDataTransacao.setValue(LocalDate.now());

        cbTipoCategoria.setItems(FXCollections.observableArrayList("RECEITA", "DESPESA"));
        cbTipoCategoria.setValue("DESPESA");

        configurarTabelasECliques();
        txtNomeUsuario.setText(logado.getNome());
        txtEmailUsuario.setText(logado.getEmail());

        atualizarTudo();
    }

    private void animarEscala(Node no, double escalaAlvo) {
        ScaleTransition st = new ScaleTransition(Duration.millis(180), no);
        st.setToX(escalaAlvo);
        st.setToY(escalaAlvo);
        st.play();
    }

    private void configurarTabelasECliques() {
        colTransData.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getData().toString()));
        colTransDesc.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescricao()));
        colTransCat.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategoria().getNome()));
        colTransTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo()));
        colTransValor.setCellValueFactory(c -> new SimpleStringProperty(
                String.format("R$ %.2f", c.getValue().calcularImpactoNoSaldo())
        ));

        tabelaTransacoes.getSelectionModel().selectedItemProperty().addListener((obs, ant, sel) -> {
            if (sel != null) {
                cbTipoTransacao.setValue(sel.getTipo());
                atualizarComboCategorias();
                for (Categoria c : cbCategoriaTransacao.getItems()) {
                    if (c.getId().equals(sel.getCategoria().getId())) {
                        cbCategoriaTransacao.setValue(c);
                        break;
                    }
                }
                txtDescTransacao.setText(sel.getDescricao());
                txtValorTransacao.setText(String.valueOf(sel.getValor()));
                dpDataTransacao.setValue(sel.getData());
            }
        });

        colCatId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        colCatNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
        colCatTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo()));

        tabelaCategorias.getSelectionModel().selectedItemProperty().addListener((obs, ant, sel) -> {
            if (sel != null) {
                txtNomeCategoria.setText(sel.getNome());
                cbTipoCategoria.setValue(sel.getTipo());
            }
        });

        colUsuId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        colUsuNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
        colUsuEmail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));

        tabelaUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, ant, sel) -> {
            if (sel != null) {
                txtNomeUsuario.setText(sel.getNome());
                txtEmailUsuario.setText(sel.getEmail());
                txtNovaSenhaUsuario.clear();
            }
        });
    }

    private void atualizarTudo() {
        try {
            Long usuarioId = ConexaoDB.getInstancia().getUsuarioLogado().getId();
            List<Transacao> transacoes = transacaoDAO.listarPorUsuario(usuarioId);
            tabelaTransacoes.setItems(FXCollections.observableArrayList(transacoes));

            saldoAtual = 0;
            double totalRec = 0;
            double totalDesp = 0;
            Map<String, Double> gastosPorCategoria = new HashMap<>();

            for (Transacao t : transacoes) {
                saldoAtual += t.calcularImpactoNoSaldo();
                if (t instanceof Receita) {
                    totalRec += t.getValor();
                } else if (t instanceof Despesa) {
                    totalDesp += t.getValor();
                    String nomeCat = t.getCategoria().getNome();
                    double acumulado = gastosPorCategoria.containsKey(nomeCat) ? gastosPorCategoria.get(nomeCat) : 0.0;
                    gastosPorCategoria.put(nomeCat, acumulado + t.getValor());
                }
            }

            double dividaProximoMes = saldoAtual < 0 ? Math.abs(saldoAtual) : 0.0;

            lblSaldo.setText(String.format("R$ %.2f", saldoAtual));
            lblReceitas.setText(String.format("R$ %.2f", totalRec));
            lblDespesas.setText(String.format("R$ %.2f", totalDesp));
            if (lblDivida != null) {
                lblDivida.setText(String.format("R$ %.2f", dividaProximoMes));
            }

            ObservableList<PieChart.Data> dadosPizza = FXCollections.observableArrayList();
            for (Map.Entry<String, Double> entry : gastosPorCategoria.entrySet()) {
                dadosPizza.add(new PieChart.Data(entry.getKey(), entry.getValue()));
            }
            graficoDespesas.setData(dadosPizza);

            final double somaDespesas = totalDesp;
            for (PieChart.Data fatia : graficoDespesas.getData()) {
                Node noFatia = fatia.getNode();
                if (noFatia != null) {
                    noFatia.setCursor(Cursor.HAND);
                    double percentual = somaDespesas > 0 ? (fatia.getPieValue() / somaDespesas) * 100.0 : 0.0;
                    String textoDetalhe = String.format("🔍 %s — R$ %.2f (%.1f%%)",
                            fatia.getName(), fatia.getPieValue(), percentual);

                    Tooltip dica = new Tooltip(textoDetalhe);
                    dica.setShowDelay(Duration.millis(60));
                    Tooltip.install(noFatia, dica);

                    noFatia.setOnMouseEntered(e -> {
                        noFatia.toFront();
                        animarEscala(noFatia, 1.12);
                        if (lblDetalheGrafico != null) {
                            lblDetalheGrafico.setText(textoDetalhe);
                        }
                    });
                    noFatia.setOnMouseExited(e -> {
                        animarEscala(noFatia, 1.0);
                        if (lblDetalheGrafico != null) {
                            lblDetalheGrafico.setText("Passe o mouse em uma fatia para ampliar");
                        }
                    });
                }
            }

            List<Categoria> categorias = categoriaDAO.listarPorUsuario(usuarioId);
            tabelaCategorias.setItems(FXCollections.observableArrayList(categorias));
            atualizarComboCategorias();

            tabelaUsuarios.setItems(FXCollections.observableArrayList(usuarioDAO.listarTodos()));
        } catch (Exception e) {
            MainApp.mostrarMensagem("Erro", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void atualizarComboCategorias() {
        try {
            Long usuarioId = ConexaoDB.getInstancia().getUsuarioLogado().getId();
            String tipoEscolhido = cbTipoTransacao.getValue();
            List<Categoria> todas = categoriaDAO.listarPorUsuario(usuarioId);
            List<Categoria> filtradas = new ArrayList<>();

            for (Categoria c : todas) {
                if (c.getTipo().equalsIgnoreCase(tipoEscolhido)) {
                    filtradas.add(c);
                }
            }

            cbCategoriaTransacao.setItems(FXCollections.observableArrayList(filtradas));
            if (!filtradas.isEmpty()) {
                cbCategoriaTransacao.getSelectionModel().selectFirst();
            }
        } catch (Exception ignored) {
        }
    }

    @FXML
    public void handleSalvarTransacao() {
        try {
            if (txtDescTransacao.getText().isBlank() || txtValorTransacao.getText().isBlank()
                    || cbCategoriaTransacao.getValue() == null || dpDataTransacao.getValue() == null) {
                throw new ValidacaoException("Preencha descrição, valor, data e categoria da transação.");
            }
            double valor = Double.parseDouble(txtValorTransacao.getText().replace(",", "."));
            if (valor <= 0) {
                throw new ValidacaoException("O valor deve ser maior que zero.");
            }

            String tipo = cbTipoTransacao.getValue();
            Long usuarioId = ConexaoDB.getInstancia().getUsuarioLogado().getId();

            Transacao nova = Transacao.criar(null, tipo, txtDescTransacao.getText().trim(),
                    valor, dpDataTransacao.getValue(), cbCategoriaTransacao.getValue(), usuarioId);
            transacaoDAO.salvar(nova);

            txtDescTransacao.clear();
            txtValorTransacao.clear();
            atualizarTudo();

            if ("DESPESA".equals(tipo) && saldoAtual < 0) {
                MainApp.mostrarMensagem(
                        "Registrado como Dívida / Próximo Mês",
                        String.format(
                                "Despesa adicionada! Como seus gastos ultrapassaram sua receita, o valor excedente de R$ %.2f ficou registrado como Dívida / Pendente para o próximo mês.",
                                Math.abs(saldoAtual)
                        ),
                        Alert.AlertType.INFORMATION
                );
            }
        } catch (NumberFormatException e) {
            MainApp.mostrarMensagem("Valor Inválido", "Digite um número válido para o valor.", Alert.AlertType.WARNING);
        } catch (ValidacaoException e) {
            MainApp.mostrarMensagem("Validação", e.getMessage(), Alert.AlertType.WARNING);
        } catch (Exception e) {
            MainApp.mostrarMensagem("Erro", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleAtualizarTransacao() {
        Transacao sel = tabelaTransacoes.getSelectionModel().getSelectedItem();
        if (sel == null) {
            MainApp.mostrarMensagem("Atenção", "Clique em uma transação na tabela para editá-la.", Alert.AlertType.WARNING);
            return;
        }
        try {
            if (txtDescTransacao.getText().isBlank() || txtValorTransacao.getText().isBlank()
                    || cbCategoriaTransacao.getValue() == null || dpDataTransacao.getValue() == null) {
                throw new ValidacaoException("Preencha descrição, valor, data e categoria da transação.");
            }
            double valor = Double.parseDouble(txtValorTransacao.getText().replace(",", "."));
            if (valor <= 0) {
                throw new ValidacaoException("O valor deve ser maior que zero.");
            }

            Long usuarioId = ConexaoDB.getInstancia().getUsuarioLogado().getId();
            Transacao editada = Transacao.criar(sel.getId(), cbTipoTransacao.getValue(),
                    txtDescTransacao.getText().trim(), valor, dpDataTransacao.getValue(),
                    cbCategoriaTransacao.getValue(), usuarioId);
            transacaoDAO.atualizar(editada);

            txtDescTransacao.clear();
            txtValorTransacao.clear();
            atualizarTudo();
            MainApp.mostrarMensagem("Sucesso", "Transação atualizada com sucesso!", Alert.AlertType.INFORMATION);
        } catch (NumberFormatException e) {
            MainApp.mostrarMensagem("Valor Inválido", "Digite um número válido para o valor.", Alert.AlertType.WARNING);
        } catch (ValidacaoException e) {
            MainApp.mostrarMensagem("Validação", e.getMessage(), Alert.AlertType.WARNING);
        } catch (Exception e) {
            MainApp.mostrarMensagem("Erro", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleExcluirTransacao() {
        Transacao sel = tabelaTransacoes.getSelectionModel().getSelectedItem();
        if (sel == null) {
            MainApp.mostrarMensagem("Atenção", "Selecione uma transação na tabela.", Alert.AlertType.WARNING);
            return;
        }
        try {
            transacaoDAO.deletar(sel.getId());
            atualizarTudo();
        } catch (Exception e) {
            MainApp.mostrarMensagem("Erro", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleSalvarCategoria() {
        try {
            if (txtNomeCategoria.getText().isBlank()) {
                throw new ValidacaoException("Informe o nome da categoria.");
            }
            Long usuarioId = ConexaoDB.getInstancia().getUsuarioLogado().getId();
            categoriaDAO.salvar(new Categoria(null, txtNomeCategoria.getText().trim(), cbTipoCategoria.getValue(), usuarioId));
            txtNomeCategoria.clear();
            atualizarTudo();
        } catch (ValidacaoException e) {
            MainApp.mostrarMensagem("Validação", e.getMessage(), Alert.AlertType.WARNING);
        } catch (Exception e) {
            MainApp.mostrarMensagem("Erro", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleAtualizarCategoria() {
        Categoria sel = tabelaCategorias.getSelectionModel().getSelectedItem();
        if (sel == null) {
            MainApp.mostrarMensagem("Atenção", "Clique em uma categoria na tabela para editá-la.", Alert.AlertType.WARNING);
            return;
        }
        try {
            if (txtNomeCategoria.getText().isBlank()) {
                throw new ValidacaoException("Informe o novo nome da categoria.");
            }
            sel.setNome(txtNomeCategoria.getText().trim());
            sel.setTipo(cbTipoCategoria.getValue());
            categoriaDAO.atualizar(sel);
            txtNomeCategoria.clear();
            atualizarTudo();
            MainApp.mostrarMensagem("Sucesso", "Categoria atualizada com sucesso!", Alert.AlertType.INFORMATION);
        } catch (ValidacaoException e) {
            MainApp.mostrarMensagem("Validação", e.getMessage(), Alert.AlertType.WARNING);
        } catch (Exception e) {
            MainApp.mostrarMensagem("Erro", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleExcluirCategoria() {
        Categoria sel = tabelaCategorias.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        try {
            categoriaDAO.deletar(sel.getId());
            atualizarTudo();
        } catch (Exception e) {
            MainApp.mostrarMensagem("Restrição", "Não é possível excluir uma categoria que já possui transações.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    public void handleAtualizarPerfil() {
        try {
            if (txtNomeUsuario.getText().isBlank() || txtEmailUsuario.getText().isBlank()) {
                throw new ValidacaoException("Nome e e-mail não podem ficar em branco.");
            }
            Usuario alvo = tabelaUsuarios.getSelectionModel().getSelectedItem();
            if (alvo == null) {
                alvo = ConexaoDB.getInstancia().getUsuarioLogado();
            }

            alvo.setNome(txtNomeUsuario.getText().trim());
            alvo.setEmail(txtEmailUsuario.getText().trim());
            if (!txtNovaSenhaUsuario.getText().isBlank()) {
                alvo.setSenhaHash(BCrypt.hashpw(txtNovaSenhaUsuario.getText(), BCrypt.gensalt()));
            }
            usuarioDAO.atualizar(alvo);

            Usuario logado = ConexaoDB.getInstancia().getUsuarioLogado();
            if (alvo.getId().equals(logado.getId())) {
                ConexaoDB.getInstancia().setUsuarioLogado(alvo);
                lblUsuarioLogado.setText("Usuário: " + alvo.getNome() + " (" + alvo.getEmail() + ")");
            }

            txtNovaSenhaUsuario.clear();
            atualizarTudo();
            MainApp.mostrarMensagem("Sucesso", "Dados do usuário atualizados!", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            MainApp.mostrarMensagem("Erro", e.getMessage(), Alert.AlertType.WARNING);
        }
    }

    @FXML
    public void handleExcluirUsuario() {
        Usuario sel = tabelaUsuarios.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        try {
            usuarioDAO.deletar(sel.getId());
            if (sel.getId().equals(ConexaoDB.getInstancia().getUsuarioLogado().getId())) {
                handleSair();
            } else {
                atualizarTudo();
            }
        } catch (Exception e) {
            MainApp.mostrarMensagem("Erro", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleSair() {
        ConexaoDB.getInstancia().setUsuarioLogado(null);
        MainApp.trocarTela("/fxml/login.fxml", "FinanControl - Login e Cadastro");
    }
}
