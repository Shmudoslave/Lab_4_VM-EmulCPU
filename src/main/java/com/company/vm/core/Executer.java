package com.company.vm.core;

import com.company.vm.command.Command;
import com.company.vm.cpu.ICpu;

public class Executer {
    private final ICpu cpu;

    public Executer(ICpu cpu) {
        this.cpu = cpu;
    }

    // Старый метод для массива команд
    public void run(Command[] program) {
        for (Command command : program) {
            cpu.exec(command);
        }
    }

    // Новый метод для класса Program
    public void run(Program program) {
        for (Command command : program) {
            cpu.exec(command);
        }
    }

    public void runStepByStep(Command[] program) {
        System.out.println("=== Starting program execution ===");
        for (int i = 0; i < program.length; i++) {
            System.out.printf("Step %d: Executing: %s%n", i + 1, program[i]);
            cpu.exec(program[i]);
        }
        System.out.println("=== Program execution completed ===");
    }

    // Новый метод для пошагового выполнения Program
    public void runStepByStep(Program program) {
        System.out.println("=== Starting program execution ===");
        int step = 1;
        for (Command command : program) {
            System.out.printf("Step %d: Executing: %s%n", step++, command);
            cpu.exec(command);
        }
        System.out.println("=== Program execution completed ===");
    }
}