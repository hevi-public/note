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
                .map(n -> new NodeResponse(n.getId(), n.getDate().format(formatter), n.getContent(), n.getType().name(), n.getType().ordinal()))
                .collect(Collectors.toList());

        return nodes;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public int add(@RequestBody AddRequest requestBody) throws IOException {
        // TODO sanitize input
        if (StringUtils.isBlank(requestBody.getContent()) ){
            return 0;
        }

        List<Integer> peerIds;
        if (isNotBlank(requestBody.getPeerIds()) && requestBody.getPeerIds().contains(",")) {
            peerIds = Arrays.asList(requestBody.getPeerIds().split(",")).stream().map(id -> Integer.parseInt(id)).collect(Collectors.toList());
        } else if (isNotBlank(requestBody.getPeerIds()) && !requestBody.getPeerIds().contains(",")) {
            peerIds = Arrays.asList(Integer.parseInt(requestBody.getPeerIds()));
        } else {
            peerIds = new ArrayList<>();
        }

        return nodeService.addNote(requestBody.getContent(), peerIds);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public int delete(@PathVariable String id) throws IOException {
        Integer nodeId = Integer.parseInt(id);
        nodeService.deleteNode(nodeId);
        return 0;
    }
}