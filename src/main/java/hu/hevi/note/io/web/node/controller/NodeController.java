package hu.hevi.note.io.web.node.controller;

import hu.hevi.note.io.web.graph.response.EdgeResponse;
import hu.hevi.note.io.web.graph.response.GraphResponse;
import hu.hevi.note.io.web.node.request.AddRequest;
import hu.hevi.note.io.web.node.response.NodeResponse;
import hu.hevi.note.note.domain.NodeType;
import hu.hevi.note.note.domain.Note;
import hu.hevi.note.note.service.NodeService;
import hu.hevi.note.note.service.type.QueryType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@RestController
@RequestMapping("/node")
public class NodeController {

    @Autowired
    private NodeService nodeService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<NodeResponse> getGraphData() throws IOException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        List<Note> notes = nodeService.getNotes(QueryType.FORCE_UPDATE);
        List<NodeResponse> nodes = notes.stream()
                .map(n -> new NodeResponse(n))
                .collect(Collectors.toList());

        return nodes;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public NodeResponse add(@RequestBody AddRequest requestBody) throws IOException {
        // TODO sanitize input
        if (StringUtils.isBlank(requestBody.getContent())) {
            // TODO do something meaningful here
            return null;
        }

        List<Integer> peerIds;
        if (isNotBlank(requestBody.getPeerIds()) && requestBody.getPeerIds().contains(",")) {
            peerIds = Arrays.asList(requestBody.getPeerIds().split(",")).stream().map(id -> Integer.parseInt(id)).collect(Collectors.toList());
        } else if (isNotBlank(requestBody.getPeerIds()) && !requestBody.getPeerIds().contains(",")) {
            peerIds = Arrays.asList(Integer.parseInt(requestBody.getPeerIds()));
        } else {
            peerIds = new ArrayList<>();
        }

        return new NodeResponse(nodeService.addNote(requestBody.getContent(), peerIds));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public int delete(@PathVariable String id) throws IOException {
        Integer nodeId = Integer.parseInt(id);
        nodeService.deleteNode(nodeId);
        return 0;
    }

    @RequestMapping(value = "/join/{id}", method = RequestMethod.PATCH)
    public int join(@PathVariable Integer id, @RequestParam Integer toBeLinked) throws IOException {
        // Integer nodeId = Integer.parseInt(id);
        List<Note> notes = nodeService.getNotes(QueryType.CACHED);
        notes.stream().filter(note -> note.getId().equals(id)).findFirst().ifPresent(note -> {
            note.getTags().add(toBeLinked);
            try {
                nodeService.update();
            } catch (IOException e) {
                // TODO ??
                e.printStackTrace();
            }
        });
        return 0;
    }

    @RequestMapping(value = "/link/between/{from}/and/{to}", method = RequestMethod.DELETE)
    public int removeLink(@PathVariable Integer from, @PathVariable Integer to) throws IOException {
        // TODO Add validation
        List<Note> notes = nodeService.getNotes(QueryType.CACHED);
        notes.stream().filter(note -> note.getId().equals(from)).findFirst().ifPresent(note -> {
            note.getTags().remove(to);
            try {
                nodeService.update();
            } catch (IOException e) {
                // TODO ??
                e.printStackTrace();
            }
        });
        return 0;
    }

    // TODO might worth reorganize to not reference Graph package
    @RequestMapping(value = "/{id}/type/{type}", method = RequestMethod.PATCH)
    public GraphResponse changeType(@PathVariable Integer id, @PathVariable String type) throws IOException {

        NodeType nodeType = NodeType.valueOf(type);
        List<Note> notes = nodeService.getNotes(QueryType.CACHED);

        Optional<Note> optionalNote = notes.stream().filter(note -> note.getId().equals(id)).findFirst();
        if (optionalNote.isPresent()) {
            Note note = optionalNote.get();
            nodeService.deleteNode(note.getId());
            note.setType(nodeType);
            try {
                nodeService.addNote(note);
            } catch (IOException e) {
                // TODO ??
                e.printStackTrace();
            }
        }

        GraphResponse.GraphResponseBuilder graphResponseBuilder = GraphResponse.builder();

        NodeResponse nodeResponse = new NodeResponse(optionalNote.get());
        graphResponseBuilder.nodes(Arrays.asList(nodeResponse));

        List<EdgeResponse> edgeResponses = new LinkedList<>();
        optionalNote.ifPresent(note -> {
            note.getTags().forEach(to -> edgeResponses.add(new EdgeResponse(note.getId(), to)));
        });

        graphResponseBuilder.edges(edgeResponses);

        return graphResponseBuilder.build();
    }
}