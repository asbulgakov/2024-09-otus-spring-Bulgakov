package ru.otus.hw.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestServiceImplTest {

    @Mock
    private LocalizedIOService ioService;

    @Mock
    private QuestionDao questionDao;

    @InjectMocks
    private TestServiceImpl testService;

    @Test
    void testExecuteTestForWhenQuestionsAreReturnedThenCorrectMethodsAreCalled() {
        Student student = new Student("John", "Doe");
        Question question1 = new Question("Question 1", List.of(new Answer("Answer 1", true), new Answer("Answer 2", false)));
        Question question2 = new Question("Question 2", List.of(new Answer("Answer 1", false), new Answer("Answer 2", true)));
        List<Question> questions = List.of(question1, question2);

        when(questionDao.findAll()).thenReturn(questions);
        when(ioService.readIntForRangeWithPrompt(1, 2, "Your answer: ", "Invalid input. Please enter a number between 1 and 2"))
                .thenReturn(2, 2);

        TestResult result = testService.executeTestFor(student);

        verify(ioService, times(2)).printLine("");
        verify(ioService, times(1)).printLineLocalized("TestService.answer.the.questions");
        verify(questionDao, times(1)).findAll();
        verify(ioService, times(1)).printLine("Question 1");
        verify(ioService, times(1)).printLine("Question 2");
        verify(ioService, times(4)).printFormattedLine(anyString(), anyInt(), anyString());
        verify(ioService, times(2)).readIntForRangeWithPrompt(1, 2, "Your answer: ", "Invalid input. Please enter a number between 1 and 2");

        assertEquals(1, result.getRightAnswersCount());
        assertEquals(2, result.getAnsweredQuestions().size());
    }

    @Test
    void testExecuteTestForWhenNoQuestionsAreReturnedThenCorrectMethodsAreCalled() {
        Student student = new Student("John", "Doe");
        List<Question> questions = List.of();

        when(questionDao.findAll()).thenReturn(questions);

        TestResult result = testService.executeTestFor(student);

        verify(ioService, times(2)).printLine("");
        verify(ioService, times(1)).printLineLocalized("TestService.answer.the.questions");
        verify(questionDao, times(1)).findAll();

        assertEquals(0, result.getRightAnswersCount());
        assertEquals(0, result.getAnsweredQuestions().size());
    }
}