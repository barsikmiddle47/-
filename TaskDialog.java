package ui;

import models.Task;
import models.TaskCategory;
import models.Priority;
import utils.TaskValidator;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class TaskDialog extends JDialog {

    private JTextField titleField;
    private JTextArea descriptionArea;
    private JComboBox<TaskCategory> categoryCombo;
    private JComboBox<Priority> priorityCombo;
    private JSpinner dateSpinner;
    private JButton okBtn;
    private JButton cancelBtn;

    private Task task;
    private boolean isConfirmed = false;

    public TaskDialog(JFrame parent, Task task) {
        super(parent, "Добавить/Редактировать задачу", true);

        this.task = task;

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(parent);

        initUI();

        if (task != null) {
            loadTaskData();
        }
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Название
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Название:"), gbc);

        gbc.gridx = 1;
        titleField = new JTextField(25);
        mainPanel.add(titleField, gbc);

        // Описание
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Описание:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        descriptionArea = new JTextArea(5, 25);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        mainPanel.add(scrollPane, gbc);

        // Категория
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;
        gbc.weighty = 0;
        mainPanel.add(new JLabel("Категория:"), gbc);

        gbc.gridx = 1;
        categoryCombo = new JComboBox<>(TaskCategory.values());
        mainPanel.add(categoryCombo, gbc);

        // Приоритет
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(new JLabel("Приоритет:"), gbc);

        gbc.gridx = 1;
        priorityCombo = new JComboBox<>(Priority.values());
        mainPanel.add(priorityCombo, gbc);

        // Дата
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(new JLabel("Дата выполнения:"), gbc);

        gbc.gridx = 1;
        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(editor);
        mainPanel.add(dateSpinner, gbc);

        // Кнопки
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        okBtn = new JButton("OK");
        okBtn.addActionListener(e -> confirmDialog());
        buttonsPanel.add(okBtn);

        cancelBtn = new JButton("Отмена");
        cancelBtn.addActionListener(e -> cancelDialog());
        buttonsPanel.add(cancelBtn);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
    }

    private void loadTaskData() {
        if (task != null) {
            titleField.setText(task.getTitle());
            descriptionArea.setText(task.getDescription());
            categoryCombo.setSelectedItem(task.getCategory());
            priorityCombo.setSelectedItem(task.getPriority());

            if (task.getDueDate() != null) {
                java.util.Date date = java.sql.Date.valueOf(task.getDueDate());
                dateSpinner.setValue(date);
            }
        }
    }

    private void confirmDialog() {
        String title = titleField.getText();
        String description = descriptionArea.getText();
        TaskCategory category = (TaskCategory) categoryCombo.getSelectedItem();
        Priority priority = (Priority) priorityCombo.getSelectedItem();

        // Валидация
        if (!TaskValidator.isValidTitle(title)) {
            showError("Название должно содержать минимум 2 символа");
            return;
        }

        if (!TaskValidator.isValidCategory(category)) {
            showError("Выберите категорию");
            return;
        }

        if (!TaskValidator.isValidPriority(priority)) {
            showError("Выберите приоритет");
            return;
        }

        // Получаем дату
        java.util.Date utilDate = (java.util.Date) dateSpinner.getValue();
        LocalDate dueDate = new java.sql.Date(utilDate.getTime()).toLocalDate();

        if (!TaskValidator.isValidDate(dueDate)) {
            showError("Дата не может быть в прошлом");
            return;
        }

        // Создаём или обновляем задачу
        if (task == null) {
            task = new Task(title, description, category, priority, dueDate);
        } else {
            task.setTitle(title);
            task.setDescription(description);
            task.setCategory(category);
            task.setPriority(priority);
            task.setDueDate(dueDate);
        }

        isConfirmed = true;
        dispose();
    }

    private void cancelDialog() {
        isConfirmed = false;
        task = null;
        dispose();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Ошибка валидации",
                JOptionPane.ERROR_MESSAGE);
    }

    public Task showDialog() {
        setVisible(true);
        return isConfirmed ? task : null;
    }
}
