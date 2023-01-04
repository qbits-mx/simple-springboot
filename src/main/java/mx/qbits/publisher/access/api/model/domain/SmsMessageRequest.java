package mx.qbits.publisher.access.api.model.domain;

import lombok.Data;

@Data
public class SmsMessageRequest {
    private String telefono;
    private String mensaje;
}
