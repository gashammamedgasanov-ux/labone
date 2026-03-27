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
import storage.FileStorage;
import storage.LabData;
import validation.FileValidator;
import validation.PreparationComponentValidation;
import validation.PreparationValidator;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

public class ComandLineInterface {
    private final PreparationManager preparationManager;
    private final PreparationComponentManager preparationComponentManager;
    private final SolutionManager solutionManager;
    private final  Scanner scanner;
    private PreparationComponentValidation PreparationValidation;
    private final FileStorage fileStorage;
    private final FileValidator fileValidator;
    private boolean running = true;
    private String currentFilePath = null;


    public ComandLineInterface(PreparationManager preparationManager,
                               PreparationComponentManager preparationComponentManager,
                               SolutionManager solutionManager) {
        this.preparationManager = preparationManager;
        this.preparationComponentManager = preparationComponentManager;
        this.solutionManager = solutionManager;
        this.fileStorage = new FileStorage();
        this.fileValidator = new FileValidator();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("Добро пожаловать");
        printHelp();

        while (running) {
            System.out.print("> ");
            if (!scanner.hasNextLine()) {
                break;
            }
            String input = scanner.nextLine().trim(); // читаем всю строку целиком
            if (input.equalsIgnoreCase("exit")) {
                Exit();
                break;
            }
            processCommand(input); // передаём всю строку в обработчик
        }
        scanner.close();
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
        System.out.println("load - загружает файл");
        System.out.println("save-сохранить файл");
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
                    Exit();
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
                    prepAdd(args);
                    break;
                case "prep_show":
                    prepShow(args);
                    break;
                case "prep_list":
                    prepList(args);
                    break;
                case "prep_delete":
                    prepDelete(args);
                    break;
                case "comp_add":
                    compAdd();
                    break;
                case "comp_list":
                    compList(args);
                    break;
                case "prep_update":
                    prepUpdate(args);
                    break;
                case "save":
                    Save(args);
                    break;
                case "load":
                    Load(args);
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

    private void prepList(String args) {
    String[] tokens = args.trim().split("\\s+");
    if (tokens.length == 0 || tokens[0].isEmpty()) {
        throw new IllegalArgumentException("Укажите ID раствора");
    }

    long solutionId;
    try {
        solutionId = Long.parseLong(tokens[0]);
    } catch (NumberFormatException e) {
        throw new IllegalArgumentException("ID раствора должен быть числом");
    }

    Solution solution = solutionManager.getSolution(solutionId);
    if (solution == null) {
        throw new IllegalArgumentException("Раствор с id=" + solutionId + " не найден");
    }

    int last = -1;
    for (int i = 1; i < tokens.length; i++) {
        if (tokens[i].equals("--last")) {
            if (i + 1 >= tokens.length) {
                throw new IllegalArgumentException("После --last нужно указать число");
            }
            try {
                last = Integer.parseInt(tokens[i + 1]);
                if (last <= 0) {
                    throw new IllegalArgumentException("Число после --last должно быть положительным");
                }
                i++;
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("После --last должно быть целое число");
            }
        } else {
            throw new IllegalArgumentException("Неизвестная опция: " + tokens[i]);
        }
    }

    List<Preparation> preparations = preparationManager.getLastPreparationsForSolution(solutionId, last);

    if (preparations.isEmpty()) {
        System.out.println("Для данного раствора нет приготовлений");
        return;
    }

    System.out.println("ID   FinalQty Unit Time                Comment");
    for (Preparation p : preparations) {
        String timeStr = "-";
        if (p.getPreparedAt() != null) {
            String full = p.getPreparedAt().toString().replace("T", " ");
            timeStr = full.length() >= 16 ? full.substring(0, 16) : full;
        }
        String comment = (p.getComment() != null && !p.getComment().isEmpty()) ? p.getComment() : "-";
        String unit = p.getFinalUnit().toString().toLowerCase();
        System.out.printf("%-4d %-8.0f %-4s %-19s %s%n",
                p.getId(),
                p.getFinalQuantity(),
                unit,
                timeStr,
                comment);
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

    private void prepUpdate(String args) {
    // Разбиваем аргументы команды
    String[] parts = args.split(" ", 2);

    // Проверяем, что есть хотя бы ID
    if (parts.length < 1 || parts[0].isEmpty()) {
        throw new IllegalArgumentException("Укажите ID приготовления");
    }

    // Парсим ID
    long id;
    try {
        id = Long.parseLong(parts[0]);
    } catch (NumberFormatException e) {
        throw new IllegalArgumentException("ID должен быть числом");
    }

    // Проверяем, есть ли поля для обновления
    if (parts.length < 2 || parts[1].trim().isEmpty()) {
        throw new IllegalArgumentException("Использование: prep_update <id> field=value ...");
    }

    Preparation preparation = preparationManager.getPreparation(id);
    if (preparation == null) {
        throw new IllegalArgumentException("Приготовление с id=" + id + " не найдено");
    }

    // Создаем временную копию для валидации с ВСЕМИ полями
    Preparation updatedPreparation = new Preparation(
            preparation.getId(),
            preparation.getSolutionId(),
            preparation.getCreatedAt(),
            preparation.getOwnerUsername()
    );

    // Копируем текущие значения
    updatedPreparation.setFinalQuantity(preparation.getFinalQuantity());
    updatedPreparation.setFinalUnit(preparation.getFinalUnit());
    updatedPreparation.setComment(preparation.getComment());

    // Парсим остальные аргументы с учетом кавычек
    String argsPart = parts[1].trim();
    List<String> assignments = parseArguments(argsPart);

    for (String assignment : assignments) {
        String[] keyValue = assignment.split("=", 2);
        if (keyValue.length != 2) {
            throw new IllegalArgumentException("Неверный формат: " + assignment + ". Ожидается field=value");
        }

        String field = keyValue[0].trim();
        String value = keyValue[1].trim();

        switch (field) {
            case "finalQuantity":
                if (value.isEmpty()) {
                    throw new IllegalArgumentException("finalQuantity не может быть пустым");
                }
                try {
                    updatedPreparation.setFinalQuantity(Double.parseDouble(value));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("finalQuantity должно быть числом");
                }
                break;

            case "finalUnit":
                if (value.isEmpty()) {
                    throw new IllegalArgumentException("finalUnit не может быть пустым");
                }
                try {
                    updatedPreparation.setFinalUnit(FinalQuantityUnit.valueOf(value.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Недопустимая единица измерения. Допустимые: ML, G");
                }
                break;

            case "comment":
                updatedPreparation.setComment(value.isEmpty() ? null : value);
                break;

            default:
                throw new IllegalArgumentException("Неизвестное поле: " + field +
                        ". Допустимые поля: finalQuantity, finalUnit, comment");
        }
    }

    // Валидируем обновленное приготовление
    try {
        PreparationValidator.validate(updatedPreparation);
    } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("Ошибка валидации: " + e.getMessage());
    }

    // Обновляем приготовление
    preparationManager.updatePreparation(
            id,
            updatedPreparation.getFinalQuantity(),
            updatedPreparation.getFinalUnit(),
            updatedPreparation.getComment()
    );

    System.out.println("Приготовление с id=" + id + " обновлено");
}

    // Метод для парсинга с кавычками
    private List<String> parseArguments(String args) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < args.length(); i++) {
            char c = args.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ' ' && !inQuotes) {
                if (current.length() > 0) {
                    result.add(current.toString());
                    current = new StringBuilder();
                }
            } else {
                current.append(c);
            }
        }

        if (current.length() > 0) {
            result.add(current.toString());
        }

        return result;
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

     //Сохранение  данных в файл
    private void Save(String args) {
        String filePath = args;
        if (filePath.isEmpty()) {
            if (currentFilePath != null && !currentFilePath.isEmpty()) {
                filePath = currentFilePath;
            } else {
                System.out.println("Укажите путь к файлу. Пример: save data.json");
                return;
            }
        }
        try {
            // Собираем все данные в LabData
            LabData data = new LabData(
                    solutionManager.getSolutions(),
                    preparationManager.getAllPreparationsMap(),
                    preparationComponentManager.getAllComponentsMap()
            );
            // Сохраняем
            fileStorage.save(data, filePath);
            currentFilePath = filePath;
            System.out.println("Данные сохранены в: " + filePath);

        } catch (IOException e) {
            System.out.println("Ошибка сохранения: " + e.getMessage());
        }
    }

     //Загрузка данных из файла
    private void Load(String args) {
        if (args.isEmpty()) {
            System.out.println("Укажите путь к файлу. Пример: load data.json");
            return;
        }

        try {
            // Загружаем из файла
            LabData loadedData = fileStorage.load(args);

            // Проверяем целостность
            List<String> errors = fileValidator.validate(loadedData);
            if (!errors.isEmpty()) {
                System.out.println("Ошибки в файле:");
                errors.forEach(err -> System.out.println("   • " + err));
                return;
            }

            // Загружаем данные в менеджеры
            if (loadedData.getSolutions() != null) {
                solutionManager.setAll(loadedData.getSolutions());
                solutionManager.updateNextId();
            }
            if (loadedData.getPreparations() != null) {
                preparationManager.setAll(loadedData.getPreparations());
                preparationManager.updateNextId();
            }
            if (loadedData.getComponents() != null) {
                preparationComponentManager.setAll(loadedData.getComponents());
                preparationComponentManager.updateNextId();
            }

            currentFilePath = args;
            System.out.println("Данные загружены из: " + args);

        } catch (IOException e) {
            System.out.println("Ошибка загрузки: " + e.getMessage());
        }
    }

    private void Exit() {
        System.out.println("Выход из программы...");
        boolean hasData = !solutionManager.getAll().isEmpty() ||
                !preparationManager.getAllPreparations().isEmpty() ||
                !preparationComponentManager.getAllComponents().isEmpty();

        if (!hasData) {
            System.out.println("Нет данных для сохранения");
            running = false;
            return;
        }
        System.out.print("Сохранить данные перед выходом? (yes/no): ");
        String answer = scanner.nextLine().trim().toLowerCase();

        if (!answer.equals("yes")) {
            System.out.println("Данные не сохранены");
            running = false;
            return;
        }
        if (currentFilePath != null && !currentFilePath.isEmpty()) {
            saveWithDefaultPath();
        } else {
            askForSavePath();
        }
        running = false;
    }

    private void saveWithDefaultPath() {
        System.out.print("Сохранить в " + currentFilePath + "? (yes/no): ");
        String useDefault = scanner.nextLine().trim().toLowerCase();
        if (useDefault.equals("yes")) {
            Save(currentFilePath);
        } else {
            askForSavePath();
        }
    }

    private void askForSavePath() {
        System.out.print("Введите путь для сохранения: ");
        String filePath = scanner.nextLine().trim();
        if (!filePath.isEmpty()) {
            Save(filePath);
        } else {
            System.out.println("Путь не указан, сохранение пропущено");
        }
    }
}


