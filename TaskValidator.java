package utils;

import models.Task;
import models.Priority;
import models.TaskCategory;
import java.time.LocalDate;

public class TaskValidator {

    public static boolean isValidTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return false;
        }
        return title.trim().length() >= 2;
    }

    public static boolean isValidDescription(String description) {
        return description != null;
    }

    public static boolean isValidDate(LocalDate dueDate) {
        if (dueDate == null) {
            return true;
        }
        return !dueDate.isBefore(LocalDate.now());
    }

    public static boolean isValidPriority(Priority priority) {
        return priority != null;
    }

    public static boolean isValidCategory(TaskCategory category) {
        return category != null;
    }

    public static String validateTask(Task task) {
        if (task == null) {
            return "Задача не может быть null";
        }

        if (!isValidTitle(task.getTitle())) {
            return "Название должно быть минимум из 2 символов";
        }

        if (!isValidDescription(task.getDescription())) {
            return "Описание некорректно";
        }

        if (!isValidPriority(task.getPriority())) {
            return "Приоритет не должен быть пустым";
        }

        if (!isValidCategory(task.getCategory())) {
            return "Категория не должна быть пустой";
        }

        if (!isValidDate(task.getDueDate())) {
            return "Дата не может быть в прошлом";
        }

        return "";
    }
}
