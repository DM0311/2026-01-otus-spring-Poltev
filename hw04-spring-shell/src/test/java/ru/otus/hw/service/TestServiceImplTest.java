package ru.otus.hw.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;

import java.util.List;

import static org.mockito.Mockito.*;

@SpringBootTest(properties = "spring.shell.interactive.enabled=false")
public class TestServiceImplTest {

    @MockitoBean
    private LocalizedIOService ioService;

    @MockitoBean
    private QuestionDao questionDao;

    @Autowired
    private TestServiceImpl testService;

    @DisplayName("Should display 2 questions with answers")
    @Test
    void shouldPrintQuestionsAndAnswers() {

        List<Question> mockQuestions = createMockQuestions();
        when(questionDao.findAll()).thenReturn(mockQuestions);
        when(ioService.getMessage("Testservice.input.incorrect.number"))
                .thenReturn("Incorrect number");


        testService.executeTestFor(new Student("Ivan", "Ivanov"));

        InOrder inOrder = inOrder(ioService, questionDao);

        String expectedQuestion1 = "Is there life on Mars?" + System.lineSeparator() +
                "0. Science doesn't know this yet" + System.lineSeparator() +
                "1. Certainly. The red UFO is from Mars. And green is from Venus" + System.lineSeparator() +
                "2. Absolutely not" + System.lineSeparator();

        String expectedQuestion2 = "How should resources be loaded form jar in Java?" + System.lineSeparator() +
                "0. ClassLoader#getResourceAsStream or ClassPathResource#getInputStream" + System.lineSeparator() +
                "1. ClassLoader#geResource#getFile + FileReader" + System.lineSeparator() +
                "2. Wingardium Leviosa" + System.lineSeparator();


        inOrder.verify(ioService).printLine("");
        inOrder.verify(ioService).printLineLocalized("TestService.answer.the.questions");
        inOrder.verify(ioService).printLine("");
        inOrder.verify(questionDao).findAll();
        inOrder.verify(ioService).printLine(expectedQuestion1);
        inOrder.verify(ioService).getMessage("Testservice.input.incorrect.number");
        inOrder.verify(ioService).readIntForRange(0, 2, "Incorrect number");
        inOrder.verify(ioService).printLine(expectedQuestion2);
        inOrder.verify(ioService).getMessage("Testservice.input.incorrect.number");
        inOrder.verify(ioService).readIntForRange(0, 2, "Incorrect number");
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
