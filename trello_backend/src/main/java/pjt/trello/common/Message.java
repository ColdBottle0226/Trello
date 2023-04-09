package pjt.trello.common;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Message {

    private StatusEnum status;
    private String message;
    private Object data;
    private Map<String, Object> additionalData;

    public Message() {
        this.status = StatusEnum.BAD_REQUEST;
        this.data = null;
        this.message = null;
        this.additionalData = new HashMap<>();
    }

    public Message(StatusEnum status, String message) {
        this.status = status;
        this.message = message;
        this.additionalData = new HashMap<>();
    }

    public Message(StatusEnum status, Object data) {
        this.status = status;
        this.data = data;
        this.additionalData = new HashMap<>();
    }

    public void addData(String key, Object value) {
        this.additionalData.put(key, value);
    }
}
