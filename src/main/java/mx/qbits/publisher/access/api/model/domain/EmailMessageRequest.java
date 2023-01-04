package mx.qbits.publisher.access.api.model.domain;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailMessageRequest {
    private String template;
    private String to;
    private String title;
    private Map<String,String> values;
}
