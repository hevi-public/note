package hu.hevi.note.io.web.graph.controller;

import hu.hevi.note.io.web.graph.response.EdgeResponse;
import hu.hevi.note.io.web.graph.response.GraphResponse;
import hu.hevi.note.io.web.node.response.NodeResponse;
import hu.hevi.note.note.domain.Note;
import hu.hevi.note.note.service.NodeService;
import hu.hevi.note.note.service.type.QueryType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/graph")
public class GraphController {

    @Autowired
    private NodeService nodeService;

    @RequestMapping(method = RequestMethod.GET)
    public GraphResponse getGraphData() throws IOException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        List<Note> notes = nodeService.getNotes(QueryType.FORCE_UPDATE);
        List<NodeResponse> nodes = notes.stream()
                .map(n -> new NodeResponse(n.getId(), n.getDate().format(formatter), n.getContent(), n.getType().name(), n.getType().ordinal()))
                .collect(Collectors.toList());


        List<EdgeResponse> edges = new ArrayList<>();
        notes.forEach(n -> {
            int id = n.getId();
            n.getTags().forEach(t -> edges.add(new EdgeResponse(id, t)));
        });

        return new GraphResponse(nodes, edges);
        
    }
}
