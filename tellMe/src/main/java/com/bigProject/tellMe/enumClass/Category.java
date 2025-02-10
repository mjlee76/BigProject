package com.bigProject.tellMe.enumClass;

import java.util.Arrays;

public enum Category {
    정상, 모욕, 협박, 욕설, 인종차별, 성차별, 성희롱, 장애인차별, 혐오발언, 종교차별, 외모차별, 지역차별, 나이차별, 기타;

    public static Category fromString(String category) {
        return Arrays.stream(Category.values())
                .filter(c -> c.name().equalsIgnoreCase(category))
                .findFirst()
                .orElse(Category.기타);
    }
}
