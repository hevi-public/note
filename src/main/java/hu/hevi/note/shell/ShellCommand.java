package hu.hevi.note.shell;

import hu.hevi.note.common.shell.ShellState;
import org.jline.terminal.Terminal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class ShellCommand {

    @Autowired
    private Terminal terminal;

    @Autowired
    private ShellState state;

    @ShellMethod(value = "Change mode", prefix = "", key = "")
    public String mode() {
        state.setState(state.getState().next());
        return null;
    }
}
