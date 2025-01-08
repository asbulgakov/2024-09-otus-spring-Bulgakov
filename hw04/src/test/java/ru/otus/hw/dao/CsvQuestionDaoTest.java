package ru.otus.hw.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = CsvQuestionDao.class)
public class CsvQuestionDaoTest {

    @MockBean
    private TestFileNameProvider fileNameProvider;

    @Autowired
    private CsvQuestionDao csvQuestionDao;


    @Test
    void testFindAllWhenCsvFileReadSuccessfullyThenReturnQuestions() {
        when(fileNameProvider.getTestFileName()).thenReturn("questions.csv");

        List<Question> questions = csvQuestionDao.findAll();

        assertThat(questions).hasSize(8);
        assertThat(questions.get(0).text()).isEqualTo("Is there life on Mars?");
        assertThat(questions.get(1).text()).isEqualTo("How should resources be loaded form jar in Java?");
    }

    @Test
    void testFindAllWhenCsvFileNotFoundThenThrowQuestionReadException() {
        when(fileNameProvider.getTestFileName()).thenReturn("/non-existent-file.csv");

        assertThatThrownBy(() -> csvQuestionDao.findAll())
                .isInstanceOf(QuestionReadException.class)
                .hasMessageContaining("Failed to read questions from CSV file");
    }

    @Test
    void testFindAllWhenCsvFileIsEmptyThenReturnEmptyList() {
        when(fileNameProvider.getTestFileName()).thenReturn("empty-questions.csv");

        List<Question> questions = csvQuestionDao.findAll();

        assertThat(questions).isEmpty();
    }
}