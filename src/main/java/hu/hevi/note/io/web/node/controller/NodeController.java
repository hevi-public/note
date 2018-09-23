package hu.hevi.note.io.web.node.controller;

import hu.hevi.note.io.web.node.request.AddRequest;
import hu.hevi.note.io.web.node.response.NodeResponse;
import hu.hevi.note.note.domain.Note;
import hu.hevi.note.note.service.NodeService;
import hu.hevi.note.note.service.type.QueryType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/node")
public class NodeController {

    @Autowired
    private NodeService nodeService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<NodeResponse> getGraphData() throws IOException {

        List<Note> notes = nodeService.getNotes(QueryType.FORCE_UPDATE);
        List<NodeResponse> nodes = notes.stream()
                .map(n -> new NodeResponse(n.getId(), n.getContent(), n.getType().ordinal()))
                .collect(Collectors.toList());

        return nodes;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public int add(@RequestBody AddRequest requestBody) throws IOException {
        // TODO sanitize input
        if (StringUtils.isBlank(requestBody.getContent())){
            return 0;
        }

        List<Integer> peerIds;
        if (requestBody.getPeerIds() != null && requestBody.getPeerIds().contains(",")) {
            peerIds = Arrays.asList(requestBody.getPeerIds().split(",")).stream().map(id -> Integer.parseInt(id)).collect(Collectors.toList());
        } else {
            peerIds = Arrays.asList(Integer.parseInt(requestBody.getPeerIds()));
        }

        return nodeService.addNote(requestBody.getContent(), peerIds);
    }
}