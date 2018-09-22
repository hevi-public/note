package hu.hevi.note.common.shell;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ShellState {

    private State state = State.COMMAND;
    private int lastAddedNoteId = 0;
}
