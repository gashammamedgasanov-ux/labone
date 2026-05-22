package org.example;

import cli.ComandLineInterface;
import manager.*;
import storage.FileStorage;
import storage.LabData;
import validation.FileValidator;

import java.io.IOException;
import java.util.List;

public class App {
    public static void main(String[] args) {
        // Создаем менеджеры
        SolutionManager solutionManager = new SolutionManager(null);
        PreparationManager preparationManager = new PreparationManager(null);
        BatchService batchService = new BatchService();
        PreparationComponentManager componentManager = new PreparationComponentManager(
                preparationManager, batchService
        );

        if (args.length > 0) {
            String filePath = args[0];
            System.out.println("Загрузка данных из файла: " + filePath);

            try {
                FileStorage fileStorage = new FileStorage();
                FileValidator fileValidator = new FileValidator();


                LabData loadedData = fileStorage.load(filePath);

                List<String> errors = fileValidator.validate(loadedData);
                if (!errors.isEmpty()) {
                    System.out.println("Ошибки в файле:");
                    for (String error : errors) {
                        System.out.println("   • " + error);
                    }
                    System.out.println("Загрузка отменена, запускаем с пустыми данными");
                } else {
                    // Загружаем растворы
                    if (loadedData.getSolutions() != null) {
                        solutionManager.setAll(loadedData.getSolutions());
                        solutionManager.updateNextId();
                    }
                    // Загружаем приготовления
                    if (loadedData.getPreparations() != null) {
                        preparationManager.setAll(loadedData.getPreparations());
                        preparationManager.updateNextId();
                    }
                    // Загружаем компоненты
                    if (loadedData.getComponents() != null) {
                        componentManager.setAll(loadedData.getComponents());
                        componentManager.updateNextId();
                    }
                    System.out.println("Данные успешно загружены");
                }
            } catch (IOException e) {
                System.out.println("Ошибка загрузки: " + e.getMessage());
                System.out.println("Запускаем с пустыми данными");
            }
        } else {
            System.out.println("ℹ️Для загрузки данных при старте укажите путь к файлу:");
            System.out.println("   ./gradlew run --args=\"data.json\"");
        }

        // Запускаем CLI
        ComandLineInterface cli = new ComandLineInterface(
                preparationManager,
                componentManager,
                solutionManager
                );
        cli.start();
    }
}