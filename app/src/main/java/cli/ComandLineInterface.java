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

    public void start() {
        System.out.println("Добро пожаловать");
        printHelp();

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim(); // читаем всю строку целиком
            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Выход");
                break;
            }
            processCommand(input); // передаём всю строку в обработчик
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
        System.out.println("prep_update - обновляет данные о приготовлениях ");
        System.out.println("comp_list - выводит список компонентов приготовления, по его Id");
    }

    // обрабатываем команды, получаем всю строку
    private void processCommand(String input) {
        String[] parts = input.split("\\s+", 2);
        String command = parts[0].toLowerCase(); // команда в нижнем регистре
        String args = parts.length > 1 ? parts[1] : ""; // если аргументов нет, то пустая строка
        try {
            switch (command) {
                case "help":
                    printHelp();
                    break;
                case "exit":
                    System.exit(0);
                    break;
                case "sol_list":
                    // для sol_list передаём все аргументы
                    solList(args);
                    break;
                case "sol_add":
                    solAdd();
                    break;
                case "sol_show":
                    // если не передали id, то ошибка
                    if (args.isEmpty()) throw new IllegalArgumentException("Укажите ID раствора");
                    solShow(args);
                    break;
                case "prep_add":
                    if (args.isEmpty()) throw new IllegalArgumentException("Укажите ID раствора");
                    prepAdd(args);
                    break;
                case "prep_show":
                    if (args.isEmpty()) throw new IllegalArgumentException("Укажите ID приготовления");
                    prepShow(args);
                    break;
                case "prep_list":
                    prepList();
                    break;
                case "prep_delete":
                    if (args.isEmpty()) throw new IllegalArgumentException("Укажите ID приготовления");
                    prepDelete(args);
                    break;
                case "comp_add":
                    if (args.isEmpty()) throw new IllegalArgumentException("Укажите ID приготовления");
                    compAdd(args);
                    break;
                case "comp_list":
                    if (args.isEmpty()) throw new IllegalArgumentException("Укажите ID приготовления");
                    compList(args);
                    break;
                case "prep_update":
                    if (args.isEmpty()) throw new IllegalArgumentException("Укажите ID приготовления");
                    prepUpdate(args);
                    break;
                default:
                    System.out.println("Такой команды нет, введите help для списка команд");
            }
        } catch (NumberFormatException e) {
            //пользователь ввёл не число там, где ожидается число
            System.out.println("Ошибка: ID должен быть числом");
            //обработка ошибок методов
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
            // обрабатывает неизвесные ошибки
        } catch (Exception e) {
            System.out.println("Неизвестная ошибка: " + e.getMessage());
        }
    }
    //теперь принимает строку args, чтобы можно было обработать --q
    private void solList(String args) {
        Map<Long, Solution> all = solutionManager.getAll();
        if (all.isEmpty()) {
            System.out.println("Нет растворов, добавьте растворы с помощью команды sol_add");
            return;
        }
        //переменная для хранения текста поиска
        String query = null;
        //если после команды есть какие-то аргументы
        if (!args.isEmpty()) {
            if (args.startsWith("--q ")) {
                query = args.substring(4).trim();
                if (query.isEmpty()) {
                    query = null;
                } else {
                    if (query.length() > 64) {
                        throw new IllegalArgumentException("Запрос слишком длинный");
                    }
                }
            } else {
                throw new IllegalArgumentException("Неизвестная опция: " + args + ". Поддерживается только --q");
            }
        }

        System.out.println("Список растворов: ");
        for (Map.Entry<Long, Solution> entry : all.entrySet()) {
            Solution solution = entry.getValue();
            boolean matches = true;
            if (query != null) {
                String nameLower = solution.getName().toLowerCase();
                String queryLower = query.toLowerCase();
                matches = nameLower.contains(queryLower);
            }
            if (matches) {
                System.out.println("ID: " + solution.getId());
                System.out.println("Название: " + solution.getName());
                System.out.println("Концентрация: " + solution.getConcentration());
                System.out.println("Единицы измерения концентрации: " + solution.getConcentrationUnit());
                System.out.println("Растворитель: " + solution.getSolvent());
                System.out.println("Имя пользователя: " + solution.getOwnerUsername());
                System.out.println("Дата создания: " + solution.getCreatedAt());
            }
        }
    }


    private void solAdd() {
        System.out.println("название:");
        String name = scanner.nextLine().trim();

        System.out.println("концентрация:");
        double concentration = Double.parseDouble(scanner.nextLine().trim());

        System.out.println(" Единицы измерения концентрации (PERCENT,MOL_PER_L,G_PER_L):");
        String conUnit = scanner.nextLine().trim().toUpperCase();
        SolutionConcentrationUnit concentrationUnit = SolutionConcentrationUnit.valueOf(conUnit);

        System.out.println("растворитель:");
        String solvent = scanner.nextLine().trim();

        System.out.println("имя пользователя:");
        String ownerName = scanner.nextLine().trim();

        try {
            solutionManager.addSolution(name, concentration, concentrationUnit, solvent, ownerName);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }

        System.out.println("Данные введены");
    }

    private void solShow(String idStr) {
        long id = Long.parseLong(idStr);
        Solution solution = solutionManager.getSolution(id);
        if (solution == null) {
            throw new IllegalArgumentException("Раствор с id=" + id + " не найден");
        }
        System.out.println("ID: " + solution.getId());
        System.out.println();
        System.out.println("Название: " + solution.getName());
        System.out.println("Концентрация:  " + solution.getConcentration());
        System.out.println("Единицы измерения концентрация:  " + solution.getConcentrationUnit());
        System.out.println("Растворитель:  " + solution.getSolvent());
        System.out.println("Имя пользователя:  " + solution.getOwnerUsername());
        System.out.println("Дата создания:  " + solution.getCreatedAt());
    }

    private void prepAdd(String solutionIdStr) {
        long solutionId = Long.parseLong(solutionIdStr);
        Solution solution = solutionManager.getSolution(solutionId);
        if (solution == null) {
            throw new IllegalArgumentException("Раствор с id=" + solutionId + " не найден");
        }
        System.out.println("обьем или масса ");
        double finalQuantity = Double.parseDouble(scanner.nextLine().trim());

        System.out.println("Единицы измерения :");
        String finalQuantityUnit = scanner.nextLine().trim().toUpperCase();
        FinalQuantityUnit finalUnit = FinalQuantityUnit.valueOf(finalQuantityUnit);

        System.out.println("имя пользователя:");
        String ownerName = scanner.nextLine().trim();

        System.out.println("коментарий");
        String comment = scanner.nextLine().trim();

        Instant prepAt = Instant.now();
        try {
            preparationManager.addPreparation(solutionId, finalQuantity, finalUnit, comment, ownerName, prepAt);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }

        System.out.println("Данные введены");
    }

    private void prepShow(String idStr) {
        long id = Long.parseLong(idStr);
        Preparation prep = preparationManager.getPreparation(id);
        if (prep == null) {
            throw new IllegalArgumentException("Приготовление с id=" + id + " не найдено");
        }
        System.out.println("ID: " + prep.getId());
        System.out.println("ID раствора: " + prep.getSolutionId());
        System.out.println("Масса или обьем: " + prep.getFinalQuantity() + " " + prep.getFinalUnit());
        System.out.println("Имя пользователя:  " + prep.getOwnerUsername());
        System.out.println("Дата создания:  " + prep.getCreatedAt());
        System.out.println("Комментарий: " + prep.getComment());
    }

    private void prepList() {
        Collection<Preparation> all = preparationManager.getAllPreparations();
        if (all.isEmpty()) {
            System.out.println("Нет приготовлений, добавьте их с помощью команды prep_add");
            return;
        }
        System.out.println("Список приготовлений");
        for (Preparation preparation : all) {
            System.out.println("ID: " + preparation.getId());
            System.out.println("ID раствора: " + preparation.getSolutionId());
            System.out.println("Масса или обьем: " + preparation.getFinalQuantity() + " " + preparation.getFinalUnit());
            System.out.println("Имя пользователя:  " + preparation.getOwnerUsername());
            System.out.println("Дата создания:  " + preparation.getCreatedAt());
            System.out.println("Комментарий: " + preparation.getComment());
        }
    }

    private void prepDelete(String idStr) {
        long id = Long.parseLong(idStr);
        if (preparationManager.getPreparation(id) == null) {
            throw new IllegalArgumentException("Приготовление с id=" + id + " не найдено");
        }
        preparationManager.removePreparation(id);
        System.out.println("Приготовление удалено");
    }

    private void compAdd() {
        System.out.println("Введите Id приготовления:");
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
            PreparationComponent comp = preparationComponentManager.addComponent(id, idb, quantity, comUnit);
        } catch (IllegalAccessException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
        System.out.println("Данные введены");
    }

    private void compList(String preparationIdStr) {
        long preparationId = Long.parseLong(preparationIdStr);
        Preparation prep = preparationManager.getPreparation(preparationId);
        if (prep == null) {
            throw new IllegalArgumentException("Приготовление с id=" + preparationId + " не найдено");
        }
        List<PreparationComponent> components = preparationComponentManager.getComponentsForPreparation(preparationId);
        if (components.isEmpty()) {
            System.out.println("Компоненты не найдены");
            return;
        }
        System.out.println("Список компонентов:");
        for (PreparationComponent component : components) {
            System.out.println("ID: " + component.getId());
            System.out.println("ID партии: " + component.getBatchId());
            System.out.println("Количества вещества: " + component.getQuantity() + " " + component.getUnit());
        }
    }

    private void prepUpdate(String idStr) {
        long id = Long.parseLong(idStr);
        if (preparationManager.getPreparation(id) == null) {
            throw new IllegalArgumentException("Приготовление с id=" + id + " не найдено");
        }
        System.out.println("Новая масса или обьем");
        String input = scanner.nextLine().trim();
        Double newFinalQuantity = input.isEmpty() ? null : Double.parseDouble(input);
        System.out.print("Новые единицы измерения: ");
        String inputUnit = scanner.nextLine().trim().toUpperCase();
        FinalQuantityUnit newFinalUnit = null;
        if (!input.isEmpty()) {
            try {
                newFinalUnit = FinalQuantityUnit.valueOf(inputUnit);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Недопустимая единица измерения");
            }
        }

        System.out.println("Новый коментарий:");
        String newComment = scanner.nextLine().trim();
        if (newComment.isEmpty()) newComment = null;

        preparationManager.updatePreparation(id, newFinalQuantity, newFinalUnit, newComment);
        System.out.println("Данные обновлены");
    }

    private void compAdd(String preparationIdStr) {
        long preparationId = Long.parseLong(preparationIdStr);
        Preparation prep = preparationManager.getPreparation(preparationId);
        if (prep == null) {
            throw new IllegalArgumentException("Приготовление с id=" + preparationId + " не найдено");
        }
        System.out.print("Введите ID партии: ");
        long batchId = Long.parseLong(scanner.nextLine().trim());

        System.out.print("Введите количество вещества: ");
        double quantity = Double.parseDouble(scanner.nextLine().trim());

        System.out.print("Введите единицы измерения количества вещества (G, ML): ");
        String unitStr = scanner.nextLine().trim().toUpperCase();
        ComponentUnit unit = ComponentUnit.valueOf(unitStr);

        try {
            preparationComponentManager.addComponent(preparationId, batchId, quantity, unit);
            System.out.println("Данные введены");
        } catch (IllegalAccessException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}


