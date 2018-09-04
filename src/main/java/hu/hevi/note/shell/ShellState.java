package hu.hevi.note.shell;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ShellState {

    private State state = State.COMMAND;
}
