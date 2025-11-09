package com.company.vm;

import com.company.vm.command.Command;
import com.company.vm.core.Program;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProgramTest {

    @Test
    void testProgramOperations() {
        Program prog = new Program();

        // Тестирование добавления команд
        prog.add(new Command("init 10 20"));
        prog.add(new Command("ld a 10"));
        prog.add(new Command("ld b 20"));
        prog.add(new Command("add"));

        assertEquals(4, prog.size());
        assertFalse(prog.isEmpty());

        // Тестирование итерации
        int count = 0;
        for (Command cmd : prog) {
            assertNotNull(cmd);
            count++;
        }
        assertEquals(4, count);

        // Тестирование анализа
        assertEquals(com.company.vm.command.CommandType.LD, prog.mostPopularInstruction());
        assertTrue(prog.getMemoryAddressRange().contains("10"));
    }

    @Test
    void testEmptyProgram() {
        Program prog = new Program();

        assertTrue(prog.isEmpty());
        assertNull(prog.mostPopularInstruction());
        assertEquals("No memory addresses used", prog.getMemoryAddressRange());
    }
}