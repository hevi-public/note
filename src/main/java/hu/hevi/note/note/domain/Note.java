package hu.hevi.note.note.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Value
@Builder
@AllArgsConstructor
public class Note {

    private Integer id;
    private LocalDateTime date;
    private String content;
    private List<String> tags;

    public Note(int id, String content, Optional<List<String>> tags) {
        this.id = id;
        this.date = LocalDateTime.now();
        this.content = content;
        if (tags.isPresent()) {
            this.tags = tags.get();
        } else {
            this.tags = new ArrayList<>();
        }
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

    public List<String> tags() {
        return tags != null ? tags : new ArrayList<>();
    }
}
