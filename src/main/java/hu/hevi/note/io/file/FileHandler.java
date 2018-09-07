package hu.hevi.note.io.file;

import hu.hevi.note.note.domain.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

@Component
public class FileHandler {

    @Autowired
    private FileFormatUtils fileFormatUtils;
    @Autowired
    private NoteFormatter noteFormatter;

    private final String FILENAME = getFilename();

    @PostConstruct
    private void init() throws IOException {
        String homeDir = System.getProperty("user.home") + File.separator;

        String directoryName = ".note" + File.separator;

        File directory = new File(homeDir + directoryName);
        if (!directory.exists()) {
            boolean mkdirResult = directory.mkdir();
            if (!mkdirResult) {
                throw new IOException("Couldn't create directory in: " + directory);
            }
        }

        String fileName = "note.txt" + File.separator;
        File file = new File(homeDir + directoryName + fileName);
        if (!file.exists()) {
            boolean newFileResult = file.createNewFile();
            if (!newFileResult) {
                throw new IOException("Couldn't create file in: " + directory);
            }
        }
    }

    private String getFilename() {
        String home = System.getProperty("user.home") + File.separator;
        String noteFile = ".note" + File.separator + "note.nt";
        return home + noteFile;
    }

    public List<String> readLines() throws IOException {
        List<String> lines = new LinkedList<>();

        try (Stream<String> stream = Files.lines(Paths.get(FILENAME))) {
            stream.forEach(line -> lines.add(line));
        } catch (IOException e) {
            throw e;
        }
        return lines;
    }

    public void write(List<Note> notes) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(FILENAME);

        try {
            notes.stream().forEach(n -> {
                byte[] bytes = noteFormatter.format(n).getBytes();
                try {
                    outputStream.write(bytes);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        } finally {
            // Dirty workaround to be able to close the outputStream if RTEx. thrown from lambda
            outputStream.close();
        }
    }
}
