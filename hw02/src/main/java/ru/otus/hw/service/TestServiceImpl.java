package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;

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

        processQuestion(questions, testResult);

        return testResult;
    }

    private void processQuestion(List<Question> questions, TestResult testResult) {
        questions.forEach(question -> {
            ioService.printLine(question.text());
            var answers = question.answers();

            printAnswerOptions(answers);

            boolean isAnswerValid = isAnswerCorrect(question, answers);

            testResult.applyAnswer(question, isAnswerValid);
        });
    }

    private void printAnswerOptions(List<Answer> answers) {
        for (int i = 0; i < answers.size(); i++) {
            ioService.printFormattedLine("%d. %s", i + 1, answers.get(i).text());
        }
    }

    private boolean isAnswerCorrect(Question question, List<Answer> answers) {
        if (answers.isEmpty()) {
            return false;
        }

        int size = question.answers().size();
        String errorMessage = "Invalid input. Please enter a number between 1 and " + size;
        String prompt = "Your answer: ";
        int answerIndex =
                ioService.readIntForRangeWithPrompt(1, size, prompt, errorMessage);

        return answers.get(answerIndex - 1).isCorrect(); // Задать вопрос, получить ответ
    }
}
