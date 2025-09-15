package domus.challenge.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class ApiMoviesPage {

    @JsonProperty("page")        private int page;
    @JsonProperty("per_page")    private int perPage;
    @JsonProperty("total")       private int total;
    @JsonProperty("total_pages") private int totalPages;
    @JsonProperty("data")        private List<Movie> data = Collections.emptyList(); // avoid null

}
