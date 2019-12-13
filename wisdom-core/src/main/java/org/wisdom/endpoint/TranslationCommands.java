package org.wisdom.endpoint;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;


@ShellComponent
public class TranslationCommands {

    @ShellMethod(value = "Add numbers.", key = "sum")
    public int add(int a, int b) {
        return a + b;
    }

}
