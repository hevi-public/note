package hu.hevi.note.io.web.node.controller;

import hu.hevi.note.io.web.node.request.AddRequest;
import hu.hevi.note.io.web.node.response.NodeResponse;
import hu.hevi.note.note.domain.Note;
import hu.hevi.note.note.service.NodeService;
import hu.hevi.note.note.service.type.QueryType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
}