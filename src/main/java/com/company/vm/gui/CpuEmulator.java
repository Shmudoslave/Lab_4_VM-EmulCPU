package com.company.vm.gui;

import com.company.vm.command.Command;
import com.company.vm.command.CommandType;
import com.company.vm.core.Executer;
import com.company.vm.core.Program;
import com.company.vm.cpu.BaseCpu;
import com.company.vm.cpu.BCpu;
import com.company.vm.cpu.ICpu;
import com.company.vm.core.Register;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class CpuEmulator extends JFrame {
    private Program program;
    private ICpu cpu;
    private Executer executer;
    private int currentInstructionIndex;

    // Компоненты GUI
    private JTable programTable;
    private DefaultTableModel tableModel;
    private JLabel registerALabel, registerBLabel, registerCLabel, registerDLabel;
    private JTextArea memoryArea;
    private JTextArea statsArea;
    private JButton addButton, deleteButton, moveUpButton, moveDownButton;
    private JButton stepButton, resetButton;
    private JComboBox<String> instructionCombo;
    private JTextField operand1Field, operand2Field;

    public CpuEmulator() {
        program = new Program();
        cpu = BCpu.build();
        executer = new Executer(cpu);
        currentInstructionIndex = -1;

        initializeGUI();
        updateDisplay();
    }

    private void initializeGUI() {
        setTitle("Эмулятор процессора - Виртуальная машина");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Панель программы
        JPanel programPanel = createProgramPanel();
        add(programPanel, BorderLayout.CENTER);

        // Панель управления
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.NORTH);

        // Панель состояния
        JPanel statusPanel = createStatusPanel();
        add(statusPanel, BorderLayout.SOUTH);

        pack();
        setSize(1000, 700);
        setLocationRelativeTo(null);
    }

    private JPanel createProgramPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Таблица программы
        String[] columns = {"✓", "№", "Инструкция", "Операнд 1", "Операнд 2"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 0 ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // Только галочка редактируема
            }
        };

        programTable = new JTable(tableModel);
        programTable.setRowHeight(25);
        programTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        programTable.getColumnModel().getColumn(1).setPreferredWidth(40);

        JScrollPane scrollPane = new JScrollPane(programTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Программа"));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Выбор инструкции
        instructionCombo = new JComboBox<>(new String[]{
                "init", "ld", "st", "mv", "add", "sub", "mult", "div", "print"
        });
        instructionCombo.setPreferredSize(new Dimension(80, 25));

        operand1Field = new JTextField(8);
        operand2Field = new JTextField(8);

        addButton = new JButton("Добавить");
        deleteButton = new JButton("Удалить");
        moveUpButton = new JButton("Вверх");
        moveDownButton = new JButton("Вниз");
        stepButton = new JButton("Выполнить шаг");
        resetButton = new JButton("Сбросить выполнение");

        // Обработчики событий
        addButton.addActionListener(e -> addInstruction());
        deleteButton.addActionListener(e -> deleteInstruction());
        moveUpButton.addActionListener(e -> moveInstruction(-1));
        moveDownButton.addActionListener(e -> moveInstruction(1));
        stepButton.addActionListener(e -> executeStep());
        resetButton.addActionListener(e -> resetExecution());

        panel.add(new JLabel("Инструкция:"));
        panel.add(instructionCombo);
        panel.add(new JLabel("Операнд 1:"));
        panel.add(operand1Field);
        panel.add(new JLabel("Операнд 2:"));
        panel.add(operand2Field);
        panel.add(addButton);
        panel.add(deleteButton);
        panel.add(moveUpButton);
        panel.add(moveDownButton);
        panel.add(stepButton);
        panel.add(resetButton);

        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Регистры
        JPanel registerPanel = new JPanel(new GridLayout(1, 4, 5, 0));
        registerPanel.setBorder(BorderFactory.createTitledBorder("Регистры процессора"));
        registerALabel = createRegisterLabel("A");
        registerBLabel = createRegisterLabel("B");
        registerCLabel = createRegisterLabel("C");
        registerDLabel = createRegisterLabel("D");

        registerPanel.add(registerALabel);
        registerPanel.add(registerBLabel);
        registerPanel.add(registerCLabel);
        registerPanel.add(registerDLabel);

        // Память
        memoryArea = new JTextArea(10, 20);
        memoryArea.setEditable(false);
        memoryArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane memoryScroll = new JScrollPane(memoryArea);
        memoryScroll.setBorder(BorderFactory.createTitledBorder("Состояние памяти (первые 50 ячеек)"));

        // Статистика
        statsArea = new JTextArea(10, 15);
        statsArea.setEditable(false);
        statsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane statsScroll = new JScrollPane(statsArea);
        statsScroll.setBorder(BorderFactory.createTitledBorder("Статистика программы"));

        panel.add(registerPanel);
        panel.add(memoryScroll);
        panel.add(statsScroll);

        return panel;
    }

    private JLabel createRegisterLabel(String registerName) {
        JLabel label = new JLabel("0", JLabel.CENTER);
        label.setBorder(BorderFactory.createTitledBorder("Регистр " + registerName));
        label.setOpaque(true);
        label.setBackground(Color.LIGHT_GRAY);
        label.setPreferredSize(new Dimension(80, 60));
        label.setFont(new Font("Arial", Font.BOLD, 16));
        return label;
    }

    private void addInstruction() {
        String instruction = (String) instructionCombo.getSelectedItem();
        String op1 = operand1Field.getText().trim();
        String op2 = operand2Field.getText().trim();

        try {
            Command command;
            if (op1.isEmpty() && op2.isEmpty()) {
                command = new Command(instruction);
            } else if (op2.isEmpty()) {
                command = new Command(instruction, op1);
            } else {
                command = new Command(instruction, op1, op2);
            }

            program.add(command);
            updateProgramTable();
            clearInputFields();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ошибка создания команды: " + e.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteInstruction() {
        int selectedRow = programTable.getSelectedRow();
        if (selectedRow != -1) {
            tableModel.removeRow(selectedRow);
            updateProgramFromTable();
        } else {
            JOptionPane.showMessageDialog(this, "Выберите инструкцию для удаления",
                    "Внимание", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void moveInstruction(int direction) {
        int selectedRow = programTable.getSelectedRow();
        if (selectedRow != -1 && selectedRow + direction >= 0 &&
                selectedRow + direction < tableModel.getRowCount()) {

            // Перемещение в таблице
            for (int col = 0; col < tableModel.getColumnCount(); col++) {
                Object value1 = tableModel.getValueAt(selectedRow, col);
                Object value2 = tableModel.getValueAt(selectedRow + direction, col);
                tableModel.setValueAt(value2, selectedRow, col);
                tableModel.setValueAt(value1, selectedRow + direction, col);
            }

            programTable.setRowSelectionInterval(selectedRow + direction, selectedRow + direction);
            updateProgramFromTable();
        }
    }

    private void executeStep() {
        if (currentInstructionIndex == -1) {
            // Начало выполнения
            cpu = BCpu.build(); 
            executer = new Executer(cpu);
            currentInstructionIndex = 0;
        }

        if (currentInstructionIndex < program.size()) {
            // Выполнение текущей инструкции
            Command currentCommand = program.get(currentInstructionIndex);

            try {
                cpu.exec(currentCommand);

                // Отметка выполнения в таблице
                tableModel.setValueAt(true, currentInstructionIndex, 0);

                // Подсветка текущей инструкции
                highlightCurrentInstruction();
                currentInstructionIndex++;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Ошибка выполнения инструкции: " + e.getMessage(),
                        "Ошибка выполнения", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Программа завершена!",
                    "Завершение", JOptionPane.INFORMATION_MESSAGE);
            currentInstructionIndex = -1;
        }

        updateDisplay();
    }

    private void resetExecution() {
        currentInstructionIndex = -1;
        cpu = BCpu.build();
        executer = new Executer(cpu);

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.setValueAt(false, i, 0);
        }

        clearInstructionHighlight();
        updateDisplay();

        JOptionPane.showMessageDialog(this, "Выполнение программы сброшено",
                "Сброс", JOptionPane.INFORMATION_MESSAGE);
    }

    private void highlightCurrentInstruction() {
        clearInstructionHighlight();
        if (currentInstructionIndex >= 0 && currentInstructionIndex < tableModel.getRowCount()) {
            programTable.setRowSelectionInterval(currentInstructionIndex, currentInstructionIndex);
            programTable.scrollRectToVisible(programTable.getCellRect(currentInstructionIndex, 0, true));

            // Установка красного фона для текущей инструкции
            programTable.setSelectionBackground(Color.RED);
            programTable.setSelectionForeground(Color.WHITE);
        }
    }

    private void clearInstructionHighlight() {
        programTable.clearSelection();
    }

    private void updateDisplay() {
        updateRegisters();
        updateMemory();
        updateStatistics();
    }

    private void updateRegisters() {
        registerALabel.setText(String.valueOf(((BaseCpu) cpu).getRegister(Register.A)));
        registerBLabel.setText(String.valueOf(((BaseCpu) cpu).getRegister(Register.B)));
        registerCLabel.setText(String.valueOf(((BaseCpu) cpu).getRegister(Register.C)));
        registerDLabel.setText(String.valueOf(((BaseCpu) cpu).getRegister(Register.D)));
    }

    private void updateMemory() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 50; i++) { // Показываем первые 50 ячеек
            if (i % 5 == 0 && i != 0) {
                sb.append("\n");
            }
            sb.append(String.format("%2d:%-4d ", i, cpu.getMemory(i)));
        }
        memoryArea.setText(sb.toString());
    }

    private void updateStatistics() {
        StringBuilder sb = new StringBuilder();
        sb.append("Частота появления инструкций:\n\n");

        for (Map.Entry<CommandType, Long> entry : program.getInstructionsByFrequency()) {
            sb.append("  ")
                    .append(entry.getKey().toString().toLowerCase())
                    .append(": ")
                    .append(entry.getValue())
                    .append(" раз\n");
        }

        sb.append("\nДиапазон адресов памяти:\n  ")
                .append(program.getMemoryAddressRange());

        statsArea.setText(sb.toString());
    }

    private void updateProgramTable() {
        tableModel.setRowCount(0);
        int index = 0;
        for (Command cmd : program) {
            String[] operands = cmd.getOperands();
            String op1 = operands.length > 0 ? operands[0] : "";
            String op2 = operands.length > 1 ? operands[1] : "";

            tableModel.addRow(new Object[]{
                    false, // галочка выполнения
                    String.valueOf(index + 1),
                    cmd.getType().toString().toLowerCase(),
                    op1,
                    op2
            });
            index++;
        }
    }

    private void updateProgramFromTable() {
        // Синхронизация Program с таблицей
        Program newProgram = new Program();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String instruction = (String) tableModel.getValueAt(i, 2);
            String op1 = (String) tableModel.getValueAt(i, 3);
            String op2 = (String) tableModel.getValueAt(i, 4);

            try {
                Command command;
                if (op1.isEmpty() && op2.isEmpty()) {
                    command = new Command(instruction);
                } else if (op2.isEmpty()) {
                    command = new Command(instruction, op1);
                } else {
                    command = new Command(instruction, op1, op2);
                }

                newProgram.add(command);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Ошибка в инструкции " + (i + 1) + ": " + e.getMessage(),
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        program = newProgram;
        updateStatistics();
    }

    private void clearInputFields() {
        operand1Field.setText("");
        operand2Field.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try{
                UIManager.setLookAndFeel(UIManager.getLookAndFeel());
            } catch (Exception e) {
                e.printStackTrace();
            }

            new CpuEmulator().setVisible(true);
        });
    }
}