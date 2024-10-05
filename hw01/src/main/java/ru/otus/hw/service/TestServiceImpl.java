package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.domain.Question;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final CsvQuestionDao csvQuestionDao;

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        // Получить вопросы из дао и вывести их с вариантами ответов
        var questions = csvQuestionDao.findAll();
        for (Question question : questions) {
            ioService.printLine(question.text());
            if (question.answers() != null && !question.answers().isEmpty()) {
                for (int i = 0; i < question.answers().size(); i++) {
                    ioService.printFormattedLine("%d. %s", i + 1, question.answers().get(i).text());
                }
            }
            ioService.printLine("");
        }
    }
}
