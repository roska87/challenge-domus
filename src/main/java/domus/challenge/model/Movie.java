package domus.challenge.model;

import lombok.Data;

@Data
public class Movie {

    private String title;
    private String year;
    private String rated;
    private String released;
    private String runtime;
    private String genre;
    private String director;
    private String writer;
    private String actors;

}