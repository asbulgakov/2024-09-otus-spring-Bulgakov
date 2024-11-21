package ru.otus.hw.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CsvQuestionDaoTest {

    @Mock
    private TestFileNameProvider fileNameProvider;

    @InjectMocks
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
        String fileName = "/non-existent-file.csv";
        when(fileNameProvider.getTestFileName()).thenReturn(fileName);

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