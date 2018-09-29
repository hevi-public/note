package hu.hevi.note.io.web.node.request;

import lombok.Data;

@Data
public class AddRequest {

    private String peerId;
    private String content;
}
