package ru.otus.hw.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

public class CsvQuestionDaoTest {
    @Mock
    private TestFileNameProvider fileNameProvider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReadAllQuestionsFromCsvFile() {

        when(fileNameProvider.getTestFileName()).thenReturn("test-questions.csv");
        CsvQuestionDao dao = new CsvQuestionDao(fileNameProvider);

        List<Question> questions = dao.findAll();

        assertThat(questions).hasSize(2);

        Question first = questions.get(0);
        assertThat(first.text()).isEqualTo("Is there life on Mars?");
        assertThat(first.answers()).containsExactly(
                new Answer("Science doesn't know this yet", true),
                new Answer("Certainly. The red UFO is from Mars. And green is from Venus", false),
                new Answer("Absolutely not", false)
        );

        Question second = questions.get(1);
        assertThat(second.text()).isEqualTo("How should resources be loaded form jar in Java?");
        assertThat(second.answers()).containsExactly(
                new Answer("ClassLoader#getResourceAsStream or ClassPathResource#getInputStream", true),
                new Answer("ClassLoader#geResource#getFile + FileReader", false),
                new Answer("Wingardium Leviosa", false)
        );
    }
}
