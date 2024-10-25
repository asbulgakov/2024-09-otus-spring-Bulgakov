package ru.otus.hw.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;
import ru.otus.hw.service.IOService;
import ru.otus.hw.service.TestServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TestServiceImplTest {

    private IOService ioService;
    private QuestionDao questionDao;
    private TestServiceImpl testService;

    @BeforeEach
    public void setUp() {
        ioService = mock(IOService.class);
        questionDao = mock(QuestionDao.class);
        testService = new TestServiceImpl(ioService, questionDao);
    }

    @Test
    public void testExecuteTestFor() {
        Student student = new Student("John", "Doe");
        List<Question> questions = List.of(
                new Question("What is 2+2?", List.of(
                        new Answer("4", true),
                        new Answer("5", false)
                ))
        );

        when(questionDao.findAll()).thenReturn(questions);
        when(ioService.readIntForRangeWithPrompt(anyInt(), anyInt(), anyString(), anyString())).thenReturn(1);

        TestResult result = testService.executeTestFor(student);

        assertEquals(1, result.getRightAnswersCount());
        assertEquals(1, result.getAnsweredQuestions().size());
        assertEquals("What is 2+2?", result.getAnsweredQuestions().get(0).text());

        verify(ioService).printLine("");
        verify(ioService).printFormattedLine("Please answer the questions below%n");
        verify(ioService).printLine("What is 2+2?");
        verify(ioService).printFormattedLine("%d. %s", 1, "4");
        verify(ioService).printFormattedLine("%d. %s", 2, "5");
        verify(ioService).readIntForRangeWithPrompt(1, 2, "Your answer: ", "Invalid input. Please enter a number between 1 and 2");
    }
}