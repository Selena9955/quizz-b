package com.example.quizz_b.service;

import com.example.quizz_b.constant.enums.UserRole;
import com.example.quizz_b.model.dto.AdminUserDto;
import com.example.quizz_b.model.entity.User;
import com.example.quizz_b.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

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

    public List<AdminUserDto> changeUsersRole(Map<String, Object> body) {
        String role = (String) body.get("role");

        UserRole newRole;
        try {
            newRole = UserRole.valueOf(role);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "無效的角色名稱");
        }

        if (newRole == UserRole.ROOT) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "禁止變更為 ROOT");
        }

        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) body.get("ids");
        List<Long> longIds = ids.stream().map(Integer::longValue).toList();

        List<User> users = userRepository.findAllById(longIds);
        for (User user : users) {
            user.setRole(newRole);
        }
        userRepository.saveAll(users);

        return getAllUsers();
    }

}
