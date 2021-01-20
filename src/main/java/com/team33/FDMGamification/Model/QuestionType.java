package com.team33.FDMGamification.Model;

public enum QuestionType {
    NONE("None"),
    DRAG_DROP("Drag and Drop"),
    MULTIPLE_CHOICE("Multiple choices"),
    TEXTBOX("Text box");

    private final String fullName;

    QuestionType(String fullname) {
        this.fullName = fullname;
    }

    public String getFullName() {
        return fullName;
    }
}
