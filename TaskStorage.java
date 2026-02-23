package storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import models.Task;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskStorage {

    private static final String FILE_PATH = "tasks.json";

    private final Gson gson;
    private final List<Task> tasks;

    public TaskStorage() {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();
        tasks = new ArrayList<>();
        loadTasks();
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void addTask(Task task) {
        tasks.add(task);
        saveTasks();
    }

    public void removeTask(Task task) {
        tasks.remove(task);
        saveTasks();
    }

    public void updateTask(Task task) {
        saveTasks();
    }

    private void loadTasks() {
        if (!Files.exists(Path.of(FILE_PATH))) {
            return;
        }
        try (FileReader reader = new FileReader(FILE_PATH)) {
            Type type = new TypeToken<List<Task>>() {}.getType();
            List<Task> loaded = gson.fromJson(reader, type);
            if (loaded != null) {
                tasks.clear();
                tasks.addAll(loaded);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveTasks() {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(tasks, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
