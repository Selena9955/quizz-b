package com.example.quizz_b.service;

import com.example.quizz_b.model.dto.TagDetailDto;
import com.example.quizz_b.model.dto.TagDto;
import com.example.quizz_b.model.entity.Tag;
import com.example.quizz_b.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TagService {
    @Autowired
    private TagRepository tagRepository;

    public List<TagDetailDto> getAllTags(){
        List<Tag> tags = tagRepository.findAll();
        return tags.stream().map(this::convertToDetailDto).toList();
    }

    public Set<Tag> getOrCreateTags(List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            System.out.println("沒有 tags 列表，已建立空列表");
            return new HashSet<>();
        }

        return tagNames.stream()
                .map(this::getOrCreateTag)
                .collect(Collectors.toSet());
    }

    public Tag getOrCreateTag(String name) {
        String normalized = name.trim().toLowerCase();
        Optional<Tag> existing = tagRepository.findByName(normalized);
        if (existing.isPresent()) {
            System.out.println("Tag 已存在：" + normalized);
            return existing.get();
        }

        Tag tag = new Tag();
        tag.setName(normalized);
        Tag saved = tagRepository.save(tag);
        System.out.println("新增 tag：" + saved.getName() + " (id: " + saved.getId() + ")");
        return saved;
    }

    private TagDetailDto convertToDetailDto(Tag tag){
        TagDetailDto tagDto = new TagDetailDto();
        tagDto.setId(tag.getId());
        tagDto.setName(tag.getName());
        tagDto.setCountArticles(tag.getCountArticle());
        tagDto.setCountQuizzes(tag.getCountQuizzes());

        return tagDto;
    }

    public List<TagDetailDto> searchByName(String keyword) {
        return tagRepository.findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(this::convertToDetailDto)
                .toList();
    }
}

