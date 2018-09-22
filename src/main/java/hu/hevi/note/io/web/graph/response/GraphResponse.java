package hu.hevi.note.io.web.graph.response;

import hu.hevi.note.io.web.node.response.NodeResponse;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor
public class GraphResponse {

    List<NodeResponse> nodes;
    List<EdgeResponse> edges;

}
