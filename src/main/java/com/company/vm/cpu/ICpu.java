package com.company.vm.cpu;

import com.company.vm.core.Register;

public interface ICpu {
    void exec(com.company.vm.command.Command command);
    int getRegister(Register reg);
    int getMemory(int address);
    void setMemory(int address, int value);
    void printRegisters();
}