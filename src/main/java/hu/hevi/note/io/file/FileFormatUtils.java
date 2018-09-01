package hu.hevi.note.io.file;

import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class FileFormatUtils {

    public DateTimeFormatter getDateTimeFormatter() {
        return DateTimeFormatter.ISO_DATE_TIME;
    }
}
