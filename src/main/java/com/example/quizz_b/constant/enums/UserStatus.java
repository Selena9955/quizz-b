package com.example.quizz_b.constant.enums;

public enum UserStatus {
    UNVERIFIED(0),
    VERIFIED(1),
    DEACTIVATED(2);

    private final int code;

    UserStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    // 可選：根據 code 反查 enum
    public static UserStatus fromCode(int code) {
        for (UserStatus status : UserStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid UserStatus code: " + code);
    }
}
