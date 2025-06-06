package com.example.quizz_b.service;

import com.example.quizz_b.model.dto.ArticleCreateRequestDto;
import com.example.quizz_b.model.dto.ArticleDto;
import com.example.quizz_b.model.entity.Article;
import com.example.quizz_b.model.entity.Tag;
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
    public void create(ArticleCreateRequestDto request) {
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
        article.setTags(tags);

        articleRepository.save(article);
    }

    public List<ArticleDto> getAllArticles() {
        List<Article> articles = articleRepository.findAll();
        return articles.stream().map(this::convertToDTO).toList();
    }

    @Transactional
    public ArticleDto getById(Long id) {
        Article article = articleRepository.findById(id).orElseThrow(() -> new RuntimeException("找不到指定的文章"));
        return convertToDTO(article);
    }

    private ArticleDto convertToDTO(Article article){
        ArticleDto dto = new ArticleDto();
        dto.setId(article.getId());
        dto.setCreateTime(article.getCreateTime());
        dto.setUpdateTime(article.getUpdateTime());
        dto.setTitle(article.getTitle());
        dto.setContent(article.getContent());

        List<String> tagNames = article.getTags().stream().map(Tag::getName).toList();
        dto.setTags(tagNames);
        return dto;
    }
}

