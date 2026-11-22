package com.financontrol.util;

import com.financontrol.model.Usuario;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public final class ConexaoDB {

    private static final String URL = "jdbc:h2:./data/financontrol_db;AUTO_SERVER=TRUE";
    private static ConexaoDB instancia;
    private Usuario usuarioLogado;

    private ConexaoDB() {
        try (InputStream in = getClass().getResourceAsStream("/script.sql")) {
            if (in != null) {
                Scanner leitor = new Scanner(in, StandardCharsets.UTF_8);
                StringBuilder textoSql = new StringBuilder();
                while (leitor.hasNextLine()) {
                    String linha = leitor.nextLine().trim();
                    if (!linha.isEmpty()) {
                        textoSql.append(linha).append("\n");
                    }
                }
                leitor.close();

                try (Connection conn = getConexao(); Statement stmt = conn.createStatement()) {
                    String[] comandos = textoSql.toString().split(";");
                    for (String cmd : comandos) {
                        if (!cmd.trim().isEmpty()) {
                            stmt.execute(cmd.trim());
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inicializar banco H2: " + e.getMessage(), e);
        }
    }

    public static synchronized ConexaoDB getInstancia() {
        if (instancia == null) {
            instancia = new ConexaoDB();
        }
        return instancia;
    }

    public Connection getConexao() throws SQLException {
        return DriverManager.getConnection(URL, "sa", "");
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public void setUsuarioLogado(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }
}
