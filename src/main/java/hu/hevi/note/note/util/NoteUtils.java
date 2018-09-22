package hu.hevi.note.note.util;

import hu.hevi.note.io.file.FileFormatUtils;
import hu.hevi.note.io.file.NoteFormatter;
import hu.hevi.note.note.domain.NodeType;
import hu.hevi.note.note.domain.Note;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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
                LocalDateTime date = LocalDateTime.parse(line.substring(line.indexOf(" ") + 1, line.indexOf(" |")));
                noteBuilder.date(date);

                String type = line.substring(line.indexOf(" |") + 2).trim();
                noteBuilder.type(NodeType.valueOf(type));
            } else if (line.startsWith("[")) {
                List<Integer> tags = getTags(line);
                noteBuilders.getLast().tags(tags);
            } else if (StringUtils.isNotBlank(line)) {
                noteBuilders.getLast().content(line);
            } else {
                if (noteBuilders.size() > 0) {
                    Note note = noteBuilders.getLast().build();
                    notes.add(note);
                    noteBuilders.removeLast();
                }
            }
        });
        return notes;
    }

    public List<Integer> getTags(String line) {
        String substring = line.trim().substring(1, line.length() - 1);
        if (StringUtils.isBlank(substring)) {
            return new ArrayList<>();
        }

        List<String> rawTags = Arrays.asList(substring.split(","));
        return rawTags.stream()
                        .map(t -> Integer.valueOf(t.trim()))
                        .collect(Collectors.toList());
    }

    public String asString(List<Note> notes) {
        return notes.stream().map(n -> noteFormatter.format(n)).collect(Collectors.joining());
    }
}
