package hu.hevi.note.shell;

import org.springframework.context.annotation.Primary;
import org.springframework.shell.ArgumentResolver;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Primary
public class FreeTextInputArgurmentumResolver implements ArgumentResolver {

    @Override
    public List<String> wordsForArguments(String command, List<String> words) {
        return words;
    }
}
