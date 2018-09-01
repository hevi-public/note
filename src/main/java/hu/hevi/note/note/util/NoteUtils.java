package hu.hevi.note.note.util;

import hu.hevi.note.io.file.FileFormatUtils;
import hu.hevi.note.io.file.NoteFormatter;
import hu.hevi.note.note.domain.Note;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class NoteUtils {

    @Autowired
    private FileFormatUtils fileFormatUtils;
    // TODO don't like this dependency here, try to remove
    @Autowired
    private NoteFormatter noteFormatter;

    public LinkedList<Note> convertToNotes(List<String> lines) {
        LinkedList<Note.NoteBuilder> noteBuilders = new LinkedList<>();
        LinkedList<Note> notes = new LinkedList<>();
        lines.stream().forEach(line -> {

            if (line.startsWith("#")) {
                Note.NoteBuilder noteBuilder = Note.builder();
                noteBuilders.add(noteBuilder);
                String id = line.substring(1, line.indexOf(" "));
                noteBuilder.id(Integer.parseInt(id));

                DateTimeFormatter formatter = fileFormatUtils.getDateTimeFormatter();
                LocalDateTime date = LocalDateTime.parse(line.substring(line.indexOf(" ") + 1));
                noteBuilder.date(date);
            } else if (StringUtils.isNotBlank(line)) {
                noteBuilders.getLast().content(line);
            } else {
                Note note = noteBuilders.getLast().build();
                notes.add(note);
                noteBuilders.removeLast();
            }
        });
        return notes;
    }

    public String asString(List<Note> notes) {
        return notes.stream().map(n -> noteFormatter.format(n)).collect(Collectors.joining());
    }
}
