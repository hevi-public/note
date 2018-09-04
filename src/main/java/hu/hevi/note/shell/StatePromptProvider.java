package hu.hevi.note.shell;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

@Component
public class StatePromptProvider implements PromptProvider {

    @Autowired
    private ShellState shellState;

    @Override
    public AttributedString getPrompt() {
        return new AttributedString(shellState.getState().name() + " :>",
                AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
    }
}