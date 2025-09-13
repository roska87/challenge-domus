package domus.challenge.support;

import domus.challenge.model.ApiMoviesPage;
import domus.challenge.model.Movie;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public final class ExampleMoviesData {

    public static List<ApiMoviesPage> pages() {
        List<ApiMoviesPage> list = new ArrayList<>();
        list.add(page1());
        list.add(page2());
        list.add(page3());
        list.add(page4());
        list.add(page5());
        return list;
    }

    public static ApiMoviesPage page(int page) {
        return switch (page) {
            case 1 -> page1();
            case 2 -> page2();
            case 3 -> page3();
            case 4 -> page4();
            case 5 -> page5();
            default -> throw new IllegalArgumentException("No page " + page);
        };
    }

    // ---------- Pages (5) • per_page = 10 • total = 50 ----------
    private static ApiMoviesPage page1() {
        List<Movie> data = List.of(
                movie("Woody Film 01", "Woody Allen", "2012"),
                movie("Woody Film 02", "Woody Allen", "2013"),
                movie("Woody Film 03", "Woody Allen", "2014"),
                movie("Scorsese Film 01", "Martin Scorsese", "2010"),
                movie("Nolan Film 01", "Christopher Nolan", "2011"),
                movie("Villeneuve Film 01", "Denis Villeneuve", "2015"),
                movie("Fincher Film 01", "David Fincher", "2014"),
                movie("Gerwig Film 01", "Greta Gerwig", "2019"),
                movie("Tarantino Film 01", "Quentin Tarantino", "2012"),
                movie("Ridley Film 01", "Ridley Scott", "2017")
        );
        return base(1, data);
    }

    private static ApiMoviesPage page2() {
        List<Movie> data = List.of(
                movie("Woody Film 04", "Woody Allen", "2015"),
                movie("Woody Film 05", "Woody Allen", "2016"),
                movie("Woody Film 06", "Woody Allen", "2017"),
                movie("Bong Film 01", "Bong Joon-ho", "2019"),
                movie("Taika Film 01", "Taika Waititi", "2016"),
                movie("Jenkins Film 01", "Patty Jenkins", "2020"),
                movie("Chazelle Film 01", "Damien Chazelle", "2016"),
                movie("Cameron Film 01", "James Cameron", "2022"),
                movie("Spielberg Film 01", "Steven Spielberg", "2021"),
                movie("Anderson Film 01", "Wes Anderson", "2020")
        );
        return base(2, data);
    }

    private static ApiMoviesPage page3() {
        List<Movie> data = List.of(
                movie("Woody Film 07", "Woody Allen", "2018"),
                movie("Woody Film 08", "Woody Allen", "2019"),
                movie("Woody Film 09", "Woody Allen", "2020"),
                movie("Cuarón Film 01", "Alfonso Cuarón", "2013"),
                movie("Iñárritu Film 01", "Alejandro G. Iñárritu", "2014"),
                movie("Ang Lee Film 01", "Ang Lee", "2012"),
                movie("Bigelow Film 01", "Kathryn Bigelow", "2012"),
                movie("Spike Lee Film 01", "Spike Lee", "2018"),
                movie("Scorsese Film 02", "Martin Scorsese", "2016"),
                movie("Nolan Film 02", "Christopher Nolan", "2017")
        );
        return base(3, data);
    }

    private static ApiMoviesPage page4() {
        List<Movie> data = List.of(
                movie("Woody Film 10", "Woody Allen", "2021"),
                movie("Woody Film 11", "Woody Allen", "2022"),
                movie("Villeneuve Film 02", "Denis Villeneuve", "2021"),
                movie("Fincher Film 02", "David Fincher", "2020"),
                movie("Gerwig Film 02", "Greta Gerwig", "2023"),
                movie("Tarantino Film 02", "Quentin Tarantino", "2019"),
                movie("Ridley Film 02", "Ridley Scott", "2021"),
                movie("Bong Film 02", "Bong Joon-ho", "2017"),
                movie("Taika Film 02", "Taika Waititi", "2019"),
                movie("Jenkins Film 02", "Patty Jenkins", "2017")
        );
        return base(4, data);
    }

    private static ApiMoviesPage page5() {
        List<Movie> data = List.of(
                movie("Woody Film 12", "Woody Allen", "2023"),
                movie("Chazelle Film 02", "Damien Chazelle", "2018"),
                movie("Cameron Film 02", "James Cameron", "2023"),
                movie("Spielberg Film 02", "Steven Spielberg", "2018"),
                movie("Anderson Film 02", "Wes Anderson", "2014"),
                movie("Cuarón Film 02", "Alfonso Cuarón", "2018"),
                movie("Iñárritu Film 02", "Alejandro G. Iñárritu", "2015"),
                movie("Ang Lee Film 02", "Ang Lee", "2016"),
                movie("Bigelow Film 02", "Kathryn Bigelow", "2012"),
                movie("Spike Lee Film 02", "Spike Lee", "2020")
        );
        return base(5, data);
    }

    // ---------- Helpers ----------
    private static ApiMoviesPage base(int page, List<Movie> data) {
        ApiMoviesPage p = new ApiMoviesPage();
        p.setPage(page);
        p.setPerPage(10);
        p.setTotal(50);
        p.setTotalPages(5);
        p.setData(data);
        return p;
    }

    private static Movie movie(String title, String director, String releaseYear) {
        Movie m = new Movie();
        m.setTitle(title);
        m.setDirector(director);
        m.setReleaseYear(releaseYear);                 // String, como en el contrato del challenge
        m.setRated("PG-13");
        m.setReleased(releaseYear + "-01-01");
        m.setRuntime("100");
        m.setGenre("Drama");
        m.setWriter("");
        m.setActors("");
        return m;
    }
}