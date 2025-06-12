package com.example.quizz_b.service;

import com.example.quizz_b.constant.enums.QuizType;
import com.example.quizz_b.factory.quiz.QuizHandlerFactory;
import com.example.quizz_b.model.dto.QuizListDto;
import com.example.quizz_b.model.dto.QuizSubmitRequestDto;
import com.example.quizz_b.model.entity.Quiz;
import com.example.quizz_b.model.entity.Tag;
import com.example.quizz_b.model.entity.User;
import com.example.quizz_b.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class QuizService {
    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private TagService tagService;

    @Autowired
    private QuizHandlerFactory factory;

    @Transactional
    public void create(QuizSubmitRequestDto dto, User user) {
        QuizType type = QuizType.fromValue(dto.getQuizType());
        System.out.println(dto.getTags());
        Set<Tag> tags = tagService.getOrCreateTags(dto.getTags());

        // 驗證
        factory.getHandler(type).validate(dto);

        Quiz quiz = new Quiz();
        quiz.setAuthor(user);
        quiz.setQuizType(type);
        quiz.setTitle(dto.getTitle());
        quiz.setTitleDetail(dto.getTitleDetail());
        quiz.setOptions(dto.getOptions());
        quiz.setSingleAnswerId(dto.getSingleAnswerId());
        quiz.setMultipleAnswerId(dto.getMultipleAnswerId());
        quiz.setFlashAnswer(dto.getFlashAnswer());
        quiz.setTags(tags);

        quizRepository.save(quiz);
    }

    @Transactional
    public List<QuizListDto> getAllQuizzes() {
        List<Quiz> quizzes = quizRepository.findAllByOrderByCreateTimeDesc();
        return quizzes.stream().map(this::convertToListDTO).toList();
    }

    public QuizListDto convertToListDTO(Quiz quiz) {
        QuizListDto dto = new QuizListDto();
        dto.setId(quiz.getId());
        dto.setAuthorName(quiz.getAuthor().getUsername());
        dto.setQuizType(quiz.getQuizType().ordinal());
        dto.setTitle(quiz.getTitle());

        List<String> tagNames = quiz.getTags()
                .stream()
                .map(Tag::getName)
                .toList();
        dto.setTags(tagNames);

        return dto;
    }
}
