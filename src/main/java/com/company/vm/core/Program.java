package com.company.vm.core;

import com.company.vm.command.Command;
import com.company.vm.command.CommandType;

import java.util.*;
import java.util.stream.Collectors;

public class Program implements Iterable<Command> {
    private final List<Command> commands;

    public Program() {
        this.commands = new ArrayList<>();
    }

    public void add(Command command) {
        commands.add(command);
    }

    public void add(String commandLine) {
        commands.add(new Command(commandLine));
    }

    public void add(CommandType type, String... operands) {
        commands.add(new Command(type, operands));
    }

    public int size() {
        return commands.size();
    }

    public Command get(int index) {
        return commands.get(index);
    }

    public boolean isEmpty() {
        return commands.isEmpty();
    }

    // 1. Какая инструкция встречается наиболее часто
    public CommandType mostPopularInstruction() {
        return commands.stream()
                .map(Command::getType)
                .collect(Collectors.groupingBy(
                        commandType -> commandType,
                        Collectors.counting()
                ))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    // 2. Диапазон адресов памяти, используемых в программе
    public String getMemoryAddressRange() {
        Set<Integer> addresses = extractMemoryAddresses();

        if (addresses.isEmpty()) {
            return "No memory addresses used";
        }

        int min = Collections.min(addresses);
        int max = Collections.max(addresses);

        return String.format("Memory addresses range: %d - %d (total: %d addresses)",
                min, max, addresses.size());
    }

    private Set<Integer> extractMemoryAddresses() {
        Set<Integer> addresses = new HashSet<>();

        for (Command command : commands) {
            switch (command.getType()) {
                case LD:
                case ST:
                    if (command.getOperands().length >= 2) {
                        try {
                            addresses.add(Integer.parseInt(command.getOperand(1)));
                        } catch (NumberFormatException e) {
                            // Игнорируем некорректные адреса
                        }
                    }
                    break;
                case INIT:
                    if (command.getOperands().length >= 1) {
                        try {
                            addresses.add(Integer.parseInt(command.getOperand(0)));
                        } catch (NumberFormatException e) {
                            // Игнорируем некорректные адреса
                        }
                    }
                    break;
            }
        }

        return addresses;
    }

    // 3. Список инструкций, упорядоченный по частоте появления
    public List<Map.Entry<CommandType, Long>> getInstructionsByFrequency() {
        return commands.stream()
                .map(Command::getType)
                .collect(Collectors.groupingBy(
                        commandType -> commandType,
                        Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<CommandType, Long>comparingByValue().reversed())
                .collect(Collectors.toList());
    }

    // Реализация Iterable для итерации по командам
    @Override
    public Iterator<Command> iterator() {
        return commands.iterator();
    }

    // Дополнительный метод для получения массива команд (для обратной совместимости)
    public Command[] toArray() {
        return commands.toArray(new Command[0]);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Program (").append(commands.size()).append(" commands):\n");
        for (int i = 0; i < commands.size(); i++) {
            sb.append(i).append(": ").append(commands.get(i)).append("\n");
        }
        return sb.toString();
    }
}