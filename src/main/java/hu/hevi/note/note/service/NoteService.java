package hu.hevi.note.note.service;

import hu.hevi.note.io.file.FileHandler;
import hu.hevi.note.note.util.NoteUtils;
import hu.hevi.note.note.domain.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class NoteService {

    @Autowired
    private FileHandler fileHandler;
    @Autowired
    private NoteUtils noteUtils;

    private List<Note> cachedNotes;

    @PostConstruct
    private void setUp() throws IOException {
        List<String> lines = fileHandler.readLines();
        cachedNotes = noteUtils.convertToNotes(lines);
    }

    public List<Note> getNotes() {
        return cachedNotes;
    }

    public void addNote(String content) throws IOException {
        OptionalInt optionalMax = cachedNotes.stream().mapToInt(n -> n.getId()).max();
        int maxId = optionalMax.equals(OptionalInt.empty()) ? 0 : optionalMax.getAsInt();

        Note note = new Note(maxId + 1, content, Optional.empty());
        cachedNotes.add(note);
        fileHandler.write(cachedNotes);
    }

    public void update() throws IOException {
        fileHandler.write(cachedNotes);
    }

    public String find(final String searchText) {
        List<Note> notes = cachedNotes.stream().filter(n -> n.contains(searchText))
                .collect(Collectors.toList());
        return getNotesAsString(notes);
    }

    public Optional<Note> findById(final int id) {
        return cachedNotes.stream().filter(n -> n.getId().equals(id)).findFirst();
    }

    private String getNotesAsString(List<Note> notes) {
        return noteUtils.asString(notes);
    }

    public String getNotesAsString() {
        return noteUtils.asString(cachedNotes);
    }
}
