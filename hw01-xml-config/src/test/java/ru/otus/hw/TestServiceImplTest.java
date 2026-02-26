package ru.otus.hw;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.service.IOService;
import ru.otus.hw.service.TestServiceImpl;

import java.io.IOException;
import java.util.List;

import static org.mockito.Mockito.*;


public class TestServiceImplTest {

    @Mock
    private IOService ioService;

    @Mock
    private QuestionDao questionDao;

   private TestServiceImpl testService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testService = new TestServiceImpl(ioService,questionDao);
    }

    @DisplayName("Should display 2 questions with answers")
    @Test
    void shouldPrintQuestionsAndAnswers() {

        List<Question> mockQuestions = createMockQuestions();
        when(questionDao.findAll()).thenReturn(mockQuestions);

        testService.executeTest();

        InOrder inOrder = inOrder(ioService, questionDao);

        inOrder.verify(ioService).printLine("");
        inOrder.verify(ioService).printFormattedLine("Please answer the questions below%n");
        inOrder.verify(questionDao).findAll();

        // q1
        inOrder.verify(ioService).printFormattedLine("Is there life on Mars?");
        inOrder.verify(ioService).printFormattedLine("%d. Science doesn't know this yet", 0);
        inOrder.verify(ioService).printFormattedLine("%d. Certainly. The red UFO is from Mars. And green is from Venus", 1);
        inOrder.verify(ioService).printFormattedLine("%d. Absolutely not", 2);

        // q2
        inOrder.verify(ioService).printFormattedLine("How should resources be loaded form jar in Java?");
        inOrder.verify(ioService).printFormattedLine("%d. ClassLoader#getResourceAsStream or ClassPathResource#getInputStream", 0);
        inOrder.verify(ioService).printFormattedLine("%d. ClassLoader#geResource#getFile + FileReader", 1);
        inOrder.verify(ioService).printFormattedLine("%d. Wingardium Leviosa", 2);

        verifyNoMoreInteractions(ioService, questionDao);

    }

    private List<Question> createMockQuestions() {

        var q1 = new Question("Is there life on Mars?",List.of(
                new Answer("Science doesn't know this yet",true),
                new Answer("Certainly. The red UFO is from Mars. And green is from Venus",false),
                new Answer("Absolutely not",false)
        ));

        var q2 = new Question("How should resources be loaded form jar in Java?",List.of(
                new Answer("ClassLoader#getResourceAsStream or ClassPathResource#getInputStream",true),
                new Answer("ClassLoader#geResource#getFile + FileReader",false),
                new Answer("Wingardium Leviosa",false)
        ));
        return List.of(q1,q2);
    }
}
