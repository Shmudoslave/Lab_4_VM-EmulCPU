package com.company.vm.cpu;

import com.company.vm.command.Command;
import com.company.vm.command.CommandType;
import com.company.vm.command.handlers.*;
import com.company.vm.core.Register;
import com.company.vm.exception.InvalidAddressException;

import java.util.HashMap;
import java.util.Map;

public class BaseCpu implements ICpu {
    private final int[] registers;
    private final int[] memory;
    private final Map<CommandType, CommandHandler> handlers;

    public BaseCpu() {
        this.registers = new int[4];
        this.memory = new int[1024];
        this.handlers = new HashMap<>();
        initializeHandlers();
    }

    private void initializeHandlers() {
        registerHandler(CommandType.LD, new LoadHandler());
        registerHandler(CommandType.ST, new StoreHandler());
        registerHandler(CommandType.MV, new MoveHandler());
        registerHandler(CommandType.INIT, new InitHandler());
        registerHandler(CommandType.PRINT, new PrintHandler());
        registerHandler(CommandType.ADD, new AddHandler());
        registerHandler(CommandType.SUB, new SubHandler());
        registerHandler(CommandType.MULT, new MultHandler());
        registerHandler(CommandType.DIV, new DivHandler());
    }

    protected void registerHandler(CommandType type, CommandHandler handler) {
        handlers.put(type, handler);
    }

    @Override
    public void exec(Command command) {
        CommandHandler handler = handlers.get(command.getType());
        if (handler != null) {
            handler.execute(command, this);
        } else {
            throw new IllegalArgumentException("Unknown command: " + command.getType());
        }
    }

    @Override
    public int getRegister(Register reg) {
        return registers[reg.ordinal()];
    }

    public void setRegister(Register reg, int value) {
        registers[reg.ordinal()] = value;
    }

    @Override
    public int getMemory(int address) {
        validateAddress(address);
        return memory[address];
    }

    @Override
    public void setMemory(int address, int value) {
        validateAddress(address);
        memory[address] = value;
    }

    @Override
    public void printRegisters() {
        System.out.printf("A=%d, B=%d, C=%d, D=%d%n",
                getRegister(Register.A),
                getRegister(Register.B),
                getRegister(Register.C),
                getRegister(Register.D));
    }

    private void validateAddress(int address) {
        if (address < 0 || address >= memory.length) {
            throw new InvalidAddressException(
                    String.format("Invalid memory address: %d. Valid range: 0-%d",
                            address, memory.length - 1));
        }
    }

    protected Map<CommandType, CommandHandler> getHandlers() {
        return handlers;
    }
}