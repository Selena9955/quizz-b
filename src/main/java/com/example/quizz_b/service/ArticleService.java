package com.example.quizz_b.service;

import com.example.quizz_b.model.dto.ArticleCreateRequestDto;
import com.example.quizz_b.model.dto.ArticleDetailDto;
import com.example.quizz_b.model.dto.ArticleListDto;
import com.example.quizz_b.model.dto.AuthorDto;
import com.example.quizz_b.model.entity.Article;
import com.example.quizz_b.model.entity.Tag;
import com.example.quizz_b.model.entity.User;
import com.example.quizz_b.repository.ArticleRepository;
import com.example.quizz_b.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ArticleService {
    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private TagRepository tagRepository;

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

        Article article = new Article();
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setPreviewContent(request.getPreviewContent());
        article.setTags(tags);
        article.setAuthor(user);

        articleRepository.save(article);
    }

    public List<ArticleListDto> getAllArticles() {
        List<Article> articles = articleRepository.findAll();
        return articles.stream().map(this::convertToListDTO).toList();
    }

    @Transactional
    public ArticleDetailDto getById(Long id) {
        Article article = articleRepository.findById(id).orElseThrow(() -> new RuntimeException("找不到指定的文章"));
        return convertToDetailDTO(article);
    }

    public void delete(Long articleId, Long userId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("找不到文章"));

        if (!article.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("無權限刪除此文章");
        }

        articleRepository.delete(article);
    }

    public ArticleListDto convertToListDTO(Article article) {
        ArticleListDto dto = new ArticleListDto();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setPreviewContent(article.getPreviewContent());
        dto.setCreateTime(article.getCreateTime());

        AuthorDto authorDto = new AuthorDto(article.getAuthor().getId(), article.getAuthor().getUsername());
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

        AuthorDto authorDto = new AuthorDto(article.getAuthor().getId(), article.getAuthor().getUsername());
        dto.setAuthor(authorDto);

        List<String> tagNames = article.getTags().stream().map(Tag::getName).toList();
        dto.setTags(tagNames);
        return dto;
    }

}

