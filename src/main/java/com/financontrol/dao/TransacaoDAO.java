package com.financontrol.dao;

import com.financontrol.model.Categoria;
import com.financontrol.model.Transacao;
import com.financontrol.util.ConexaoDB;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransacaoDAO implements Repositorio<Transacao> {

    @Override
    public void salvar(Transacao t) throws SQLException {
        String sql = "INSERT INTO transacoes (descricao, valor, data_transacao, tipo, categoria_id, usuario_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoDB.getInstancia().getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, t.getDescricao());
            stmt.setDouble(2, t.getValor());
            stmt.setDate(3, Date.valueOf(t.getData()));
            stmt.setString(4, t.getTipo());
            stmt.setLong(5, t.getCategoria().getId());
            stmt.setLong(6, t.getUsuarioId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void atualizar(Transacao t) throws SQLException {
        String sql = "UPDATE transacoes SET descricao = ?, valor = ?, data_transacao = ?, tipo = ?, categoria_id = ? WHERE id = ?";
        try (Connection conn = ConexaoDB.getInstancia().getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, t.getDescricao());
            stmt.setDouble(2, t.getValor());
            stmt.setDate(3, Date.valueOf(t.getData()));
            stmt.setString(4, t.getTipo());
            stmt.setLong(5, t.getCategoria().getId());
            stmt.setLong(6, t.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void deletar(Long id) throws SQLException {
        String sql = "DELETE FROM transacoes WHERE id = ?";
        try (Connection conn = ConexaoDB.getInstancia().getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Transacao> listarTodos() throws SQLException {
        Long usuarioId = ConexaoDB.getInstancia().getUsuarioLogado().getId();
        return listarPorUsuario(usuarioId);
    }

    public List<Transacao> listarPorUsuario(Long usuarioId) throws SQLException {
        String sql = """
                SELECT t.id, t.descricao, t.valor, t.data_transacao, t.tipo, t.usuario_id,
                       c.id AS cat_id, c.nome AS cat_nome, c.tipo AS cat_tipo
                FROM transacoes t
                INNER JOIN categorias c ON t.categoria_id = c.id
                WHERE t.usuario_id = ?
                ORDER BY t.data_transacao DESC, t.id DESC
                """;
        List<Transacao> lista = new ArrayList<>();
        try (Connection conn = ConexaoDB.getInstancia().getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Categoria cat = new Categoria(
                            rs.getLong("cat_id"),
                            rs.getString("cat_nome"),
                            rs.getString("cat_tipo"),
                            usuarioId
                    );
                    lista.add(Transacao.criar(
                            rs.getLong("id"),
                            rs.getString("tipo"),
                            rs.getString("descricao"),
                            rs.getDouble("valor"),
                            rs.getDate("data_transacao").toLocalDate(),
                            cat,
                            usuarioId
                    ));
                }
            }
        }
        return lista;
    }
}
