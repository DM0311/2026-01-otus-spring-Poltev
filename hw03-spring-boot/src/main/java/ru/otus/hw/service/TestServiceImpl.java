package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final LocalizedIOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printLineLocalized("TestService.answer.the.questions");
        ioService.printLine("");

        var questions = questionDao.findAll();
        return executeSurvey(questions, student);
    }

    private TestResult executeSurvey(List<Question> questions, Student student) {
        var testResult = new TestResult(student);
        for (var question : questions) {
            var output = toFormattedString(question);
            ioService.printLine(output);
            var answerNum = ioService.readIntForRange(0, question.answers().size() - 1, "Incorrect number");
            var isAnswerValid = question.answers().get(answerNum).isCorrect();
            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }

    private String toFormattedString(Question question) {
        StringBuilder builder = new StringBuilder(question.text());
        builder.append(System.lineSeparator());
        for (int i = 0; i < question.answers().size(); i++) {
            builder.append(String.format("%d. %s%n", i, question.answers().get(i).text()));
        }
        return builder.toString();
    }

}
