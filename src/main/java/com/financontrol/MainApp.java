package com.financontrol;

import com.financontrol.dao.UsuarioDAO;
import com.financontrol.util.ConexaoDB;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static Stage stagePrincipal;

    @Override
    public void start(Stage primaryStage) {
        stagePrincipal = primaryStage;
        ConexaoDB.getInstancia();
        new UsuarioDAO().criarContaDemoSeNaoExistir();

        trocarTela("/fxml/login.fxml", "FinanControl - Login e Cadastro");
        primaryStage.show();
    }

    public static void trocarTela(String caminhoFxml, String titulo) {
        try {
            Parent root = FXMLLoader.load(MainApp.class.getResource(caminhoFxml));
            Scene scene = new Scene(root, 980, 640);
            scene.getStylesheets().add(MainApp.class.getResource("/css/style.css").toExternalForm());
            stagePrincipal.setTitle(titulo);
            stagePrincipal.setScene(scene);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao abrir tela " + caminhoFxml + ": " + e.getMessage(), e);
        }
    }

    public static void mostrarMensagem(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
