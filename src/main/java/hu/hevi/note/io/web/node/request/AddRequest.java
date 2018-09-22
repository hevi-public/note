package hu.hevi.note.io.web.node.request;

import lombok.Data;

@Data
public class AddRequest {

    private int peerId;
    private String content;
}
