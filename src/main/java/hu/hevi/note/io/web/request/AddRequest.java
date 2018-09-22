package hu.hevi.note.io.web.request;

import lombok.Data;

@Data
public class AddRequest {

    private int peerId;
    private String content;
}
