package hu.hevi.note.shell;

import hu.hevi.note.common.shell.ShellState;
import hu.hevi.note.note.command.NoteCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.shell.*;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@Primary
public class StateMethodTargetResolver implements MethodTargetResolver {

    @Autowired
    private ShellState shellState;
    @Autowired
    private NoteCommand noteCommand;
    @Autowired
    private ShellCommand shellCommand;
    @Autowired
    private ArgumentResolver argumentResolver;

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public MethodTarget getMethodTarget(Input input) {
        if (input.words().stream().collect(Collectors.joining("")).length() == 0) {
            return MethodTarget.of("mode", shellCommand, new Command.Help("default command"), () -> Availability.available(), argumentResolver);
        }

        switch (shellState.getState()) {
            case ADD:
                return MethodTarget.of("add", noteCommand, new Command.Help("add command"), () -> Availability.available(), argumentResolver);
            case FIND:
                return MethodTarget.of("find", noteCommand, new Command.Help("find command"), () -> Availability.available(), argumentResolver);
            case TAG_CURRENT_NOTE:
                return MethodTarget.of("tagLastNote", noteCommand, new Command.Help("Tag last note"), () -> Availability.available(), argumentResolver);
        }
        return null;
    }
}
