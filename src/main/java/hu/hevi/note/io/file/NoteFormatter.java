package hu.hevi.note.io.file;

import hu.hevi.note.note.domain.Note;

public interface NoteFormatter {
    String format(Note note);
}
