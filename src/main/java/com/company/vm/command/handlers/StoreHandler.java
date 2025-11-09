package com.company.vm.command.handlers;

import com.company.vm.command.Command;
import com.company.vm.core.Register;
import com.company.vm.cpu.ICpu;
import com.company.vm.cpu.BaseCpu;

public class StoreHandler implements CommandHandler {
    @Override
    public void execute(Command command, ICpu cpu) {
        Register reg = Register.valueOf(command.getOperand(0).toUpperCase());
        int address = Integer.parseInt(command.getOperand(1));
        int value = ((BaseCpu) cpu).getRegister(reg);
        cpu.setMemory(address, value);
    }
}