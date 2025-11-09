package com.company.vm.command.handlers;

import com.company.vm.command.Command;
import com.company.vm.cpu.ICpu;

public interface CommandHandler {
    void execute(Command command, ICpu cpu);
}