package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;
import java.util.stream.IntStream;

@Primary
@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        questions.forEach(question -> {
            ioService.printLine(question.text());
            var answers = getValidatedAnswersList(question);

            printAnswerOptions(question, answers);

            handleUserInput(question, answers, testResult);
        });

        return testResult;
    }

    private List<Answer> getValidatedAnswersList(Question question) {
        return question.answers() != null ? question.answers() : List.of();
    }

    private void printAnswerOptions(Question question, List<?> answers) {
        IntStream.range(0, answers.size())
                .forEach(i ->
                        ioService.printFormattedLine(
                                "%d. %s", i + 1,
                                question.answers().get(i).text()
                        )
                );
    }

    private void handleUserInput(Question question, List<Answer> answers, TestResult testResult) {
        if (!answers.isEmpty()) {
            int size = question.answers().size();
            String errorMessage = "Invalid input. Please enter a number between 1 and " + size;
            String prompt = "Your answer: ";
            int answerIndex =
                    ioService.readIntForRangeWithPrompt(1, size, prompt, errorMessage);
            var isAnswerValid = answers.get(answerIndex - 1).isCorrect(); // Задать вопрос, получить ответ
            testResult.applyAnswer(question, isAnswerValid);
        }
    }
}
