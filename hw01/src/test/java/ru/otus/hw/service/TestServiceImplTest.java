package ru.otus.hw.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestServiceImplTest {

    @Mock
    private IOService ioService;

    @Mock
    private CsvQuestionDao csvQuestionDao;

    @InjectMocks
    private TestServiceImpl testService;

    @Test
    void testExecuteTestWhenQuestionsWithAnswersThenPrintQuestionsAndAnswers() {
        List<Answer> answers = List.of(
                new Answer("Answer 1", true),
                new Answer("Answer 2", false)
        );
        List<Question> questions = List.of(
                new Question("Question 1", answers)
        );

        when(csvQuestionDao.findAll()).thenReturn(questions);

        testService.executeTest();

        verify(ioService, times(2)).printLine(""); // Expecting two invocations
        verify(ioService).printFormattedLine("Please answer the questions below%n");
        verify(ioService).printLine("Question 1");
        verify(ioService).printFormattedLine("%d. %s", 1, "Answer 1");
        verify(ioService).printFormattedLine("%d. %s", 2, "Answer 2");
    }

    @Test
    void testExecuteTestWhenQuestionsWithoutAnswersThenPrintQuestions() {
        List<Question> questions = List.of(
                new Question("Question 1", List.of())
        );

        when(csvQuestionDao.findAll()).thenReturn(questions);

        testService.executeTest();

        verify(ioService, times(2)).printLine("");
        verify(ioService).printFormattedLine("Please answer the questions below%n");
        verify(ioService).printLine("Question 1");
        verify(ioService, never()).printFormattedLine(anyString(), anyInt(), anyString());
    }
}