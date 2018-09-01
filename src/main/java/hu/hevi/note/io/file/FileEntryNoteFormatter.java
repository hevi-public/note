package hu.hevi.note.io.file;

import hu.hevi.note.note.domain.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class FileEntryNoteFormatter implements NoteFormatter {

    @Autowired
    private FileFormatUtils fileFormatUtils;

    @Override
    public String format(Note note) {
        DateTimeFormatter formatter = fileFormatUtils.getDateTimeFormatter();
        String formatDateTime = note.getDate().format(formatter);

        StringBuilder sb = new StringBuilder();
        sb.append("#");
        sb.append(note.getId());
        sb.append(" ");
        sb.append(formatDateTime);
        sb.append("\n");
        sb.append(note.getContent());
        sb.append("\n");
        sb.append("\n");
        return sb.toString();
    }
}
