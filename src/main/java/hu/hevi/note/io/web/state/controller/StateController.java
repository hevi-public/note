package hu.hevi.note.io.web.state.controller;

import hu.hevi.note.common.shell.ShellState;
import hu.hevi.note.common.shell.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/state")
public class StateController {

    @Autowired
    private ShellState state;

    @RequestMapping(value = "/next", method = RequestMethod.GET)
    public State getState() {
        state.setState(state.getState().next());
        return state.getState();
    }
}
