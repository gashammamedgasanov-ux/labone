package cli;

import domain.entity.Solution;
import domain.enums.SolutionConcentrationUnit;
import manager.PreparationComponentManager;
import manager.PreparationManager;
import manager.SolutionManager;

import java.sql.SQLOutput;
import java.util.Map;
import java.util.Scanner;

public class ComandLineInterface {
    private PreparationManager preparationManager;
    private PreparationComponentManager preparationComponentManager;
    private SolutionManager solutionManager;
    private Scanner scanner;

    public ComandLineInterface(PreparationManager preparationManager,
                               PreparationComponentManager preparationComponentManager,
                               SolutionManager solutionManager) {
        this.preparationManager = preparationManager;
        this.preparationComponentManager = preparationComponentManager;
        this.solutionManager = solutionManager;
        this.scanner = new Scanner(System.in);
    }

    public void start(){
        System.out.println("Добро пожаловать");
        printHelp();

        while (true) {
            String command = scanner.nextLine().trim().toLowerCase();
            processCommand(command);
        }
    }

    private void printHelp() {
        System.out.println("Доступные команды:");
        System.out.println("help - выводит информацию о доступных командах");
        System.out.println("exit - выход");

    }

    private void processCommand(String command) {
        try {
            switch (command) {
                case "help":
                    printHelp();
                    break;
                case "exit":
                    System.exit(0);
                    break;
                case "sol_list":
                    solList();
                    break;
                case "sol_add":
                    solAdd();
                    break;
                default:
                    System.out.println("Такой команды нет, введите help для списка команд");
            }
         //обработка ошибок методов
        } catch (IllegalArgumentException e){
            System.out.println("Ошибка: " + e.getMessage());
        // обрабатывает неизвесные ошибки
        } catch (Exception e) {
            System.out.println("Неизвестная ошибка: " + e.getMessage());
        }
    }

    private void solList(){
        Map<Long, Solution> all = solutionManager.getAll();
        if (all.isEmpty()){
           System.out.println("Нет растворов, добавьте растворы с помощью команды sol_add");
           return;
       }
        System.out.println("Список растворов: ");
        for (Map.Entry<Long, Solution> entry : all.entrySet()) {
            Solution solution = entry.getValue();
            System.out.println("ID: " + solution.getId());
            System.out.println("Название: " + solution.getName());
            System.out.println("Концентрация:  " + solution.getConcentration());
            System.out.println("Единицы измерения Концентрация:  " + solution.getConcentrationUnit());
            System.out.println("Растворитель:  " + solution.getSolvent());
            System.out.println("Имя пользователя:  " + solution.getOwnerUsername());
            System.out.println("Дата создания:  " + solution.getCreatedAt());
        }
        System.out.println("Данные введены");
    }

    private void solAdd(){
        System.out.println("название:");
        String name=scanner.nextLine().trim();

        System.out.println("концентрация:");
        double concentration = Double.parseDouble(scanner.nextLine().trim());

        System.out.println(" Единицы измерения концентрации (PERCENT,MOL_PER_L,G_PER_L):");
        String conUnit=scanner.nextLine().trim().toUpperCase();
        SolutionConcentrationUnit concentrationUnit = SolutionConcentrationUnit.valueOf(conUnit);

        System.out.println("растворитель:");
        String solvent = scanner.nextLine().trim();

        System.out.println("имя пользователяЖ");
        String ownerName=scanner.nextLine().trim();
        try {
            solutionManager.addSolution(name,concentration,concentrationUnit, solvent, ownerName);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}
