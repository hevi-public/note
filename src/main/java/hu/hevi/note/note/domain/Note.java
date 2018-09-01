package hu.hevi.note.note.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Value
@Builder
@AllArgsConstructor
public class Note {

    private int id;
    private LocalDateTime date;
    private String content;

    public Note(int id, String content) {
        this.id = id;
        this.date = LocalDateTime.now();
        this.content = content;
    }

    public boolean contains(String searchText) {
        List<String> searchTerms = Arrays.asList(searchText.split(" "));
        boolean matched = true;
        for (String searchTerm : searchTerms) {
            if (!content.toLowerCase().contains(searchTerm.toLowerCase())) {
                matched = false;
            }
        }
        return matched;
    }
}
