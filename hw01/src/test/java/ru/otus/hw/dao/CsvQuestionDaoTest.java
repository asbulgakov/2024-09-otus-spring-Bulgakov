package ru.otus.hw.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.ArrayList;
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
    void testFindAllWhenCsvFileReadSuccessfullyThenReturnQuestions() throws Exception {
        String fileName = "/test-questions.csv";
        when(fileNameProvider.testFileName()).thenReturn(fileName);

        List<QuestionDto> questionDtos = new ArrayList<>();
        QuestionDto questionDto1 = new QuestionDto();
        questionDto1.setText("Question 1");
        questionDto1.setAnswers(List.of(new Answer("Answer 1", true)));
        questionDtos.add(questionDto1);

        QuestionDto questionDto2 = new QuestionDto();
        questionDto2.setText("Question 2");
        questionDto2.setAnswers(List.of(new Answer("Answer 2", false)));
        questionDtos.add(questionDto2);

        List<Question> questions = csvQuestionDao.findAll();

        assertThat(questions).hasSize(2);
        assertThat(questions.get(0).text()).isEqualTo("Question 1");
        assertThat(questions.get(1).text()).isEqualTo("Question 2");
    }

    @Test
    void testFindAllWhenCsvFileNotFoundThenThrowQuestionReadException() {
        String fileName = "/non-existent-file.csv";
        when(fileNameProvider.testFileName()).thenReturn(fileName);

        assertThatThrownBy(() -> csvQuestionDao.findAll())
                .isInstanceOf(QuestionReadException.class)
                .hasMessageContaining("Failed to read questions from CSV file");
    }

    @Test
    void testFindAllWhenCsvFileIsEmptyThenReturnEmptyList() throws Exception {
        String fileName = "/empty-questions.csv";
        when(fileNameProvider.testFileName()).thenReturn(fileName);

        List<Question> questions = csvQuestionDao.findAll();

        assertThat(questions).isEmpty();
    }
}