package com.company.vm.cpu;

public class BCpu {
    public static ICpu build() {
        return new BaseCpu();
    }

    public static ICpu buildExtended() {
        return new BaseCpu(); // Можно вернуть ExtendedCpu для расширенной версии
    }
}