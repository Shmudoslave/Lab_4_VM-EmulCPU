package com.company.vm.command;

import java.util.Arrays;

public class Command {
    private final CommandType type;
    private final String[] operands;

    // Конструктор из строки
    public Command(String line) {
        String[] parts = line.trim().split("\\s+");
        if (parts.length == 0) {
            throw new IllegalArgumentException("Empty command");
        }

        this.type = CommandType.valueOf(parts[0].toUpperCase());
        this.operands = Arrays.copyOfRange(parts, 1, parts.length);
    }

    // Конструктор из типа и операндов
    public Command(CommandType type, String... operands) {
        this.type = type;
        this.operands = operands != null ? operands : new String[0];
    }

    // НОВЫЙ КОНСТРУКТОР: из строкового типа и операндов
    public Command(String type, String... operands) {
        this.type = CommandType.valueOf(type.toUpperCase());
        this.operands = operands != null ? operands : new String[0];
    }

    public CommandType getType() {
        return type;
    }

    public String[] getOperands() {
        return operands;
    }

    public String getOperand(int index) {
        return (operands != null && operands.length > index) ? operands[index] : null;
    }

    @Override
    public String toString() {
        return type + (operands.length > 0 ? " " + String.join(" ", operands) : "");
    }
}