package hu.hevi.note.io.web.controller;

import hu.hevi.note.io.web.request.AddRequest;
import hu.hevi.note.io.web.response.NodeResponse;
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
        return nodeService.addNote(requestBody.getContent(), Arrays.asList(requestBody.getPeerId()));
    }
}