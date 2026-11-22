package com.financontrol.dao;

import com.financontrol.exception.ValidacaoException;
import com.financontrol.model.Categoria;
import com.financontrol.model.Transacao;
import com.financontrol.model.Usuario;
import com.financontrol.util.ConexaoDB;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO implements Repositorio<Usuario> {

    @Override
    public void salvar(Usuario u) throws SQLException {
        String sql = "INSERT INTO usuarios (nome, email, senha_hash) VALUES (?, ?, ?)";
        try (Connection conn = ConexaoDB.getInstancia().getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, u.getNome());
            stmt.setString(2, u.getEmail().toLowerCase().trim());
            stmt.setString(3, u.getSenhaHash());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    u.setId(keys.getLong(1));
                }
            }
        }
    }

    @Override
    public void atualizar(Usuario u) throws SQLException {
        String sql = "UPDATE usuarios SET nome = ?, email = ?, senha_hash = ? WHERE id = ?";
        try (Connection conn = ConexaoDB.getInstancia().getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, u.getNome());
            stmt.setString(2, u.getEmail().toLowerCase().trim());
            stmt.setString(3, u.getSenhaHash());
            stmt.setLong(4, u.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void deletar(Long id) throws SQLException {
        try (Connection conn = ConexaoDB.getInstancia().getConexao();
             PreparedStatement delTransacoes = conn.prepareStatement("DELETE FROM transacoes WHERE usuario_id = ?");
             PreparedStatement delCategorias = conn.prepareStatement("DELETE FROM categorias WHERE usuario_id = ?");
             PreparedStatement delUsuario = conn.prepareStatement("DELETE FROM usuarios WHERE id = ?")) {

            delTransacoes.setLong(1, id);
            delTransacoes.executeUpdate();

            delCategorias.setLong(1, id);
            delCategorias.executeUpdate();

            delUsuario.setLong(1, id);
            delUsuario.executeUpdate();
        }
    }

    @Override
    public List<Usuario> listarTodos() throws SQLException {
        String sql = "SELECT id, nome, email, senha_hash FROM usuarios ORDER BY nome";
        List<Usuario> lista = new ArrayList<>();
        try (Connection conn = ConexaoDB.getInstancia().getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(new Usuario(
                        rs.getLong("id"),
                        rs.getString("nome"),
                        rs.getString("email"),
                        rs.getString("senha_hash")
                ));
            }
        }
        return lista;
    }

    public Usuario autenticar(String email, String senha) throws ValidacaoException, SQLException {
        if (email == null || email.isBlank() || senha == null || senha.isBlank()) {
            throw new ValidacaoException("Preencha o e-mail e a senha.");
        }
        String sql = "SELECT id, nome, email, senha_hash FROM usuarios WHERE LOWER(email) = LOWER(?)";
        try (Connection conn = ConexaoDB.getInstancia().getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String hash = rs.getString("senha_hash");
                    if (BCrypt.checkpw(senha, hash)) {
                        Usuario u = new Usuario(rs.getLong("id"), rs.getString("nome"), rs.getString("email"), hash);
                        ConexaoDB.getInstancia().setUsuarioLogado(u);
                        return u;
                    }
                }
            }
        }
        throw new ValidacaoException("E-mail ou senha inválidos.");
    }

    public Usuario cadastrarNovo(String nome, String email, String senha) throws ValidacaoException, SQLException {
        if (nome == null || nome.isBlank() || email == null || email.isBlank() || senha == null || senha.isBlank()) {
            throw new ValidacaoException("Todos os campos (Nome, E-mail e Senha) são obrigatórios.");
        }
        String hash = BCrypt.hashpw(senha, BCrypt.gensalt());
        Usuario novo = new Usuario(null, nome.trim(), email.trim().toLowerCase(), hash);
        salvar(novo);

        CategoriaDAO catDao = new CategoriaDAO();
        catDao.salvar(new Categoria(null, "Salário", "RECEITA", novo.getId()));
        catDao.salvar(new Categoria(null, "Freelance", "RECEITA", novo.getId()));
        catDao.salvar(new Categoria(null, "Alimentação", "DESPESA", novo.getId()));
        catDao.salvar(new Categoria(null, "Moradia", "DESPESA", novo.getId()));
        catDao.salvar(new Categoria(null, "Lazer", "DESPESA", novo.getId()));
        return novo;
    }

    public void criarContaDemoSeNaoExistir() {
        try {
            for (Usuario u : listarTodos()) {
                if ("demo@financontrol.com".equalsIgnoreCase(u.getEmail())) {
                    return;
                }
            }
            Usuario demo = cadastrarNovo("Aluno Demo", "demo@financontrol.com", "123456");
            List<Categoria> cats = new CategoriaDAO().listarPorUsuario(demo.getId());
            TransacaoDAO tDao = new TransacaoDAO();
            tDao.salvar(Transacao.criar(null, "RECEITA", "Salário Mensal", 3500.0, LocalDate.now().minusDays(5), cats.get(0), demo.getId()));
            tDao.salvar(Transacao.criar(null, "DESPESA", "Supermercado", 650.0, LocalDate.now().minusDays(3), cats.get(2), demo.getId()));
            tDao.salvar(Transacao.criar(null, "DESPESA", "Aluguel", 1200.0, LocalDate.now().minusDays(2), cats.get(3), demo.getId()));
        } catch (Exception ignored) {
        }
    }
}
