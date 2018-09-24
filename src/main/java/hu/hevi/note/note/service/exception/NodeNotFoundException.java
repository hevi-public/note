package hu.hevi.note.note.service.exception;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class NodeNotFoundException extends RuntimeException {

    private Integer id;
}
