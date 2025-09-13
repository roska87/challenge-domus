package domus.challenge.model;

import lombok.Data;
import java.util.List;

@Data
public class ApiMoviesPage {

    private int page;
    private int perPage;
    private int total;
    private int totalPages;
    private List<Movie> data;

}
