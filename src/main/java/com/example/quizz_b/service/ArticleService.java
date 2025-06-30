package com.example.quizz_b.service;

import com.example.quizz_b.constant.enums.TagUsageType;
import com.example.quizz_b.model.dto.ArticleCreateRequestDto;
import com.example.quizz_b.model.dto.ArticleDetailDto;
import com.example.quizz_b.model.dto.ArticleListDto;
import com.example.quizz_b.model.dto.AuthorDto;
import com.example.quizz_b.model.entity.Article;
import com.example.quizz_b.model.entity.Tag;
import com.example.quizz_b.model.entity.User;
import com.example.quizz_b.repository.ArticleRepository;
import com.example.quizz_b.repository.TagRepository;
import com.example.quizz_b.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ArticleService {
    @Autowired
    private TagService tagService;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void create(ArticleCreateRequestDto request, User user) {
        Set<Tag> tags  = request.getTags().stream()
                .map(tagName -> {
                    tagName = tagName.trim().toLowerCase();
                    Tag tag = tagRepository.findByName(tagName).orElse(null);
                    if (tag == null) {
                        tag = new Tag();
                        tag.setName(tagName);
                        tag.setCountArticle(1);
                        tag = tagRepository.save(tag);
                    } else {
                        tag.setCountArticle(tag.getCountArticle() + 1);
                        tag = tagRepository.save(tag);
                    }
                    return tag;
                }
        ).collect(Collectors.toSet());
        tags.forEach(tag -> tagService.recordTagUsage(tag.getName(), TagUsageType.ARTICLE));

        Article article = new Article();
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setPreviewContent(request.getPreviewContent());
        article.setTags(tags);
        article.setAuthor(user);

        articleRepository.save(article);
    }

    @Transactional
    public List<ArticleListDto> getAllArticles() {
        List<Article> articles = articleRepository.findByIsDeleteFalse(Sort.by(Sort.Direction.DESC, "createTime"));
        return articles.stream().map(this::convertToListDTO).toList();
    }

    @Transactional
    public ArticleDetailDto getById(Long id) {
        Article article = articleRepository.findById(id).orElseThrow(() -> new RuntimeException("找不到指定的文章"));
        return convertToDetailDTO(article);
    }

    public void hardDeleteArticleById(Long articleId, Long userId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("找不到文章"));

        if (!article.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("無權限刪除此文章");
        }

        articleRepository.delete(article);
    }

    public void softDeleteArticleById(Long articleId, Long userId) throws AccessDeniedException {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("找不到文章"));

        // TODO: 支援管理員可跳過作者驗證邏輯，允許硬刪除
        if (!article.getAuthor().getId().equals(userId)) {
            throw new AccessDeniedException("無權限刪除該資料");
        }
        article.setDelete(true);
        articleRepository.save(article);
    }

    public void update(Long articleId, @Valid ArticleCreateRequestDto body, User user) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("文章不存在"));

        if (!article.getAuthor().getId().equals(user.getId())) {
            throw new RuntimeException("無權限修改這篇文章");
        }
        Set<Tag> tags  = body.getTags().stream()
                .map(tagName -> {
                            tagName = tagName.trim().toLowerCase();
                            Tag tag = tagRepository.findByName(tagName).orElse(null);
                            if (tag == null) {
                                tag = new Tag();
                                tag.setName(tagName);
                                tag.setCountArticle(1);
                                tag = tagRepository.save(tag);
                            } else {
                                tag.setCountArticle(tag.getCountArticle() + 1);
                                tag = tagRepository.save(tag);
                            }
                            return tag;
                        }
                ).collect(Collectors.toSet());

        article.setTitle(body.getTitle());
        article.setContent(body.getContent());
        article.setPreviewContent(body.getPreviewContent());
        article.setTags(tags);

        articleRepository.save(article);
    }

    public ArticleListDto convertToListDTO(Article article) {
        ArticleListDto dto = new ArticleListDto();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setPreviewContent(article.getPreviewContent());
        dto.setCreateTime(article.getCreateTime());

        AuthorDto authorDto = new AuthorDto(article.getAuthor().getId(),
                                            article.getAuthor().getUsername(),
                                            article.getAuthor().getAvatarUrl());
        dto.setAuthor(authorDto);

        List<String> tagNames = article.getTags().stream().map(Tag::getName).toList();
        dto.setTags(tagNames);
        return dto;
    }

    private ArticleDetailDto convertToDetailDTO(Article article){
        ArticleDetailDto dto = new ArticleDetailDto();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setContent(article.getContent());
        dto.setCreateTime(article.getCreateTime());
        dto.setUpdateTime(article.getUpdateTime());

        AuthorDto authorDto = new AuthorDto(article.getAuthor().getId(),
                                            article.getAuthor().getUsername(),
                                            article.getAuthor().getAvatarUrl());
        dto.setAuthor(authorDto);

        List<String> tagNames = article.getTags().stream().map(Tag::getName).toList();
        dto.setTags(tagNames);
        return dto;
    }

    @Transactional
    public List<ArticleListDto> getArticlesByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        List<Article> articles = articleRepository.findByAuthorIdAndIsDeleteFalse(user.getId());
        return articles.stream().map(this::convertToListDTO).toList();
    }

    @Transactional(readOnly = true)
    public int getArticleCountByUserId(Long id) {
        return articleRepository.countByAuthorId(id);
    }

    @Transactional
    public List<ArticleListDto> searchByTitle(String keyword) {
        return articleRepository.findByTitleContainingIgnoreCase(keyword)
                .stream()
                .map(this::convertToListDTO)
                .toList();
    }

    public Set<Tag> findTagsByArticleId(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到文章"));
        return article.getTags();
    }
}

