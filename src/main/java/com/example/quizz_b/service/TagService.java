package com.example.quizz_b.service;

import com.example.quizz_b.model.dto.TagDto;
import com.example.quizz_b.model.entity.Tag;
import com.example.quizz_b.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagService {
    @Autowired
    private TagRepository tagRepository;

    public List<TagDto> getAllTags(){
        List<Tag> tags = tagRepository.findAll();
        return tags.stream().map(this::convertToDTO).toList();
    }

    private TagDto convertToDTO(Tag tag){
        TagDto tagDto = new TagDto();
        tagDto.setId(tag.getId());
        tagDto.setName(tag.getName());
        tagDto.setCountArticle(tag.getCountArticle());
        tagDto.setCountProblem(tag.getCountProblem());

        return tagDto;
    }
}

