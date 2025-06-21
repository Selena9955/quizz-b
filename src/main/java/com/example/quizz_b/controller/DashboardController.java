package com.example.quizz_b.controller;

import com.example.quizz_b.constant.enums.TagUsageType;
import com.example.quizz_b.model.dto.AdminUserDto;
import com.example.quizz_b.response.ApiResponse;
import com.example.quizz_b.service.AdminUserService;
import com.example.quizz_b.service.TagService;
import com.example.quizz_b.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/db")
public class DashboardController {

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private TagService tagService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping({"", "/"})
    public String testAdmin() {
        return "admin ok";
    }

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
    public ResponseEntity<List<Map<String, Object>>> getTagUsage(
            @RequestParam(value = "type", required = false, defaultValue = "ALL") TagUsageType type,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "limit", required = false) Integer limit
    ) {
        System.out.printf("%s %s %s %n",type, startDate, endDate, limit);
        List<Map<String, Object>> usageStats = tagService.getTagUsageStats(type, startDate, endDate, limit);
        return ResponseEntity.ok(usageStats);
    }

}
