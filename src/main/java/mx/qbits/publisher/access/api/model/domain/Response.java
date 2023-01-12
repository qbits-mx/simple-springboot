package mx.qbits.publisher.access.api.model.domain;

import lombok.Data;

@Data
public class Response {
    private String user;
    private String hashed;
    private String olderThan;
    private String gen;
    private String error="no error";

    public boolean failed() {
        return !"no error".equals(this.error);
    }
}
