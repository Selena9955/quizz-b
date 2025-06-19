package com.example.quizz_b.controller;

import com.example.quizz_b.model.dto.AdminUserDto;
import com.example.quizz_b.response.ApiResponse;
import com.example.quizz_b.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/db")
public class DashboardController {

    @Autowired
    private AdminUserService adminUserService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse< List<AdminUserDto> >> getAllUsers(
    ) {
        List<AdminUserDto> dtos = adminUserService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("取得所有會員成功", dtos));
    }

}
