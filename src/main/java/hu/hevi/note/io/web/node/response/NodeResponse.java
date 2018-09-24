package hu.hevi.note.io.web.node.response;

import hu.hevi.note.note.domain.Note;
import lombok.AllArgsConstructor;
import lombok.Value;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.time.format.DateTimeFormatter;

@Value
@AllArgsConstructor
public class NodeResponse {

    private int id;
    private String date;
    private String label;
    private String type;
    private int group;
    private int value;

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public NodeResponse(Note node) {
        // TODO move value calculation away
        int value = 1;
        switch (node.getType()) {
            case NODE:
                value = 10 + (node.getTags().size() * 5);
                break;
            case CATEGORY:
                value = 50 + (node.getTags().size() * 30);
                break;
            default:
                throw new NotImplementedException();
        }

        // TODO make this more sophisticated
        //      weight references in both directions
        //      if necessary with different weight
        value += node.getTags().size() * 0.1;

        this.id = node.getId();
        this.date = node.getDate().format(formatter);
        this.label = node.getContent();
        this.type = node.getType().name();
        this.group = node.getType().ordinal();
        this.value = value;
    }
}
