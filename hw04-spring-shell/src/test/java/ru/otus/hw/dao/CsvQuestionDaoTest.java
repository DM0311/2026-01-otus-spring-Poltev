package ru.otus.hw.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = "spring.shell.interactive.enabled=false")
public class CsvQuestionDaoTest {

    @MockitoBean
    private AppProperties properties;

    @Autowired
    private CsvQuestionDao dao;

    @BeforeEach
    void setUp() {
        when(properties.getTestFileName()).thenReturn("test-questions.csv");
    }

    @Test
    void shouldReadAllQuestionsFromCsvFile() {
 
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
