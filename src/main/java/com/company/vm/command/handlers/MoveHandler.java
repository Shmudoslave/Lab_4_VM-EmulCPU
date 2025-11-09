package com.company.vm.command.handlers;

import com.company.vm.command.Command;
import com.company.vm.core.Register;
import com.company.vm.cpu.ICpu;
import com.company.vm.cpu.BaseCpu;

public class MoveHandler implements CommandHandler {
    @Override
    public void execute(Command command, ICpu cpu) {
        Register dest = Register.valueOf(command.getOperand(0).toUpperCase());
        Register src = Register.valueOf(command.getOperand(1).toUpperCase());
        int value = ((BaseCpu) cpu).getRegister(src);
        ((BaseCpu) cpu).setRegister(dest, value);
    }
}