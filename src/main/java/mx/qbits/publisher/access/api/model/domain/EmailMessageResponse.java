package mx.qbits.publisher.access.api.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailMessageResponse {
    private String to;
    private String subject;
}
