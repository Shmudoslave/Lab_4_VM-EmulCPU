package com.company.vm;

import com.company.vm.command.Command;
import com.company.vm.command.CommandType;
import com.company.vm.core.Executer;
import com.company.vm.core.Register;
import com.company.vm.cpu.BaseCpu;
import com.company.vm.cpu.ICpu;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CpuTest {
    private ICpu cpu;

    @BeforeEach
    void setUp() {
        cpu = new BaseCpu();
    }

    @Test
    void testInitAndLoad() {
        cpu.exec(new Command(CommandType.INIT, "5", "100"));
        cpu.exec(new Command(CommandType.LD, "a", "5"));

        assertEquals(100, ((BaseCpu) cpu).getRegister(Register.A));
    }

    @Test
    void testAddOperation() {
        cpu.exec(new Command(CommandType.INIT, "0", "10"));
        cpu.exec(new Command(CommandType.INIT, "1", "20"));
        cpu.exec(new Command(CommandType.LD, "a", "0"));
        cpu.exec(new Command(CommandType.LD, "b", "1"));
        cpu.exec(new Command(CommandType.ADD));

        assertEquals(30, ((BaseCpu) cpu).getRegister(Register.D));
    }

    @Test
    void testMoveOperation() {
        cpu.exec(new Command(CommandType.INIT, "0", "50"));
        cpu.exec(new Command(CommandType.LD, "a", "0"));
        cpu.exec(new Command(CommandType.MV, "b", "a"));

        assertEquals(50, ((BaseCpu) cpu).getRegister(Register.B));
    }

    @Test
    void testStoreOperation() {
        cpu.exec(new Command(CommandType.INIT, "0", "77"));
        cpu.exec(new Command(CommandType.LD, "a", "0"));
        cpu.exec(new Command(CommandType.ST, "a", "10"));

        assertEquals(77, cpu.getMemory(10));
    }

    @Test
    void testComplexProgram() {
        Command[] program = {
                new Command(CommandType.INIT, "1", "8"),
                new Command(CommandType.INIT, "2", "2"),
                new Command(CommandType.LD, "a", "1"),
                new Command(CommandType.LD, "b", "2"),
                new Command(CommandType.MULT),
                new Command(CommandType.MV, "c", "d")
        };

        Executer exec = new Executer(cpu);
        exec.run(program);

        assertEquals(16, ((BaseCpu) cpu).getRegister(Register.C));
    }
}