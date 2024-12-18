package com.raphaelprojetos.sentinel.dao;

import com.raphaelprojetos.sentinel.database.Database;
import com.raphaelprojetos.sentinel.dto.UsuarioDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    // Método para salvar um novo usuário no banco
    public void salvarUsuario(UsuarioDTO usuario) {
        String sql = "INSERT INTO usuarios (nome, senha, admin) VALUES (?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getSenha());
            stmt.setBoolean(3, usuario.isAdmin());

            stmt.executeUpdate();

            // Recupera o ID gerado automaticamente
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    usuario.setId(generatedKeys.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para buscar todos os usuários do banco
    public List<UsuarioDTO> buscarTodosUsuarios() {
        String sql = "SELECT id, nome, senha, admin FROM usuarios ORDER BY id ASC";
        List<UsuarioDTO> usuarios = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                UsuarioDTO usuario = new UsuarioDTO();
                usuario.setId(rs.getLong("id"));
                usuario.setNome(rs.getString("nome"));
                usuario.setSenha(rs.getString("senha"));
                usuario.setAdmin(rs.getBoolean("admin"));
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return usuarios;
    }

    // Método para buscar um usuário por ID
    public UsuarioDTO buscarUsuarioPorId(Long id) {
        String sql = "SELECT id, nome, senha, admin FROM usuarios WHERE id = ?";
        UsuarioDTO usuario = null;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    usuario = new UsuarioDTO();
                    usuario.setId(rs.getLong("id"));
                    usuario.setNome(rs.getString("nome"));
                    usuario.setSenha(rs.getString("senha"));
                    usuario.setAdmin(rs.getBoolean("admin"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return usuario;
    }

    // Método para autenticar um usuário pelo nome e senha
    public UsuarioDTO autenticarUsuario(String nome, String senha) {
        String sql = "SELECT id, nome, senha, admin FROM usuarios WHERE nome = ? AND senha = ?";
        UsuarioDTO usuario = null;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);
            stmt.setString(2, senha);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    usuario = new UsuarioDTO();
                    usuario.setId(rs.getLong("id"));
                    usuario.setNome(rs.getString("nome"));
                    usuario.setSenha(rs.getString("senha"));
                    usuario.setAdmin(rs.getBoolean("admin"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return usuario;
    }

    // Método para atualizar os dados de um usuário
    public boolean atualizarUsuario(UsuarioDTO usuario) {
        String sql = "UPDATE usuarios SET nome = ?, senha = ?, admin = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getSenha());
            stmt.setBoolean(3, usuario.isAdmin());
            stmt.setLong(4, usuario.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método para deletar um usuário pelo ID
    public boolean deletarUsuarioPorId(Long id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
