package hu.hevi.note.note.command;

import hu.hevi.note.note.domain.Note;
import hu.hevi.note.note.service.NoteService;
import org.jline.terminal.Terminal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.io.IOException;
import java.util.Optional;

@ShellComponent
public class NoteCommand {

    @Autowired
    private NoteService noteService;
    @Autowired
    private Terminal terminal;

    @ShellMethod(value = "add", freetext = true)
    public String add(@ShellOption String content) throws IOException {
        noteService.addNote(content);
        return "";
    }

    @ShellMethod("Get notes")
    public String list() throws IOException {
        return noteService.getNotesAsString();
    }

    @ShellMethod(value = "Find notes", freetext = true)
    public String find(@ShellOption String searchText) throws IOException {
        return noteService.find(searchText);
    }

    @ShellMethod(value = "Tag note")
    public String tag(@ShellOption int noteId, @ShellOption String tag) throws IOException {
        Optional<Note> noteOptional = noteService.findById(noteId);
        if (noteOptional.isPresent()) {
            Note note = noteOptional.get();
            note.tags().add(tag);
            noteService.update();
            return "";
        } else {
            return "Note not found.";
        }
    }
}