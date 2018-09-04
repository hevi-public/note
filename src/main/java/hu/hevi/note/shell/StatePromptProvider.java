package hu.hevi.note.shell;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class StatePromptProvider implements PromptProvider {

    @Autowired
    private ShellState shellState;

    @Override
    public AttributedString getPrompt() {
        List<AttributedString> prompt = new LinkedList<>();
        if (shellState.getLastAddedNoteId() > 0) {
            prompt.add(new AttributedString("#" + shellState.getLastAddedNoteId(),
                    AttributedStyle.DEFAULT.foreground(AttributedStyle.CYAN)));
        }

        switch (shellState.getState()) {
            case FIND:
                prompt.add(new AttributedString(shellState.getState().name().toUpperCase(),
                        AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN)));
                break;
            case ADD:
                prompt.add(new AttributedString(shellState.getState().name().toUpperCase(),
                        AttributedStyle.DEFAULT.foreground(AttributedStyle.BRIGHT)));
                break;
            case COMMAND:
                prompt.add(new AttributedString(shellState.getState().name().toUpperCase(),
                        AttributedStyle.DEFAULT.foreground(AttributedStyle.MAGENTA)));
                break;
        }

        prompt.add(new AttributedString(":>",
                AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW)));

        return new AttributedString("").join(new AttributedString(" "), prompt);
    }
}