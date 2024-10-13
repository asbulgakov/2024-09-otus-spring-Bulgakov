package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Question;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        printWelcomeMessage();
        // Получить вопросы из дао и вывести их с вариантами ответов
        var questions = questionDao.findAll();
        for (Question question : questions) {
            printQuestion(question);
        }
    }

    private void printWelcomeMessage() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
    }

    private void printQuestion(Question question) {
        ioService.printLine(question.text());
        if (question.answers() != null && !question.answers().isEmpty()) {
            printAnswers(question);
        }
        ioService.printLine("");
    }

    private void printAnswers(Question question) {
        for (int i = 0; i < question.answers().size(); i++) {
            ioService.printFormattedLine("%d. %s", i + 1, question.answers().get(i).text());
        }
    }
}
