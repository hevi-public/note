package hu.hevi.note.io.web.graph.response;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class EdgeResponse {

    private int from;
    private int to;
}
