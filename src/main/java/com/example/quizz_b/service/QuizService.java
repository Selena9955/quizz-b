package com.example.quizz_b.service;

import com.example.quizz_b.constant.enums.QuizType;
import com.example.quizz_b.factory.quiz.QuizDtoFactory;
import com.example.quizz_b.factory.quiz.QuizHandlerFactory;
import com.example.quizz_b.model.dto.QuizDto;
import com.example.quizz_b.model.dto.QuizListDto;
import com.example.quizz_b.model.dto.QuizSubmitRequestDto;
import com.example.quizz_b.model.entity.*;
import com.example.quizz_b.repository.QuizRepository;
import com.example.quizz_b.repository.UserRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class QuizService {
    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagService tagService;

    @Autowired
    private QuizHandlerFactory factory;

    @Transactional
    public void create(QuizSubmitRequestDto dto, User user) {
        QuizType type = QuizType.fromValue(dto.getQuizType());

        // 驗證
        factory.getHandler(type).validate(dto);



        Set<Tag> tags = tagService.getOrCreateTags(dto.getTags());
        Quiz quiz = new Quiz();
        quiz.setAuthor(user);
        quiz.setQuizType(type);
        quiz.setTitle(dto.getTitle());
        quiz.setTitleDetail(dto.getTitleDetail());
        quiz.setSingleAnswerId(dto.getSingleAnswerId());
        quiz.setMultipleAnswerId(new HashSet<>(dto.getMultipleAnswerId()));
        quiz.setFlashAnswer(dto.getFlashAnswer());
        quiz.setTags(tags);
        quiz.setAnswerDetail(dto.getAnswerDetail());

        List<QuizOption> options = dto.getOptions();
        for (QuizOption option : options) {
            option.setQuiz(quiz); // 設定每個選項的 quiz 外鍵
        }
        quiz.setOptions(options);

        quizRepository.save(quiz);
    }

    @Transactional
    public List<QuizListDto> getAllQuizzes() {
        List<Quiz> quizzes = quizRepository.findAllByIsDeleteFalseOrderByCreateTimeDesc();
        return quizzes.stream().map(this::convertToListDTO).toList();
    }

    @Transactional
    public QuizDto getQuizById(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("查無此題"));

        // 延遲加載：等到真的需要才抓
        if (quiz.getQuizType() == QuizType.MULTIPLE) {
            quiz.getMultipleAnswerId().size();
        }

        // lazy 的其他欄位照需求決定要不要 init
        Hibernate.initialize(quiz.getOptions());
        Hibernate.initialize(quiz.getTags());
        Hibernate.initialize(quiz.getAuthor());

        return QuizDtoFactory.fromEntity(quiz);
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

    public void softDeleteQuizById(Long quizId, Long userId) throws AccessDeniedException {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz 不存在"));

        // TODO: 支援管理員可跳過作者驗證邏輯，允許硬刪除
        if (!quiz.getAuthor().getId().equals(userId)) {
            throw new AccessDeniedException("無權限刪除該資料");
        }
        quiz.setDelete(true);
        quizRepository.save(quiz);
    }

    @Transactional
    public List<QuizListDto> getQuizzesByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        List<Quiz> quizzes = quizRepository.findByAuthorId(user.getId());
        return quizzes.stream().map(this::convertToListDTO).toList();
    }
}
