package hu.hevi.note.io.web.response;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor
public class GraphResponse {

    List<NodeResponse> nodes;
    List<EdgeResponse> edges;

}
