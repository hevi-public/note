package hu.hevi.note.note.command;

import hu.hevi.note.note.service.NoteService;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.io.IOException;

@ShellComponent
public class NoteCommand {

    @Autowired
    private NoteService noteService;
    @Autowired
    private Terminal terminal;

    @ShellMethod(value = "Add a note", prefix = "")
    public String add(@ShellOption String content) throws IOException {
        terminal.puts(InfoCmp.Capability.clear_screen);
        noteService.addNote(content);
        return "";
    }

    @ShellMethod("Get notes")
    public String list() throws IOException {
        terminal.puts(InfoCmp.Capability.clear_screen);
        return noteService.getNotesAsString();
    }

    @ShellMethod(value = "Find notes", prefix = "")
    public String find(@ShellOption String searchText) throws IOException {
        terminal.puts(InfoCmp.Capability.clear_screen);
        return noteService.find(searchText);
    }
}