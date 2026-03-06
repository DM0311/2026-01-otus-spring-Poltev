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
        testService = new TestServiceImpl(ioService, questionDao);
    }

    @DisplayName("Should display 2 questions with answers")
    @Test
    void shouldPrintQuestionsAndAnswers() {

        List<Question> mockQuestions = createMockQuestions();
        when(questionDao.findAll()).thenReturn(mockQuestions);

        testService.executeTest();

        InOrder inOrder = inOrder(ioService, questionDao);

        String expected1 = "Is there life on Mars?" + System.lineSeparator() +
                "0. Science doesn't know this yet" + System.lineSeparator() +
                "1. Certainly. The red UFO is from Mars. And green is from Venus" + System.lineSeparator() +
                "2. Absolutely not" + System.lineSeparator();

        String expected2 = "How should resources be loaded form jar in Java?" + System.lineSeparator() +
                "0. ClassLoader#getResourceAsStream or ClassPathResource#getInputStream" + System.lineSeparator() +
                "1. ClassLoader#geResource#getFile + FileReader" + System.lineSeparator() +
                "2. Wingardium Leviosa" + System.lineSeparator();

        inOrder.verify(ioService).printLine("");
        inOrder.verify(ioService).printFormattedLine("Please answer the questions below%n");
        inOrder.verify(questionDao).findAll();
        inOrder.verify(ioService).printLine(expected1);
        inOrder.verify(ioService).printLine(expected2);

        verifyNoMoreInteractions(ioService, questionDao);

    }

    private List<Question> createMockQuestions() {

        var q1 = new Question("Is there life on Mars?", List.of(
                new Answer("Science doesn't know this yet", true),
                new Answer("Certainly. The red UFO is from Mars. And green is from Venus", false),
                new Answer("Absolutely not", false)
        ));

        var q2 = new Question("How should resources be loaded form jar in Java?", List.of(
                new Answer("ClassLoader#getResourceAsStream or ClassPathResource#getInputStream", true),
                new Answer("ClassLoader#geResource#getFile + FileReader", false),
                new Answer("Wingardium Leviosa", false)
        ));
        return List.of(q1, q2);
    }
}
