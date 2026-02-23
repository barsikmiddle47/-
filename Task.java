package models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class Task {
    private String id;
    private String title;
    private String description;
    private TaskCategory category;
    private Priority priority;
    private LocalDate dueDate;
    private boolean isCompleted;
    private LocalDateTime createdDate;

    public Task(String title, String description, TaskCategory category,
                Priority priority, LocalDate dueDate) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.dueDate = dueDate;
        this.isCompleted = false;
        this.createdDate = LocalDateTime.now();
    }

    public Task(String id, String title, String description, TaskCategory category,
                Priority priority, LocalDate dueDate, boolean isCompleted,
                LocalDateTime createdDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.dueDate = dueDate;
        this.isCompleted = isCompleted;
        this.createdDate = createdDate;
    }

    public String getId()                 { return id; }
    public String getTitle()              { return title; }
    public String getDescription()        { return description; }
    public TaskCategory getCategory()     { return category; }
    public Priority getPriority()         { return priority; }
    public LocalDate getDueDate()         { return dueDate; }
    public boolean isCompleted()          { return isCompleted; }
    public LocalDateTime getCreatedDate() { return createdDate; }

    public void setTitle(String title)             { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setCategory(TaskCategory category) { this.category = category; }
    public void setPriority(Priority priority)     { this.priority = priority; }
    public void setDueDate(LocalDate dueDate)      { this.dueDate = dueDate; }
    public void setCompleted(boolean completed)    { this.isCompleted = completed; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id.equals(task.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Task{" + "title='" + title + '\'' + ", priority=" + priority + '}';
    }
}
