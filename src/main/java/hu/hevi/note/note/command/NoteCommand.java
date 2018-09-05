package hu.hevi.note.note.command;

import hu.hevi.note.note.domain.Note;
import hu.hevi.note.note.service.NoteService;
import hu.hevi.note.shell.ShellState;
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
    private ShellState shellState;
    @Autowired
    private NoteService noteService;
    @Autowired
    private Terminal terminal;

    @ShellMethod(value = "add", freetext = true)
    public String add(@ShellOption String content) throws IOException {
        int id = noteService.addNote(content);
        shellState.setLastAddedNoteId(id);

        StringBuilder sb = new StringBuilder();
        sb.append("-----------------------------------------");
        sb.append("\n");
        sb.append(">> " + "#");
        sb.append(id);
        sb.append(" -> ");
        sb.append(content);
        sb.append("\n");
        sb.append("\n");
        return sb.toString();
    }

    @ShellMethod("Get notes")
    public String list() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("-----------------------------------------");
        sb.append("\n");
        sb.append(">> list");
        sb.append("\n");
        sb.append(noteService.getNotesAsString());
        sb.append("\n");
        sb.append("\n");
        return sb.toString();
    }

    @ShellMethod(value = "Find notes", freetext = true)
    public String find(@ShellOption String searchText) throws IOException {
        String found = noteService.find(searchText);

        StringBuilder sb = new StringBuilder();
        sb.append("-----------------------------------------");
        sb.append("\n");
        sb.append("?? " + searchText);
        sb.append("\n");
        sb.append("\n");
        sb.append(found);
        return sb.toString();
    }

    @ShellMethod(value = "Tag note")
    public String tag(@ShellOption int noteId, @ShellOption String tag) throws IOException {
        Optional<Note> noteOptional = noteService.findById(noteId);
        if (noteOptional.isPresent()) {
            Note note = noteOptional.get();
            note.tags().add(tag);
            noteService.update();

            StringBuilder sb = new StringBuilder();
            sb.append("-----------------------------------------");
            sb.append("\n");
            sb.append(String.format(">> %i <-> %s", noteId, tag));
            sb.append("\n");
            sb.append("\n");
            return sb.toString();
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("-----------------------------------------");
            sb.append("\n");
            sb.append(String.format("!! %i <-> %s. Note not found.", noteId, tag));
            sb.append("\n");
            sb.append("\n");
            return sb.toString();
        }
    }

    @ShellMethod(value = "Tag last note", freetext = true)
    public String tagLastNote(@ShellOption String tag) throws IOException {
        if (shellState.getLastAddedNoteId() == 0) {
            return "!! There is no previous note in this session to be tagged. Try adding one";
        } else {
            return this.tag(shellState.getLastAddedNoteId(), tag);
        }
    }
}