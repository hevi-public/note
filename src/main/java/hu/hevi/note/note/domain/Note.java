package hu.hevi.note.note.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class Note {

    private Integer id;
    private LocalDateTime date;
    private String content;
    private NodeType type;
    private List<Integer> tags;

    public Note(Note note) {
        this.id = note.id;
        this.date = LocalDateTime.now();
        this.content = note.content;
        this.type = note.type;
        this.tags = note.tags;
    }

    public Note(int id, String content, NodeType type, List<Integer> tags) {
        this.id = id;
        this.date = LocalDateTime.now();
        this.content = content;
        this.type = type;
        this.tags = tags;
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

    public List<Integer> tags() {
        return tags != null ? tags : new ArrayList<>();
    }
}
