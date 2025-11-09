package com.company.vm.command.handlers;

import com.company.vm.command.Command;
import com.company.vm.core.Register;
import com.company.vm.cpu.ICpu;
import com.company.vm.cpu.BaseCpu;

public class MultHandler implements CommandHandler {
    @Override
    public void execute(Command command, ICpu cpu) {
        int a = ((BaseCpu) cpu).getRegister(Register.A);
        int b = ((BaseCpu) cpu).getRegister(Register.B);
        ((BaseCpu) cpu).setRegister(Register.D, a * b);
    }
}