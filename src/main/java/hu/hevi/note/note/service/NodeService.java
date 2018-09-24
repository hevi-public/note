package hu.hevi.note.note.service;

import hu.hevi.note.io.file.FileHandler;
import hu.hevi.note.note.domain.NodeType;
import hu.hevi.note.note.service.exception.NodeNotFoundException;
import hu.hevi.note.note.service.type.QueryType;
import hu.hevi.note.note.util.NoteUtils;
import hu.hevi.note.note.domain.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class NodeService {

    @Autowired
    private FileHandler fileHandler;
    @Autowired
    private NoteUtils noteUtils;

    private List<Note> cachedNotes;

    @PostConstruct
    private void setUp() throws IOException {
        cachedNotes = readNodesFromFile();
    }

    public List<Note> getNotes(QueryType queryType) throws IOException {
        switch (queryType) {
            case CACHED:
                break;
            case FORCE_UPDATE:
                cachedNotes = readNodesFromFile();
                return cachedNotes;
        }
        return cachedNotes;
    }

    public int addNote(Note note) throws IOException {
        OptionalInt optionalMax = cachedNotes.stream().mapToInt(n -> n.getId()).max();
        int maxId = optionalMax.equals(OptionalInt.empty()) ? 0 : optionalMax.getAsInt();

        int id = maxId + 1;
        cachedNotes.add(note);
        update();
        return id;
    }

    public int addNote(String content) throws IOException {
        OptionalInt optionalMax = cachedNotes.stream().mapToInt(n -> n.getId()).max();
        int maxId = optionalMax.equals(OptionalInt.empty()) ? 0 : optionalMax.getAsInt();

        int id = maxId + 1;
        Note note = new Note(id, content, NodeType.NODE, new ArrayList<>());
        cachedNotes.add(note);
        update();
        return id;
    }

    public Note addNote(String content, List<Integer> connections) throws IOException {
        OptionalInt optionalMax = cachedNotes.stream().mapToInt(n -> n.getId()).max();
        int maxId = optionalMax.equals(OptionalInt.empty()) ? 0 : optionalMax.getAsInt();

        int id = maxId + 1;
        Note note = new Note(id, content, NodeType.NODE, connections);
        cachedNotes.add(note);
        update();
        return note;
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


    private List<Note> readNodesFromFile() throws IOException {
        List<String> lines = fileHandler.readLines();
        return noteUtils.convertToNotes(lines);
    }

    public void deleteNode(Integer id) {
        if (!contans(id)) {
            throw new NodeNotFoundException(id);
        }

        cachedNotes.stream()
                .filter(n -> n.getId().equals(id))
                .findFirst()
                .ifPresent(note -> {
                    cachedNotes.remove(note);
                    try {
                        this.update();
                    } catch (IOException e) {
                        // TODO ??
                        e.printStackTrace();
                    }
                });
    }

    private boolean contans(Integer id) {
        return cachedNotes.stream().filter(n -> n.getId().equals(id)).findFirst().isPresent();
    }
}
