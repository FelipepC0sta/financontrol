package com.financontrol.dao;

import com.financontrol.model.Categoria;
import com.financontrol.util.ConexaoDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO implements Repositorio<Categoria> {

    @Override
    public void salvar(Categoria c) throws SQLException {
        String sql = "INSERT INTO categorias (nome, tipo, usuario_id) VALUES (?, ?, ?)";
        try (Connection conn = ConexaoDB.getInstancia().getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, c.getNome());
            stmt.setString(2, c.getTipo());
            stmt.setLong(3, c.getUsuarioId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void atualizar(Categoria c) throws SQLException {
        String sql = "UPDATE categorias SET nome = ?, tipo = ? WHERE id = ?";
        try (Connection conn = ConexaoDB.getInstancia().getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, c.getNome());
            stmt.setString(2, c.getTipo());
            stmt.setLong(3, c.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void deletar(Long id) throws SQLException {
        String sql = "DELETE FROM categorias WHERE id = ?";
        try (Connection conn = ConexaoDB.getInstancia().getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Categoria> listarTodos() throws SQLException {
        Long usuarioId = ConexaoDB.getInstancia().getUsuarioLogado().getId();
        return listarPorUsuario(usuarioId);
    }

    public List<Categoria> listarPorUsuario(Long usuarioId) throws SQLException {
        String sql = "SELECT id, nome, tipo, usuario_id FROM categorias WHERE usuario_id = ? ORDER BY tipo, nome";
        List<Categoria> lista = new ArrayList<>();
        try (Connection conn = ConexaoDB.getInstancia().getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Categoria(
                            rs.getLong("id"),
                            rs.getString("nome"),
                            rs.getString("tipo"),
                            rs.getLong("usuario_id")
                    ));
                }
            }
        }
        return lista;
    }
}
