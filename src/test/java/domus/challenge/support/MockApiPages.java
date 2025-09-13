package domus.challenge.support;

public class MockApiPages {

    private MockApiPages() {}

    public static String page1(int totalPages) {
        return """
                {
                  "page": 1,
                  "per_page": 2,
                  "total": 4,
                  "total_pages": %d,
                  "data": [
                    {
                      "Title": "Movie A","Year":"2012","Rated":"PG","Released":"2012-01-01",
                      "Runtime":"100","Genre":"Drama","Director":"Woody Allen","Writer":"","Actors":""
                    },
                    {
                      "Title": "Movie B","Year":"2013","Rated":"PG","Released":"2013-01-01",
                      "Runtime":"90","Genre":"Drama","Director":"Martin Scorsese","Writer":"","Actors":""
                    }
                  ]
                }
                """.formatted(totalPages);
    }

    public static String pageN(int pageNumber) {
        // Página 2 y 3 con más títulos para contar directores
        if (pageNumber == 2) {
            return """
                    {
                      "page": 2,
                      "per_page": 2,
                      "total": 4,
                      "total_pages": 3,
                      "data": [
                        {
                          "Title": "Movie C","Year":"2015","Rated":"PG","Released":"2015-01-01",
                          "Runtime":"110","Genre":"Drama","Director":"Woody Allen","Writer":"","Actors":""
                        },
                        {
                          "Title": "Movie D","Year":"2016","Rated":"PG","Released":"2016-01-01",
                          "Runtime":"95","Genre":"Drama","Director":"Martin Scorsese","Writer":"","Actors":""
                        }
                      ]
                    }
                    """;
        }
        // Página 3
        return """
                {
                  "page": 3,
                  "per_page": 2,
                  "total": 4,
                  "total_pages": 3,
                  "data": [
                    {
                      "Title": "Movie E","Year":"2017","Rated":"PG","Released":"2017-01-01",
                      "Runtime":"120","Genre":"Drama","Director":"Woody Allen","Writer":"","Actors":""
                    },
                    {
                      "Title": "Movie F","Year":"2018","Rated":"PG","Released":"2018-01-01",
                      "Runtime":"105","Genre":"Drama","Director":"Christopher Nolan","Writer":"","Actors":""
                    }
                  ]
                }
                """;
    }

}
