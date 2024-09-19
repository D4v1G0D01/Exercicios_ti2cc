package dao;

import model.Produto;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {
    private Connection conexao;

    public ProdutoDAO() {
        this.conexao = null;
        conectar();
    }

    public boolean conectar() {
        String driverName = "org.postgresql.Driver";
        String serverName = "localhost";
        String mydatabase = "postgres";  // Nome do banco de dados
        int porta = 5432;
        String url = "jdbc:postgresql://" + serverName + ":" + porta + "/" + mydatabase;
        String username = "davi";   // Seu nome de usuário
        String password = "davi004";  // Sua senha
        boolean status = false;

        try {
            Class.forName(driverName);
            conexao = DriverManager.getConnection(url, username, password);
            status = (conexao != null);
            System.out.println("Conexão efetuada com o PostgreSQL!");
        } catch (ClassNotFoundException e) {
            System.err.println("Conexão NÃO efetuada -- Driver não encontrado -- " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Conexão NÃO efetuada com o PostgreSQL -- " + e.getMessage());
        }

        return status;
    }

    public int getMaxIdFromDB() {
        int maxId = 0;
        try {
            Statement stmt = conexao.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MAX(id) AS max_id FROM produtos");
            if (rs.next()) {
                maxId = rs.getInt("max_id");
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Erro ao obter o maxId: " + e.getMessage());
        }
        return maxId;
    }

    public void add(Produto produto) {
        try {
            String sql = "INSERT INTO produtos (id, descricao, preco, quantidade, dataFabricacao, dataValidade) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, produto.getId());
            stmt.setString(2, produto.getDescricao());
            stmt.setFloat(3, produto.getPreco());
            stmt.setInt(4, produto.getQuant());
            stmt.setTimestamp(5, Timestamp.valueOf(produto.getDataFabricacao()));
            stmt.setDate(6, Date.valueOf(produto.getDataValidade()));
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("ERRO ao gravar o produto '" + produto.getDescricao() + "' no banco de dados!");
        }
    }

    public Produto get(int id) {
        Produto produto = null;
        try {
            String sql = "SELECT * FROM produtos WHERE id = ?";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                LocalDateTime dataFabricacao = rs.getTimestamp("dataFabricacao").toLocalDateTime();
                LocalDate dataValidade = rs.getDate("dataValidade").toLocalDate();
                
                produto = new Produto(
                    rs.getInt("id"),
                    rs.getString("descricao"),
                    rs.getFloat("preco"),
                    rs.getInt("quantidade"),
                    dataFabricacao,
                    dataValidade
                );
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Erro ao buscar o produto com id: " + id + " - " + e.getMessage());
        }
        return produto;
    }

    public void update(Produto p) {
        try {
            String sql = "UPDATE produtos SET descricao = ?, preco = ?, quantidade = ?, dataFabricacao = ?, dataValidade = ? WHERE id = ?";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setString(1, p.getDescricao());
            stmt.setFloat(2, p.getPreco());
            stmt.setInt(3, p.getQuant());
            stmt.setTimestamp(4, Timestamp.valueOf(p.getDataFabricacao()));
            stmt.setDate(5, Date.valueOf(p.getDataValidade()));
            stmt.setInt(6, p.getId());
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("ERRO ao atualizar o produto '" + p.getDescricao() + "' no banco de dados!");
        }
    }

    public void remove(Produto p) {
        try {
            String sql = "DELETE FROM produtos WHERE id = ?";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, p.getId());
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("ERRO ao remover o produto '" + p.getDescricao() + "' do banco de dados!");
        }
    }

    public List<Produto> getAll() {
        List<Produto> produtos = new ArrayList<>();
        try {
            Statement stmt = conexao.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM produtos");
            while (rs.next()) {
                LocalDateTime dataFabricacao = rs.getTimestamp("dataFabricacao").toLocalDateTime();
                LocalDate dataValidade = rs.getDate("dataValidade").toLocalDate();
                
                Produto produto = new Produto(
                    rs.getInt("id"),
                    rs.getString("descricao"),
                    rs.getFloat("preco"),
                    rs.getInt("quantidade"),
                    dataFabricacao,
                    dataValidade
                );
                produtos.add(produto);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Erro ao listar os produtos: " + e.getMessage());
        }
        return produtos;
    }

    public boolean close() {
        boolean status = false;
        try {
            if (conexao != null && !conexao.isClosed()) {
                conexao.close();
                status = true;
                System.out.println("Conexão com o banco de dados fechada.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar a conexão: " + e.getMessage());
        }
        return status;
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            this.close();
        } catch (Exception e) {
            System.out.println("ERRO ao fechar a conexão com o banco de dados!");
            e.printStackTrace();
        }
    }
}