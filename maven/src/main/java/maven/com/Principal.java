package maven.com;

import java.util.List;
import java.util.Scanner;
import dao.UsuarioDAO;
import model.Usuario;

public class Principal {
	
	public static void main(String[] args) throws Exception {
		UsuarioDAO usuarioDAO = new UsuarioDAO();
        Scanner scanner = new Scanner(System.in);
        int opcao = 0;

        while (opcao != 5) {
            System.out.println("\n==== Menu ====");
            System.out.println("1. Listar usuários");
            System.out.println("2. Inserir usuário");
            System.out.println("3. Excluir usuário");
            System.out.println("4. Atualizar usuário");
            System.out.println("5. Sair");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine(); // consumir a nova linha

            switch (opcao) {
                case 1:
                    System.out.println("\n==== Mostrar usuários ordenados por código ===");
                    List<Usuario> usuarios = usuarioDAO.getOrderByCodigo();
                    for (Usuario u: usuarios) {
                        System.out.println(u.toString());
                    }
                    break;

                case 2:
                    System.out.println("\n==== Inserir usuário ===");
                    System.out.print("Código: ");
                    int codigo = scanner.nextInt();
                    scanner.nextLine(); // consumir a nova linha

                    System.out.print("Login: ");
                    String login = scanner.nextLine();

                    System.out.print("Senha: ");
                    String senha = scanner.nextLine();

                    System.out.print("Sexo (M/F): ");
                    char sexo = scanner.nextLine().charAt(0);

                    Usuario novoUsuario = new Usuario(codigo, login, senha, sexo);
                    if (usuarioDAO.insert(novoUsuario)) {
                        System.out.println("Inserção com sucesso -> " + novoUsuario.toString());
                    }
                    break;

                case 3:
                    System.out.println("\n==== Excluir usuário ===");
                    System.out.print("Código do usuário a ser excluído: ");
                    int codigoExcluir = scanner.nextInt();
                    if (usuarioDAO.delete(codigoExcluir)) {
                        System.out.println("Usuário excluído com sucesso!");
                    }
                    break;

                case 4:
                    System.out.println("\n==== Atualizar usuário ===");
                    System.out.print("Código: ");
                    int codigoAtualizar = scanner.nextInt();
                    scanner.nextLine(); // consumir a nova linha

                    System.out.print("Novo Login: ");
                    String novoLogin = scanner.nextLine();

                    System.out.print("Nova Senha: ");
                    String novaSenha = scanner.nextLine();

                    System.out.print("Novo Sexo (M/F): ");
                    char novoSexo = scanner.nextLine().charAt(0);

                    Usuario usuarioAtualizar = new Usuario(codigoAtualizar, novoLogin, novaSenha, novoSexo);
                    if (usuarioDAO.update(usuarioAtualizar)) {
                        System.out.println("Atualização com sucesso -> " + usuarioAtualizar.toString());
                    }
                    break;

                case 5:
                    System.out.println("Saindo...");
                    break;

                default:
                    System.out.println("Opção inválida! Tente novamente.");
                    break;
            }
        }

        scanner.close();
    }
}