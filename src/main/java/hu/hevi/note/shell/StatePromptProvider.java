package hu.hevi.note.shell;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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

        AttributedStyle foreground = AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW);
        switch (shellState.getState()) {
            case FIND:
                foreground = AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN);
                prompt.add(new AttributedString(shellState.getState().name().toUpperCase(), foreground));
                break;
            case ADD:
                foreground = AttributedStyle.DEFAULT.foreground(AttributedStyle.BRIGHT);
                prompt.add(new AttributedString(shellState.getState().name().toUpperCase(), foreground));
                break;
            case TAG_CURRENT_NOTE:
                foreground = AttributedStyle.DEFAULT.foreground(AttributedStyle.BLUE);
                String[] split = shellState.getState().name().split("_");
                List<String> splitName = Arrays.asList(split);
                String readableName = splitName.stream().collect(Collectors.joining(" "));
                prompt.add(new AttributedString(readableName.toUpperCase(), foreground));
                break;
            case COMMAND:
                foreground = AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW);
                prompt.add(new AttributedString(shellState.getState().name().toUpperCase(), foreground));
                break;
        }

        prompt.add(new AttributedString(":>",
                AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW)));

        return new AttributedString("").join(new AttributedString(" "), prompt);
    }
}