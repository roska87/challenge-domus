package domus.challenge.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class DirectorsResponse {
    private List<String> directors;
}
