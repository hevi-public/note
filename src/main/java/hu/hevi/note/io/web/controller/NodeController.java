package hu.hevi.note.io.web.controller;

import hu.hevi.note.io.web.response.NodeResponse;
import hu.hevi.note.note.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class GraphController {

    @Autowired
    private NoteService nodeService;

    @RequestMapping(value = "/graph", method = RequestMethod.GET)
    public List<NodeResponse> greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        List<NodeResponse> nodeResponses = new ArrayList<>();



        model.addAttribute("name", name);
        return nodeResponses;
    }
}
