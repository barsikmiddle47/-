package ui;

import models.Task;
import models.TaskCategory;
import models.Priority;
import storage.TaskStorage;
import utils.TaskValidator;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

public class TodoFrame extends JFrame {

    private TaskStorage storage;
    private JTable tasksTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<TaskCategory> categoryFilter;
    private JComboBox<String> statusFilter;
    private boolean darkTheme = false; // Переменная для отслеживания темы
    private JToggleButton themeToggleButton; // Кнопка переключения темы

    public TodoFrame() {
        super("TODO List Приложение");

        storage = new TaskStorage();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setResizable(true);

        initUI();
        loadTasks();
    }

    private void initUI() {
        // Главная панель
        JPanel mainPanel = new JPanel(new BorderLayout());

        this.add(mainPanel);

        // Панель инструментов (поиск и фильтры)
        JPanel toolbarPanel = createToolbarPanel();
        mainPanel.add(toolbarPanel, BorderLayout.NORTH);

        // Таблица задач
        JPanel tablePanel = createTablePanel();
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // Панель кнопок
        JPanel buttonsPanel = createButtonsPanel();
        buttonsPanel.setBackground(new Color(255, 107, 0));
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
    }

    public void getButtonsPanel() {
        //for ()
    }

    private void toggleTheme() {
        darkTheme = !darkTheme;
        updateTheme();
    }

    private void updateTheme() {
        Color backgroundColor;
        Color foregroundColor;

        if (darkTheme) {
            backgroundColor = Color.DARK_GRAY;
            foregroundColor = Color.LIGHT_GRAY;
            themeToggleButton.setText("Светлая тема");
        } else {
            backgroundColor = Color.WHITE;
            foregroundColor = Color.BLACK;
            themeToggleButton.setText("Темная тема");
        }

        // Обновляем цвета основного окна
        getContentPane().setBackground(backgroundColor);
        searchField.setBackground(backgroundColor);
        searchField.setForeground(foregroundColor);
        searchField.setCaretColor(foregroundColor);

        // Обновляем таблицу
        tasksTable.setBackground(backgroundColor);
        tasksTable.setForeground(foregroundColor);

        // Цикл обновления всех компонентов с учетом темы
        for (Component component : getContentPane().getComponents()) {
            if (component instanceof JPanel) {
                component.setBackground(backgroundColor);
            }
            component.setForeground(foregroundColor);
        }

        // После изменения темы, перезагружаем задачи, чтобы обновить таблицу
        loadTasks();
    }

    private JPanel createToolbarPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBackground(new Color(255, 107, 0));

        // Поиск
        panel.add(new JLabel("Поиск:"));
        searchField = new JTextField(15);
        panel.add(searchField);

        // Фильтр по категориям
        panel.add(new JLabel("Категория:"));
        TaskCategory[] categories = new TaskCategory[TaskCategory.values().length + 1];
        categories[0] = null; // "Все"
        for (int i = 0; i < TaskCategory.values().length; i++) {
            categories[i + 1] = TaskCategory.values()[i];
        }
        categoryFilter = new JComboBox<>(categories);
        categoryFilter.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                if (value == null) {
                    value = "Все";
                } else {
                    value = ((TaskCategory) value).getDisplayName();
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        panel.add(categoryFilter);

        // Фильтр по статусу
        panel.add(new JLabel("Статус:"));
        String[] statuses = {"Все", "Активные", "Выполненные"};
        statusFilter = new JComboBox<>(statuses);
        panel.add(statusFilter);

        // Кнопка применить фильтры
        JButton applyFilterBtn = new JButton("Применить");
        applyFilterBtn.addActionListener(e -> applyFilters());
        panel.add(applyFilterBtn);

        // Переключатель тем
        themeToggleButton = new JToggleButton("Темная тема");
        themeToggleButton.addActionListener(e -> toggleTheme());
        panel.add(themeToggleButton);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Модель таблицы
        tableModel = new DefaultTableModel(
                new String[]{"✓", "Название", "Категория", "Приоритет", "Дата", "Статус"},
                0
        ) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) return Boolean.class;
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // Только чекбокс редактируется
            }
        };

        tasksTable = new JTable(tableModel);
        tasksTable.setRowHeight(25);
        tasksTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        tasksTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        tasksTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        tasksTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        tasksTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        tasksTable.getColumnModel().getColumn(5).setPreferredWidth(80);

        // Слушатель для чекбокса
        tasksTable.getModel().addTableModelListener(e -> {
            if (e.getColumn() == 0) {
                int row = e.getFirstRow();
                boolean isCompleted = (boolean) tableModel.getValueAt(row, 0);
                Task task = getTaskFromRow(row);
                if (task != null) {
                    task.setCompleted(isCompleted);
                    storage.updateTask(task);
                    loadTasks();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tasksTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JButton addBtn = new JButton(" + Добавить");
        addBtn.addActionListener(e -> openAddTaskDialog());
        panel.add(addBtn);

        JButton editBtn = new JButton(" ± Редактировать");
        editBtn.addActionListener(e -> openEditTaskDialog());
        panel.add(editBtn);

        JButton deleteBtn = new JButton(" — Удалить");
        deleteBtn.addActionListener(e -> deleteTask());
        panel.add(deleteBtn);

        JButton clearBtn = new JButton(" | Очистить фильтры");
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            categoryFilter.setSelectedIndex(0);
            statusFilter.setSelectedIndex(0);
            loadTasks();
        });
        panel.add(clearBtn);

        return panel;
    }
    private void openAddTaskDialog() {
        TaskDialog dialog = new TaskDialog(this, null);
        Task newTask = dialog.showDialog();
        if (newTask != null) {
            storage.addTask(newTask);
            loadTasks();
        getButtonsPanel();
        }
    }

    private void openEditTaskDialog() {
        int selectedRow = tasksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите задачу для редактирования",
                    "Предупреждение", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Task selectedTask = getTaskFromRow(selectedRow);
        if (selectedTask != null) {
            TaskDialog dialog = new TaskDialog(this, selectedTask);
            Task editedTask = dialog.showDialog();
            if (editedTask != null) {
                storage.updateTask(editedTask);
                loadTasks();
            }
        }
    }

    private void deleteTask() {
        int selectedRow = tasksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите задачу для удаления",
                    "Предупреждение", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Вы уверены, что хотите удалить эту задачу?",
                "Подтверждение удаления", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Task task = getTaskFromRow(selectedRow);
            if (task != null) {
                storage.removeTask(task);
                loadTasks();
            }
        }
    }

    private void applyFilters() {
        loadTasks();
    }

    private void loadTasks() {
        tableModel.setRowCount(0);

        String searchText = searchField.getText().toLowerCase();
        TaskCategory selectedCategory = (TaskCategory) categoryFilter.getSelectedItem();
        String selectedStatus = (String) statusFilter.getSelectedItem();

        for (Task task : storage.getTasks()) {
            // Фильтр по поиску
            if (!task.getTitle().toLowerCase().contains(searchText)) {
                continue;
            }

            // Фильтр по категории
            if (selectedCategory != null && !task.getCategory().equals(selectedCategory)) {
                continue;
            }

            // Фильтр по статусу
            if (selectedStatus.equals("Активные") && task.isCompleted()) {
                continue;
            }
            if (selectedStatus.equals("Выполненные") && !task.isCompleted()) {
                continue;
            }

            // Добавляем строку в таблицу
            tableModel.addRow(new Object[]{
                    task.isCompleted(),
                    task.getTitle(),
                    task.getCategory().getDisplayName(),
                    task.getPriority().getDisplayName(),
                    task.getDueDate(),
                    task.isCompleted() ? "Выполнена" : "Активна"
            });
        }
    }

    private Task getTaskFromRow(int row) {
        if (row < 0 || row >= tableModel.getRowCount()) {
            return null;
        }
        String taskTitle = (String) tableModel.getValueAt(row, 1);
        for (Task task : storage.getTasks()) {
            if (task.getTitle().equals(taskTitle)) {
                return task;
            }
        }
        return null;
    }
}
