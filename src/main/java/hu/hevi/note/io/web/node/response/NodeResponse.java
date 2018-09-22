package hu.hevi.note.io.web.node.response;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class NodeResponse {

    private int id;
    private String label;
    private int group;
}
