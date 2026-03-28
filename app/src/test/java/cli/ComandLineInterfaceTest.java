package cli;

import domain.entity.Preparation;
import domain.entity.PreparationComponent;
import domain.entity.Solution;
import domain.enums.ComponentUnit;
import domain.enums.FinalQuantityUnit;
import domain.enums.SolutionConcentrationUnit;
import manager.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;//строка как ввод
import java.io.ByteArrayOutputStream;//ловим вывод в буфере
import java.io.InputStream;//родительские классы для потоков
import java.io.PrintStream;//родительские классы для потоков
import java.time.Instant;//время
import java.util.List;//коллекция

import static org.assertj.core.api.Assertions.assertThat;//Статический импорт assertThat из AssertJ

class ComandLineInterfaceTest {
    private SolutionManager solutionManager;
    private PreparationManager preparationManager;
    private PreparationComponentManager componentManager;
    private ByteArrayOutputStream outContent;//буфер, куда будет перенаправлен System.out, чтобы мы могли прочитать, что программа вывела
    private PrintStream originalOut;
    private InputStream originalIn;

    @BeforeEach
    void setUp() {
        solutionManager = new SolutionManager();
        preparationManager = new PreparationManager();
        BatchService batchService = new BatchService();
        componentManager = new PreparationComponentManager(preparationManager, batchService);

        //сохраняем оригинальные потоки в переменные, чтобы потом восстановить
        originalOut = System.out;
        originalIn = System.in;

        //создаём новый буфер и перенаправляем System.out в него, вывод программы будет писаться в outContent
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    //восстановление после каждого теста
    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    //возвращаем новый экземпляр ComandLineInterface с текущими менеджерами (которые созданы в setUp()). так мы можем в каждом тесте получать свежий CLI.
    private ComandLineInterface createCli() {
        return new ComandLineInterface(preparationManager, componentManager, solutionManager);
    }

    @Test
    void helpCommandPrintsHelp() {
        ComandLineInterface cli = createCli();
        cli.processCommand("help");
        String output = outContent.toString();//Берём всё, что программа вывела в System.out, из буфера outContent и превращаем в строку.
        assertThat(output).contains("Доступные команды:")//начинаем проверку строки, проверяем, что строка output содержит эту подстроку.
                .contains("sol_list")//проверяет, что содержит sol_list
                .contains("prep_add");//проверяет, что содержит prep_add
    }

    //выполняем несуществующую команду и проверяем, что есть сообщение об ошибке
    @Test
    void unknownCommandShowsError() {
        ComandLineInterface cli = createCli();
        cli.processCommand("unknown");
        assertThat(outContent.toString()).contains("Такой команды нет");
    }

    //когда коллекция растворов пуста
    @Test
    void solListWhenEmptyShowsMessage() {
        ComandLineInterface cli = createCli();
        cli.processCommand("sol_list");
        assertThat(outContent.toString()).contains("Нет растворов, добавьте растворы с помощью команды sol_add");
    }

    //эмуляция интерактивного ввода
    @Test
    void solAddInteractiveCreatesSolution() {
        String input = "TestSolution\n1.0\nPERCENT\nwater\nuser\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));//Подменяем System.in на поток, который читает из строки input. Теперь, когда программа вызовет scanner.nextLine(), она получит эти строки.
        ComandLineInterface cli = createCli();//Создаём CLI и выполняем команду sol_add. Программа начнёт задавать вопросы и читать ответы из подменённого System.in.
        cli.processCommand("sol_add");
        assertThat(solutionManager.getAll()).hasSize(1);//Проверяем, что в менеджере растворов появился ровно один раствор.
        Solution sol = solutionManager.getAll().values().iterator().next();//Получаем этот раствор и проверяем, что его название и концентрация соответствуют введённым.
        assertThat(sol.getName()).isEqualTo("TestSolution");
        assertThat(sol.getConcentration()).isEqualTo(1.0);
    }

    //несуществующий id
    @Test
    void solShowNotFoundShowsError() {
        ComandLineInterface cli = createCli();
        cli.processCommand("sol_show 999");
        assertThat(outContent.toString()).contains("Раствор с id=999 не найден");
    }

    //вывод данных для существующего раствора
    @Test
    void solShowValidShowsInfo() {
        Solution sol = solutionManager.addSolution("NaCl", 0.9, SolutionConcentrationUnit.PERCENT, "water", "user");
        ComandLineInterface cli = createCli();
        cli.processCommand("sol_show " + sol.getId());
        String output = outContent.toString();
        assertThat(output).contains("NaCl")
                .contains("0.9")
                .contains("%")
                .contains("water");
    }

    //проверяем по --q
    @Test
    void solListWithQueryFilters() {
        solutionManager.addSolution("NaCl", 0.9, SolutionConcentrationUnit.PERCENT, "water", "user");
        solutionManager.addSolution("KCl", 1.0, SolutionConcentrationUnit.PERCENT, "water", "user");
        ComandLineInterface cli = createCli();
        cli.processCommand("sol_list --q NaCl");
        String output = outContent.toString();
        assertThat(output).contains("NaCl")
                .doesNotContain("KCl");
    }

    //эмулируем создание приготавления
    @Test
    void prepAddInteractiveCreatesPreparation() {
        Solution sol = solutionManager.addSolution("NaCl", 0.9, SolutionConcentrationUnit.PERCENT, "water", "user");
        String input = "100\nML\nuser\ncomment\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        ComandLineInterface cli = createCli();
        cli.processCommand("prep_add " + sol.getId());
        assertThat(preparationManager.getAllPreparations()).hasSize(1);
        Preparation prep = preparationManager.getAllPreparations().iterator().next();
        assertThat(prep.getFinalQuantity()).isEqualTo(100.0);
        assertThat(prep.getFinalUnit()).isEqualTo(FinalQuantityUnit.ML);
        assertThat(prep.getComment()).isEqualTo("comment");
    }

    //проверяет prep_list с --last
    @Test
    void prepListShowsPreparations() {
        Solution sol = solutionManager.addSolution("NaCl", 0.9, SolutionConcentrationUnit.PERCENT, "water", "user");
        preparationManager.addPreparation(sol.getId(), 100, FinalQuantityUnit.ML, "comment1", "user", Instant.now());
        preparationManager.addPreparation(sol.getId(), 200, FinalQuantityUnit.ML, "comment2", "user", Instant.now());
        ComandLineInterface cli = createCli();
        cli.processCommand("prep_list " + sol.getId() + " --last 1");
        String output = outContent.toString();
        assertThat(output).contains("comment2");
        assertThat(output).doesNotContain("comment1");
    }

    //эмулируем добавлеие компонента
    @Test
    void compAddInteractiveCreatesComponent() throws IllegalAccessException {
        Solution sol = solutionManager.addSolution("NaCl", 0.9, SolutionConcentrationUnit.PERCENT, "water", "user");
        Preparation prep = preparationManager.addPreparation(sol.getId(), 100, FinalQuantityUnit.ML, "prep", "user", Instant.now());
        String input = "55\n10\nG\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        ComandLineInterface cli = createCli();
        cli.processCommand("comp_add " + prep.getId());
        List<PreparationComponent> comps = componentManager.getComponentsForPreparation(prep.getId());
        assertThat(comps).hasSize(1);
        PreparationComponent comp = comps.get(0);
        assertThat(comp.getBatchId()).isEqualTo(55);
        assertThat(comp.getQuantity()).isEqualTo(10.0);
        assertThat(comp.getUnit()).isEqualTo(ComponentUnit.G);
    }
}