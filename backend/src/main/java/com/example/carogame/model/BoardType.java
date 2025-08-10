package com.example.carogame.model;

public enum BoardType {
    SMALL(10, "10x10 - Nhỏ"),
    STANDARD(15, "15x15 - Tiêu chuẩn"),
    LARGE(20, "20x20 - Lớn"),
    EXTRA_LARGE(25, "25x25 - Rất lớn"),
    HUGE(30, "30x30 - Khổng lồ"),
    MASSIVE(40, "40x40 - Siêu lớn");

    private final int size;
    private final String description;

    BoardType(int size, String description) {
        this.size = size;
        this.description = description;
    }

    public int getSize() {
        return size;
    }

    public String getDescription() {
        return description;
    }

    public static BoardType fromSize(int size) {
        for (BoardType type : values()) {
            if (type.size == size) {
                return type;
            }
        }
        return STANDARD; // Default fallback
    }
}
