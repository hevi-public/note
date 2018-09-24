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
    private int mass;

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public NodeResponse(Note node) {
        // TODO move mass calculation away
        int mass = 1;
        switch (node.getType()) {
            case NODE:
                mass = 1;
                break;
            case CATEGORY:
                mass = 2;
                break;
            default:
                throw new NotImplementedException();
        }

        // TODO make this more sophisticated
        //      weight references in both directions
        //      if necessary with different weight
        mass += node.getTags().size() * 0.1;

        this.id = node.getId();
        this.date = node.getDate().format(formatter);
        this.label = node.getContent();
        this.type = node.getType().name();
        this.group = node.getType().ordinal();
        this.mass = mass;
    }
}
