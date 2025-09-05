package estruturaDoProjeto.agendamendo;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        AgendamentoDAO dao = new AgendamentoDAO();
        Scanner sc = new Scanner(System.in);
        int opcao;

        do {
            System.out.println("\n===== CRUD AGENDAMENTOS =====");
            System.out.println("1 - Criar");
            System.out.println("2 - Listar");
            System.out.println("3 - Atualizar");
            System.out.println("4 - Deletar");
            System.out.println("0 - Sair");
            System.out.print("Escolha: ");
            opcao = sc.nextInt();
            sc.nextLine();

            switch (opcao) {
                case 1 -> {
                    Agendamento ag = new Agendamento();
                    System.out.print("Status (PENDENTE/CONFIRMADO/CANCELADO): ");
                    ag.setStatus(sc.nextLine());
                    System.out.print("Dia mês (yyyy-mm-dd hh:mm:ss): ");
                    ag.setDiaMes(Timestamp.valueOf(sc.nextLine()));
                    System.out.print("Dia semana (SEGUNDA/TERCA/...): ");
                    ag.setDiaSemana(sc.nextLine());
                    System.out.print("Hora início (hh:mm:ss): ");
                    ag.setHoraInicio(Time.valueOf(sc.nextLine()));
                    System.out.print("Hora término (hh:mm:ss): ");
                    ag.setHoraTermino(Time.valueOf(sc.nextLine()));

                    dao.inserir(ag);
                }
                case 2 -> {
                    List<Agendamento> lista = dao.listar();
                    lista.forEach(System.out::println);
                }
                case 3 -> {
                    System.out.print("ID para atualizar: ");
                    int id = sc.nextInt();
                    sc.nextLine();
                    Agendamento ag = new Agendamento();
                    ag.setIdAgendamento(id);
                    System.out.print("Novo Status: ");
                    ag.setStatus(sc.nextLine());
                    System.out.print("Novo Dia mês (yyyy-mm-dd hh:mm:ss): ");
                    ag.setDiaMes(Timestamp.valueOf(sc.nextLine()));
                    System.out.print("Novo Dia semana: ");
                    ag.setDiaSemana(sc.nextLine());
                    System.out.print("Nova Hora início (hh:mm:ss): ");
                    ag.setHoraInicio(Time.valueOf(sc.nextLine()));
                    System.out.print("Nova Hora término (hh:mm:ss): ");
                    ag.setHoraTermino(Time.valueOf(sc.nextLine()));

                    dao.atualizar(ag);
                }
                case 4 -> {
                    System.out.print("ID para deletar: ");
                    int id = sc.nextInt();
                    dao.deletar(id);
                }
                case 0 -> System.out.println("Saindo...");
                default -> System.out.println("Opção inválida!");
            }
        } while (opcao != 0);

        sc.close();
    }
}
