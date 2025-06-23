package com.example.quizz_b.service;

import com.example.quizz_b.constant.enums.TagUsageType;
import com.example.quizz_b.model.dto.TagDetailDto;
import com.example.quizz_b.model.dto.TagDto;
import com.example.quizz_b.model.entity.Tag;
import com.example.quizz_b.repository.TagRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TagService {
    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

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
        tagDto.setCountQuizzes(tag.getCountQuiz());

        return tagDto;
    }

    private TagDto convertToDto(Tag tag){
        TagDto tagDto = new TagDto();
        tagDto.setId(tag.getId());
        tagDto.setName(tag.getName());

        return tagDto;
    }

    public List<TagDetailDto> searchByName(String keyword) {
        return tagRepository.findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(this::convertToDetailDto)
                .toList();
    }

    @Transactional
    public void updateHomeHotTags(List<String> tagNames) {
        // 清除所有已有的 homeHotOrder
        tagRepository.clearHomeHotOrder(); // 將欄位設為 null

        // 根據傳入順序更新
        for (int i = 0; i < tagNames.size(); i++) {
            tagRepository.updateHomeHotOrderByName(tagNames.get(i), i);
        }
    }

    public List<TagDto> getHomeHotTags() {
        List<Tag> tags = tagRepository.findByHomeHotOrderIsNotNullOrderByHomeHotOrderAsc();
        return tags.stream().map(this::convertToDto).toList();
    }

    // 寫入 Redis
    public void recordTagUsage(String tagName, TagUsageType type) {
        String date = LocalDate.now().toString(); // e.g. "2024-06-26"
        String key = "tag_usage:" + type.name().toLowerCase() + ":" + date;
        redisTemplate.opsForHash().increment(key, tagName, 1);
    }

    // 從 redis取得使用次數
    public List<Map<String, Object>> getTagUsageStats(TagUsageType type,
                                                      LocalDate startDate,
                                                      LocalDate endDate,
                                                      Integer limit) {
        if (startDate != null && endDate != null) {
            return getByDateRange(type, startDate, endDate, limit);
        }

        if (startDate != null || endDate != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "請同時提供 startDate 和 endDate，或全部不填");
        }

        // 如果都沒提供，找出 Redis 中最早和最晚日期來查詢
        return getByAllAvailableDates(type, limit);
    }

    private List<Map<String, Object>> getByAllAvailableDates(TagUsageType type, Integer limit) {
        List<TagUsageType> types = (type == TagUsageType.ALL)
                ? List.of(TagUsageType.SEARCH, TagUsageType.ARTICLE, TagUsageType.QUIZ)
                : List.of(type);

        Set<String> keys = new HashSet<>();
        for (TagUsageType t : types) {
            keys.addAll(redisTemplate.keys("tag_usage:" + t.name().toLowerCase() + ":*"));
        }

        TreeSet<LocalDate> allDates = new TreeSet<>();
        for (String key : keys) {
            String[] parts = key.split(":");
            if (parts.length == 3) {
                try {
                    LocalDate date = LocalDate.parse(parts[2]);
                    allDates.add(date);
                } catch (DateTimeParseException ignored) {}
            }
        }

        if (allDates.isEmpty()) {
            return List.of();
        }

        LocalDate startDate = allDates.first();
        LocalDate endDate = allDates.last();

        return getByDateRange(type, startDate, endDate, limit);
    }


    private List<Map<String, Object>> getByDateRange(TagUsageType type, LocalDate start, LocalDate end, Integer limit) {
        Map<String, Integer> tagCountMap = new HashMap<>();

        List<TagUsageType> types = (type == TagUsageType.ALL)
                ? List.of(TagUsageType.SEARCH, TagUsageType.ARTICLE, TagUsageType.QUIZ)
                : List.of(type);

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            for (TagUsageType t : types) {
                String key = "tag_usage:" + t.name().toLowerCase() + ":" + date;
                Map<Object, Object> redisData = redisTemplate.opsForHash().entries(key);

                for (var entry : redisData.entrySet()) {
                    tagCountMap.merge(entry.getKey().toString(),
                            Integer.parseInt(entry.getValue().toString()),
                            Integer::sum);
                }
            }
        }

        return toSortedTagList(tagCountMap, limit);
    }

    // 排序並格式化為 List<Map>
    private List<Map<String, Object>> toSortedTagList(Map<String, Integer> tagCountMap, Integer limit) {
        return tagCountMap.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(limit != null ? limit : Long.MAX_VALUE)
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", entry.getKey());
                    map.put("value", entry.getValue());
                    return map;
                })
                .toList();
    }

    // 記錄從搜尋來的使用次數
    public void recordSearchTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) return;
        System.out.println("redis:search 紀錄");
        for (String tag : tags) {
            recordTagUsage(tag, TagUsageType.SEARCH);
        }
    }
}

