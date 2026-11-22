package com.financontrol.controller;

import com.financontrol.MainApp;
import com.financontrol.dao.UsuarioDAO;
import com.financontrol.exception.ValidacaoException;
import com.financontrol.model.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField txtNome;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtSenha;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
    public void handleEntrar() {
        try {
            Usuario u = usuarioDAO.autenticar(txtEmail.getText(), txtSenha.getText());
            MainApp.trocarTela("/fxml/principal.fxml", "FinanControl - Olá, " + u.getNome());
        } catch (ValidacaoException e) {
            MainApp.mostrarMensagem("Atenção", e.getMessage(), Alert.AlertType.WARNING);
        } catch (Exception e) {
            MainApp.mostrarMensagem("Erro", "Erro no banco: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleCadastrar() {
        try {
            Usuario novo = usuarioDAO.cadastrarNovo(txtNome.getText(), txtEmail.getText(), txtSenha.getText());
            MainApp.mostrarMensagem("Sucesso",
                    "Usuário '" + novo.getNome() + "' cadastrado! Clique em Entrar.",
                    Alert.AlertType.INFORMATION);
        } catch (ValidacaoException e) {
            MainApp.mostrarMensagem("Atenção", e.getMessage(), Alert.AlertType.WARNING);
        } catch (Exception e) {
            MainApp.mostrarMensagem("Erro", "E-mail já cadastrado ou erro no banco.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handlePreencherDemo() {
        txtEmail.setText("demo@financontrol.com");
        txtSenha.setText("123456");
    }
}
