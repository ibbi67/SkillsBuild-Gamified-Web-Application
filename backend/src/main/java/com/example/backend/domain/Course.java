package com.example.backend.domain;

public class Course {
    private final int id;
    private final String name;
    private final String description;
    private final String category;
    private final String level;

    public Course(int id, String name, String description, String category, String level) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.level = level;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public String getLevel() { return level; }
}


