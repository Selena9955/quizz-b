package com.example.quizz_b.constant.enums;

public enum QuizType {
    SINGLE(0), MULTIPLE(1), FLASH(2);

    private final int value;

    QuizType(int value) {
        this.value = value;
    }

    public static QuizType fromValue(int value) {
        for (QuizType type : QuizType.values()) {
            if (type.value == value) return type;
        }
        throw new IllegalArgumentException("Invalid quiz type: " + value);
    }

    public int getValue() {
        return value;
    }
}
