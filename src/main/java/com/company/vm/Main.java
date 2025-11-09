package com.company.vm;

import com.company.vm.command.Command;
import com.company.vm.command.CommandType;
import com.company.vm.core.Executer;
import com.company.vm.core.Program;
import com.company.vm.cpu.BCpu;
import com.company.vm.cpu.ICpu;

import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--gui")) {
            // Запуск GUI версии
            startGuiVersion();
        } else if (args.length > 0 && args[0].equals("--console")) {
            // Запуск консольной версии
            startConsoleVersion();
        } else {
            // Интерактивный выбор
            chooseVersion();
        }
    }

    private static void startGuiVersion() {
        System.out.println("Запуск графического интерфейса...");
        com.company.vm.gui.CpuEmulator.main(new String[]{});
    }

    private static void startConsoleVersion() {
        System.out.println("=== Виртуальная машина - Консольная версия ===");

        // Создание программы
        Program prog = new Program();
        prog.add(new Command("init 10 20"));
        prog.add(new Command("init 11 25"));
        prog.add(new Command("ld a 10"));
        prog.add(new Command("ld b 11"));
        prog.add(new Command("ld c 11"));
        prog.add(new Command("add"));
        prog.add(new Command("mv a d"));
        prog.add(new Command("add"));
        prog.add(new Command("print"));

        // Демонстрация итерации
        System.out.println("=== Итерация по программе ===");
        for (Command c : prog) {
            System.out.println(c);
        }

        // Анализ программы
        System.out.println("\n=== Анализ программы ===");
        System.out.println("Всего команд: " + prog.size());
        System.out.println("Самая частая инструкция: " + prog.mostPopularInstruction());
        System.out.println(prog.getMemoryAddressRange());

        System.out.println("\n=== Инструкции по частоте ===");
        for (Map.Entry<CommandType, Long> entry : prog.getInstructionsByFrequency()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + " раз");
        }

        // Выполнение программы
        System.out.println("\n=== Выполнение программы ===");
        ICpu cpu = BCpu.build();
        Executer exec = new Executer(cpu);
        exec.runStepByStep(prog);
    }

    private static void chooseVersion() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Выберите версию:");
        System.out.println("1 - Графический интерфейс (GUI)");
        System.out.println("2 - Консольная версия");
        System.out.print("Ваш выбор (1/2): ");

        String choice = scanner.nextLine().trim();

        if (choice.equals("1")) {
            startGuiVersion();
        } else if (choice.equals("2")) {
            startConsoleVersion();
        } else {
            System.out.println("Неверный выбор. Запускаю графический интерфейс...");
            startGuiVersion();
        }

        scanner.close();
    }
}