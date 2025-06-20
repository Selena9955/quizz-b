package com.example.quizz_b.controller;

import com.example.quizz_b.constant.enums.TagUsageType;
import com.example.quizz_b.model.dto.AdminUserDto;
import com.example.quizz_b.response.ApiResponse;
import com.example.quizz_b.service.AdminUserService;
import com.example.quizz_b.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/db")
public class DashboardController {

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse< List<AdminUserDto> >> getAllUsers() {
        List<AdminUserDto> dtos = adminUserService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("取得所有會員成功", dtos));
    }

    @PostMapping("/users/role")
    public ResponseEntity<ApiResponse<List<AdminUserDto>>> changeUsersRole(@RequestBody Map<String, Object> body, HttpServletRequest httpRequest) {
        String token = jwtUtil.extractTokenFromRequest(httpRequest);
        String role = jwtUtil.extractClaim(token, claims -> claims.get("role", String.class));

        if (!"ROOT".equals(role)) {
            throw new AccessDeniedException("只有 ROOT 可以執行此操作");
        }
        List<AdminUserDto> dtos = adminUserService.changeUsersRole(body);
        return ResponseEntity.ok(ApiResponse.success("修改身分成功", dtos));
    }

    @GetMapping("/tag-usage")
    public ResponseEntity<Map<String, Integer>> getTagUsage(
            @RequestParam TagUsageType type
    ) {
        String key = "tag_usage:" + type.name().toLowerCase();
        Map<Object, Object> redisData = redisTemplate.opsForHash().entries(key);

        Map<String, Integer> result = redisData.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().toString(),
                        e -> Integer.parseInt(e.getValue().toString())
                ));

        return ResponseEntity.ok(result);
    }

}
