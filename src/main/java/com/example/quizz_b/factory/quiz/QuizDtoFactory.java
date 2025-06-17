package com.example.quizz_b.factory.quiz;

import com.example.quizz_b.constant.enums.QuizType;
import com.example.quizz_b.model.dto.*;
import com.example.quizz_b.model.entity.Quiz;

import java.util.ArrayList;
import java.util.List;

public class QuizDtoFactory {

    public static QuizDto fromEntity(Quiz quiz) {
        QuizType type = quiz.getQuizType();

        QuizDto baseDto;

        List<QuizOptionDto> optionDtos = quiz.getOptions().stream()
                .map(opt -> new QuizOptionDto(opt.getId(), opt.getText()))
                .toList();

        switch (type) {
            case SINGLE:
                SingleChoiceQuizDto singleDto = new SingleChoiceQuizDto();
                singleDto.setOptions(optionDtos);
                singleDto.setSingleAnswerId(quiz.getSingleAnswerId());
                baseDto = singleDto;
                break;
            case MULTIPLE:
                MultipleChoiceQuizDto multiDto = new MultipleChoiceQuizDto();
                multiDto.setOptions(optionDtos);
                multiDto.setMultipleAnswerId(new ArrayList<>(quiz.getMultipleAnswerId()));
                baseDto = multiDto;
                break;
            case FLASH:
                FlashCardQuizDto flashDto = new FlashCardQuizDto();
                flashDto.setFlashAnswer(quiz.getFlashAnswer());
                baseDto = flashDto;
                break;
            default:
                throw new RuntimeException("題目DTO工廠 不支援此題型");
        }

        baseDto.setId(quiz.getId());
        baseDto.setQuizType(quiz.getQuizType().getValue());
        baseDto.setTitle(quiz.getTitle());
        baseDto.setTitleDetail(quiz.getTitleDetail());
        baseDto.setAnswerDetail(quiz.getAnswerDetail());
        baseDto.setTags(quiz.getTags().stream() .map(tag -> new TagDto(tag.getId(), tag.getName())).toList());
        baseDto.setAuthorId(quiz.getAuthor().getId());
        baseDto.setAuthorName(quiz.getAuthor().getUsername());
        baseDto.setAuthorAvatarUrl(quiz.getAuthor().getAvatarUrl());
        baseDto.setCreateTime(quiz.getCreateTime());
        baseDto.setUpdateTime(quiz.getUpdateTime());

        return baseDto;
    }
}
