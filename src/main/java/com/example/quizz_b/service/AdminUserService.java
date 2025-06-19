package com.example.quizz_b.service;

import com.example.quizz_b.model.dto.AdminUserDto;
import com.example.quizz_b.model.entity.User;
import com.example.quizz_b.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminUserService {
    @Autowired
    private UserRepository userRepository;

    public AdminUserDto convertToDTO(User user) {
        AdminUserDto dto = new AdminUserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus());
        dto.setCreateTime(user.getCreateTime());
        dto.setUpdateTime(user.getUpdateTime());
        return dto;
    }

    public List<AdminUserDto> getAllUsers() {
        List<User> users;
        users = userRepository.findAll(Sort.by(Sort.Direction.DESC, "createTime"));

        return users.stream()
                .map(this::convertToDTO)
                .toList();
    }
}
