package ru.otus.hw.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.service.TestRunnerService;

@ShellComponent(value = "Test Application Commands")
@RequiredArgsConstructor
public class TestShellCommands {

    private final TestRunnerService testRunnerService;

    @ShellMethod(value = "Start testing", key = {"s", "start"})
    public void startTesting() {
        testRunnerService.run();
    }
}
