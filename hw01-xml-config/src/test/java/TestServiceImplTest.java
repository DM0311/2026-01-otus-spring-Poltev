import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.service.IOService;
import ru.otus.hw.service.TestServiceImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.mockito.Mockito.inOrder;

public class TestServiceImplTest {

    @Mock
    private IOService ioService;

    private TestServiceImpl testService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("Should display questions and answers via IOService")
    @Test
    void shouldPrintQuestionsAndAnswers() throws IOException {
        Path csvFile = tempDir.resolve("questions.csv");
        List<String> lines = List.of(
                "Is there life on Mars?;Science doesn't know this yet%true|Certainly. The red UFO is from Mars. " +
                        "And green is from Venus%false|Absolutely not%false"
        );
        Files.write(csvFile, lines);
        TestFileNameProvider provider = new AppProperties(csvFile.getFileName().toString());
        CsvQuestionDao questionDao = new CsvQuestionDao(provider);
        testService = new TestServiceImpl(ioService,questionDao);
        testService.executeTest();

        InOrder inOrder = inOrder(ioService);

        inOrder.verify(ioService).printLine("");
        inOrder.verify(ioService).printFormattedLine("Please answer the questions below%n");

        inOrder.verify(ioService).printFormattedLine("Is there life on Mars?");
        inOrder.verify(ioService).printFormattedLine("%d. Science doesn't know this yet", 0);
        inOrder.verify(ioService).printFormattedLine("%d. Certainly. The red UFO is from Mars. And green is from Venus", 1);
        inOrder.verify(ioService).printFormattedLine("%d. Absolutely not", 2);

    }
}
