-- Limpieza por si corren tests en el mismo proceso
DELETE FROM movies;

-- Denis Villeneuve (5)  -> >2 y >4
INSERT INTO movies (title, year, rated, released, runtime, genre, director, writer, actors) VALUES
('Incendies', 2010, 'R', '2011-01-12', '131', 'Drama', 'Denis Villeneuve', 'Denis Villeneuve', 'Cast'),
('Prisoners', 2013, 'R', '2013-09-20', '153', 'Thriller', 'Denis Villeneuve', 'Aaron Guzikowski', 'Cast'),
('Sicario', 2015, 'R', '2015-10-02', '121', 'Action', 'Denis Villeneuve', 'Taylor Sheridan', 'Cast'),
('Arrival', 2016, 'PG-13', '2016-11-11', '116', 'Sci-Fi', 'Denis Villeneuve', 'Eric Heisserer', 'Cast'),
('Dune', 2021, 'PG-13', '2021-10-22', '155', 'Adventure', 'Denis Villeneuve', 'Jon Spaihts', 'Cast');

-- Christopher Nolan (3) -> >2 pero no >4
INSERT INTO movies (title, year, rated, released, runtime, genre, director, writer, actors) VALUES
('Inception', 2010, 'PG-13', '2010-07-16', '148', 'Sci-Fi', 'Christopher Nolan', 'Christopher Nolan', 'Cast'),
('Interstellar', 2014, 'PG-13', '2014-11-07', '169', 'Sci-Fi', 'Christopher Nolan', 'Jonathan Nolan', 'Cast'),
('Dunkirk', 2017, 'PG-13', '2017-07-21', '106', 'War', 'Christopher Nolan', 'Christopher Nolan', 'Cast');

-- Martin Scorsese (2) -> no >2
INSERT INTO movies (title, year, rated, released, runtime, genre, director, writer, actors) VALUES
('Hugo', 2011, 'PG', '2011-11-23', '126', 'Drama', 'Martin Scorsese', 'John Logan', 'Cast'),
('The Irishman', 2019, 'R', '2019-11-27', '209', 'Crime', 'Martin Scorsese', 'Steven Zaillian', 'Cast');

-- Greta Gerwig (1) -> no >2
INSERT INTO movies (title, year, rated, released, runtime, genre, director, writer, actors) VALUES
('Barbie', 2023, 'PG-13', '2023-07-21', '114', 'Comedy', 'Greta Gerwig', 'Greta Gerwig', 'Cast');

-- Extras para superar 10 filas (paginación)
INSERT INTO movies (title, year, rated, released, runtime, genre, director, writer, actors) VALUES
('Jojo Rabbit', 2019, 'PG-13', '2019-10-18', '108', 'Comedy', 'Taika Waititi', 'Taika Waititi', 'Cast'),
('Parasite', 2019, 'R', '2019-05-30', '132', 'Thriller', 'Bong Joon-ho', 'Bong Joon-ho', 'Cast');
