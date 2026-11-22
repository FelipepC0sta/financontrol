package com.financontrol.dao;

import java.sql.SQLException;
import java.util.List;

public interface Repositorio<T> {

    void salvar(T entidade) throws SQLException;

    void atualizar(T entidade) throws SQLException;

    void deletar(Long id) throws SQLException;

    List<T> listarTodos() throws SQLException;
}
