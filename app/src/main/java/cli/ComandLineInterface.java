package cli;

import domain.entity.Preparation;
import domain.entity.PreparationComponent;
import domain.entity.Solution;
import domain.enums.ComponentUnit;
import domain.enums.FinalQuantityUnit;
import domain.enums.SolutionConcentrationUnit;
import manager.PreparationComponentManager;
import manager.PreparationManager;
import manager.SolutionManager;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
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
            while (!scanner.hasNextLine()) {

            }
            String command = scanner.nextLine().trim().toLowerCase();
            if (command.equals("exit")) {
                System.out.println("выход");
                break;
            }
            processCommand(command);
        }
    }

    private void printHelp() {
        System.out.println("Доступные команды:");
        System.out.println("help - выводит информацию о доступных командах");
        System.out.println("exit - выход");
        System.out.println("sol_list - выводит список растворов");
        System.out.println("sol_add - создает раствор");
        System.out.println("sol_show - выводит данные о растворе по его id");
        System.out.println("prep_add - создает факт приготовления");
        System.out.println("prep_show - выводит данные о приготовлении");
        System.out.println("prep_list - выводит список приготовлений");
        System.out.println("comp_add - добавляет компонент приготовления");
        System.out.println("prep_update - обновляет данные о приготовлениях " );
        System.out.println("comp_list - выводит список компонентов приготовления, по его Id");
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
                case "sol_show":
                    solShow();
                    break;
                case "prep_add":
                    prepAdd();
                    break;
                case "prep_show":
                    prepShow();
                    break;
                case "prep_list":
                    prepList();
                    break;
                case "prep_delete":
                    prepDelete();
                    break;
                case "comp_add":
                    compAdd();
                    break;
                case "comp_list":
                    compList();
                    break;
                case "prep_update":
                    prepUpdate();
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
            System.out.println("Единицы измерения концентрация:  " + solution.getConcentrationUnit());
            System.out.println("Растворитель:  " + solution.getSolvent());
            System.out.println("Имя пользователя:  " + solution.getOwnerUsername());
            System.out.println("Дата создания:  " + solution.getCreatedAt());
        }
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

        System.out.println("имя пользователя:");
        String ownerName=scanner.nextLine().trim();

        try {
            solutionManager.addSolution(name,concentration,concentrationUnit, solvent, ownerName);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }

        System.out.println("Данные введены");
    }

    private void solShow(){
        System.out.println("Введите ID раствора");
        long id = Long.parseLong(scanner.nextLine().trim());
        Solution solution = solutionManager.getSolution(id);
        System.out.println("ID: " + solution.getId());
        System.out.println();
        System.out.println("Название: " + solution.getName());
        System.out.println("Концентрация:  " + solution.getConcentration());
        System.out.println("Единицы измерения концентрация:  " + solution.getConcentrationUnit());
        System.out.println("Растворитель:  " + solution.getSolvent());
        System.out.println("Имя пользователя:  " + solution.getOwnerUsername());
        System.out.println("Дата создания:  " + solution.getCreatedAt());
    }

    private void prepAdd(){
        System.out.println("Введите ID раствора");
        long id = Long.parseLong(scanner.nextLine().trim());
        Solution solution = solutionManager.getSolution(id);

        System.out.println("обьем или масса ");
        double finalQuantity = Double.parseDouble(scanner.nextLine().trim());

        System.out.println("Единицы измерения :");
        String finalQuantityUnit =scanner.nextLine().trim().toUpperCase();
        FinalQuantityUnit finalUnit = FinalQuantityUnit.valueOf(finalQuantityUnit);

        System.out.println("имя пользователя:");
        String ownerName=scanner.nextLine().trim();

        System.out.println("коментарий");
        String comment =scanner.nextLine().trim();

        Instant prepAt = Instant.now();
        try{
            preparationManager.addPreparation(id,finalQuantity,finalUnit,comment,ownerName,prepAt);
        }catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }

        System.out.println("Данные введены");
    }

    private void prepShow(){
        System.out.println("Введите ID приготовления");
        long id = Long.parseLong(scanner.nextLine().trim());
        Preparation prep = preparationManager.getPreparation(id);
            System.out.println("ID: " + prep.getId());
            System.out.println("ID раствора: " + prep.getSolutionId());
            System.out.println("Масса или обьем: " + prep.getFinalQuantity() + " "+ prep.getFinalUnit());
            System.out.println("Имя пользователя:  " + prep.getOwnerUsername());
            System.out.println("Дата создания:  " + prep.getCreatedAt());
            System.out.println("Комментарий: " + prep.getComment());
    }

    private void prepList(){
        Collection<Preparation> all = preparationManager.getAllPreparations();
        if (all.isEmpty()){
            System.out.println("Нет приготовлений, добавьте их с помощью команды prep_add");
            return;
        }
        System.out.println("Список приготовлений");
        for (Preparation preparation : all) {
            System.out.println("ID: " + preparation.getId());
            System.out.println("ID раствора: " + preparation.getSolutionId());
            System.out.println("Масса или обьем: " + preparation.getFinalQuantity() + " "+ preparation.getFinalUnit());
            System.out.println("Имя пользователя:  " + preparation.getOwnerUsername());
            System.out.println("Дата создания:  " + preparation.getCreatedAt());
            System.out.println("Комментарий: " + preparation.getComment());
        }
    }

    private void prepDelete(){
        System.out.println("Введите Id приготовления, которое вы хотите удалить");
        long id = Long.parseLong(scanner.nextLine().trim());
        preparationManager.removePreparation(id);
        System.out.println("Приготовление удалено");
    }

    private void compAdd(){
        System.out.println("Введите Id приготовления:" );
        long id = Long.parseLong(scanner.nextLine().trim());
        Preparation prep = preparationManager.getPreparation(id);

        System.out.println("Введите Id партии (от 1 до 100): ");
        long idb = Long.parseLong(scanner.nextLine().trim());

        System.out.println("Введите количество вещества: ");
        double quantity = Double.parseDouble(scanner.nextLine().trim());

        System.out.println("Введите единицы измерения количества вещества(G, ML)");
        String componentUnit = scanner.nextLine().trim().toUpperCase();
        ComponentUnit comUnit = ComponentUnit.valueOf(componentUnit);

        try {
            PreparationComponent comp = preparationComponentManager.addComponent(id,idb, quantity,comUnit );
        } catch (IllegalAccessException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
        System.out.println("Данные введены");
    }

    private void compList(){
        System.out.println("Введите Id приготовления:" );
        long id = Long.parseLong(scanner.nextLine().trim());
        Preparation prep = preparationManager.getPreparation(id);
        List<PreparationComponent> comp = preparationComponentManager.getComponentsForPreparation(id);
        for (PreparationComponent component : comp) {
            System.out.println("ID: " + component.getId());
            System.out.println("ID партии: " + component.getBatchId());
            System.out.println("Количества вещества: " + component.getQuantity() + " "+ component.getUnit());
        }
    }

    private void prepUpdate(){
        System.out.println("Введите id приготовления,которое хотите обновить");
        long id = Long.parseLong(scanner.nextLine().trim());
        PreparationManager prep = preparationManager;

        System.out.println("Новая масса или обьем");
        String input = scanner.nextLine().trim();
        Double newFinalQuantity = null;
        if (!input.isEmpty()){
            newFinalQuantity = Double.parseDouble(input);
        }


        System.out.println("Новые единицы измерения :");
        String inputt =scanner.nextLine().trim().toUpperCase();
        FinalQuantityUnit newFinalUnit = null;
        if(!inputt.isEmpty()){
            try{
                newFinalUnit = FinalQuantityUnit.valueOf(inputt);
            }catch (IllegalArgumentException e){}

        }

        System.out.println("Новый коментарий:" );
        String inpu = scanner.nextLine().trim().toUpperCase();
        String newComment = null;
        if(!inpu.isEmpty()){
            newComment = inpu;
        }

        try {
            prep.updatePreparation(id, newFinalQuantity, newFinalUnit, newComment);
        }catch (IllegalArgumentException e) {
            System.out.println("Ошибка : " + e.getMessage());
        }
        System.out.println("Данные обновленны");
    }
}
