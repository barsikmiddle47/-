package models;

public enum TaskCategory {
    WORK("Работа"),
    PERSONAL("Личное"),
    STUDY("Учёба"),
    OTHER("Прочее");

    private final String displayName;

    TaskCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
