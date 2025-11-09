package com.company.vm.command.handlers;

import com.company.vm.command.Command;
import com.company.vm.core.Register;
import com.company.vm.cpu.ICpu;
import com.company.vm.cpu.BaseCpu;

public class DivHandler implements CommandHandler {
    @Override
    public void execute(Command command, ICpu cpu) {
        int a = ((BaseCpu) cpu).getRegister(Register.A);
        int b = ((BaseCpu) cpu).getRegister(Register.B);
        if (b == 0) {
            throw new ArithmeticException("Division by zero");
        }
        ((BaseCpu) cpu).setRegister(Register.D, a / b);
    }
}