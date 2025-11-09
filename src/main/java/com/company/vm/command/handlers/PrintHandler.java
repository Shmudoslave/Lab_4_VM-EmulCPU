package com.company.vm.command.handlers;

import com.company.vm.command.Command;
import com.company.vm.cpu.ICpu;

public class PrintHandler implements CommandHandler {
    @Override
    public void execute(Command command, ICpu cpu) {
        cpu.printRegisters();
    }
}