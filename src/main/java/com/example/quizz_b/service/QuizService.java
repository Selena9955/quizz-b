package com.example.quizz_b.service;

import com.example.quizz_b.constant.enums.QuizType;
import com.example.quizz_b.constant.enums.TagUsageType;
import com.example.quizz_b.factory.quiz.QuizDtoFactory;
import com.example.quizz_b.factory.quiz.QuizHandlerFactory;
import com.example.quizz_b.model.dto.*;
import com.example.quizz_b.model.entity.*;
import com.example.quizz_b.repository.QuizRecordRepository;
import com.example.quizz_b.repository.QuizRepository;
import com.example.quizz_b.repository.UserRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.AccessDeniedException;
import java.util.*;

@Service
public class QuizService {
    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizRecordRepository quizRecordRepository;

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
        tags.forEach(tag -> tagService.recordTagUsage(tag.getName(), TagUsageType.QUIZ));

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
    public Map<String, Object> getAllQuizzes(String type, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Quiz> quizPage;
        if ("ALL".equalsIgnoreCase(type)) {
            quizPage = quizRepository.findAllVisible(pageable);
        } else {
            QuizType quizType = QuizType.valueOf(type);
            quizPage = quizRepository.findAllVisibleByType(quizType, pageable);
        }
        List<QuizListDto> items = quizPage.getContent().stream()
                .map(this::convertToListDTO)
                .toList();

        Map<String, Object> result = new HashMap<>();
        result.put("items", items);
        result.put("totalPages", quizPage.getTotalPages());
        result.put("totalItems", quizPage.getTotalElements());
        result.put("currentPage", page);
        result.put("pageSize", size);

        return result;
    }

    @Transactional
    public QuizDto getQuizById(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() ->  new ResponseStatusException(HttpStatus.NOT_FOUND, "查無此題"));

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
        dto.setAvatarUrl(quiz.getAuthor().getAvatarUrl());
        dto.setQuizType(quiz.getQuizType().ordinal());
        dto.setTitle(quiz.getTitle());

        QuizStatsDto stats = getQuizStats(quiz.getId());
        dto.setQuizStats(stats);

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
    public void update(Long id, QuizSubmitRequestDto dto, User user) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到指定的題目"));

        // 權限驗證：只能本人修改
        if (!quiz.getAuthor().getId().equals(user.getId())) {
            throw new RuntimeException("無權限修改他人題目");
        }

        QuizType type = QuizType.fromValue(dto.getQuizType());

        // 題型驗證（工廠處理）
        factory.getHandler(type).validate(dto);

        // 更新欄位
        Set<Tag> tags = tagService.getOrCreateTags(dto.getTags());
        quiz.setQuizType(type);
        quiz.setTitle(dto.getTitle());
        quiz.setTitleDetail(dto.getTitleDetail());
        quiz.setSingleAnswerId(dto.getSingleAnswerId());
        quiz.setMultipleAnswerId(new HashSet<>(dto.getMultipleAnswerId()));
        quiz.setFlashAnswer(dto.getFlashAnswer());
        quiz.setAnswerDetail(dto.getAnswerDetail());
        quiz.setTags(tags);

        // 清空原選項 → 設定新選項
        quiz.getOptions().clear(); // 這會移除原 list 中所有選項 → orphanRemoval 生效

        List<QuizOption> newOptions = dto.getOptions();
        for (QuizOption option : newOptions) {
            option.setQuiz(quiz); // 綁定關聯
        }

        quiz.getOptions().addAll(newOptions); // 加入新的選項到同一個 List 實例

        // 最後 save（或自動 flush）
        quizRepository.save(quiz);
    }

    @Transactional
    public List<QuizListDto> getQuizzesByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        List<Quiz> quizzes = quizRepository.findByAuthorId(user.getId());
        return quizzes.stream().map(this::convertToListDTO).toList();
    }

    @Transactional(readOnly = true)
    public int getQuizCountByUserId(Long id) {
        return quizRepository.countByAuthorId(id);
    }

    @Transactional(readOnly = true)
    public List<QuizListDto> searchByTitle(String keyword) {
        if (keyword == null || keyword.isBlank()) return Collections.emptyList();

        String[] keywords = keyword.trim().split("\\s+"); // 切分多關鍵字

        return quizRepository.searchByMultipleKeywords(keywords).stream()
                .map(this::convertToListDTO)
                .toList();
    }

    public QuizStatsDto recordAnswer(QuizRecordRequestDto request, Long userId) {
        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() -> new RuntimeException("找不到該題目"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("找不到使用者"));

        // 建立紀錄物件
        QuizRecord record = new QuizRecord();
        record.setQuiz(quiz);
        record.setUser(user);
        record.setIsCorrect(request.getIsCorrect());

        quizRecordRepository.save(record);

        return getQuizStats(quiz.getId());
    }

    public QuizStatsDto getQuizStats(Long quizId) {
        Long total = quizRecordRepository.countByQuizId(quizId);
        Long correct = quizRecordRepository.countByQuizIdAndIsCorrectTrue(quizId);
        double rate = (total == 0) ? 0.0 : (double) correct / total;

        return new QuizStatsDto(total, correct, rate);
    }

    @Transactional
    public List<QuizListDto> findLatest(int limit) {
        List<Quiz> quizzes = quizRepository.findLatest(limit);
        return quizzes.stream()
                .map(this::convertToListDTO)
                .toList();
    }

    public Set<Tag> findTagsByQuizId(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到題目"));
        return quiz.getTags();
    }
}
