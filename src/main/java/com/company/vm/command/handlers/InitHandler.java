package com.company.vm.command.handlers;

import com.company.vm.command.Command;
import com.company.vm.cpu.ICpu;

public class InitHandler implements CommandHandler {
    @Override
    public void execute(Command command, ICpu cpu) {
        int address = Integer.parseInt(command.getOperand(0));
        int value = Integer.parseInt(command.getOperand(1));
        cpu.setMemory(address, value);
    }
}