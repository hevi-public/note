package hu.hevi.note.note.command;

import hu.hevi.note.note.domain.Note;
import hu.hevi.note.note.service.NodeService;
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
    private NodeService nodeService;
    @Autowired
    private Terminal terminal;

    @ShellMethod(value = "add", freetext = true)
    public String add(@ShellOption String content) throws IOException {
        int id = nodeService.addNote(content);
        shellState.setLastAddedNoteId(id);

        return getAddDisplayText(id, content);
    }

    @ShellMethod("Get notes")
    public String list() throws IOException {
        return getListDisplayText(nodeService.getNotesAsString());
    }

    @ShellMethod(value = "Find notes", freetext = true)
    public String find(@ShellOption String searchText) throws IOException {
        String found = nodeService.find(searchText);

        return getFindDisplayText(searchText, found);
    }

    @ShellMethod(value = "Tag note")
    public String tag(@ShellOption int noteId, @ShellOption String tag) throws IOException {
        Optional<Note> noteOptional = nodeService.findById(noteId);
        if (noteOptional.isPresent()) {
            Note note = noteOptional.get();
            note.tags().add(Integer.parseInt(tag));
            nodeService.update();

            return getTagSuccessDisplayText(noteId, tag);
        } else {
            return getTagErrorDisplayText(noteId, tag);
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

    private String getAddDisplayText(int id, String content) {
        StringBuilder sb = new StringBuilder();
        sb.append("-----------------------------------------");
        sb.append("\n");
        sb.append(">> " + "#");
        sb.append(id);
        sb.append(" -> ");
        sb.append(content);
        sb.append("\n");
        sb.append("\n");
        sb.toString();
        return sb.toString();
    }

    private String getListDisplayText(String notes) {
        StringBuilder sb = new StringBuilder();
        sb.append("-----------------------------------------");
        sb.append("\n");
        sb.append(">> list");
        sb.append("\n");
        sb.append(notes);
        sb.append("\n");
        sb.append("\n");
        return sb.toString();
    }

    private String getFindDisplayText(@ShellOption String searchText, String found) {
        StringBuilder sb = new StringBuilder();
        sb.append("-----------------------------------------");
        sb.append("\n");
        sb.append("?? " + searchText);
        sb.append("\n");
        sb.append("\n");
        sb.append(found);
        return sb.toString();
    }

    private String getTagSuccessDisplayText(@ShellOption int noteId, @ShellOption String tag) {
        StringBuilder sb = new StringBuilder();
        sb.append("-----------------------------------------");
        sb.append("\n");
        sb.append(String.format(">> %d <-> %s", noteId, tag));
        sb.append("\n");
        sb.append("\n");
        return sb.toString();
    }

    private String getTagErrorDisplayText(@ShellOption int noteId, @ShellOption String tag) {
        StringBuilder sb = new StringBuilder();
        sb.append("-----------------------------------------");
        sb.append("\n");
        sb.append(String.format("!! %d <-> %s. Note not found.", noteId, tag));
        sb.append("\n");
        sb.append("\n");
        return sb.toString();
    }
}