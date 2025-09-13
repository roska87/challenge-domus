-- Limpieza inicial
DELETE FROM movies;

-- Helper: función release_year ciclada a partir de 2010
-- Nota: en H2 podemos usar 2010 + MOD(x, 15) para distribuir años 2010..2024
-- Campos simples para rated/released/runtime/genre/writer/actors

-- Christopher Nolan (120)
INSERT INTO movies (title, release_year, rated, released, runtime, genre, director, writer, actors)
SELECT 'Nolan Movie ' || x,
       2010 + MOD(x, 15),
       'PG-13',
       '2010-01-01',
       '150',
       'Action,Sci-Fi',
       'Christopher Nolan',
       'Christopher Nolan',
       'Actor A, Actor B'
FROM SYSTEM_RANGE(1, 120);

-- Denis Villeneuve (90)
INSERT INTO movies (title, release_year, rated, released, runtime, genre, director, writer, actors)
SELECT 'Villeneuve Movie ' || x,
       2010 + MOD(x, 15),
       'PG-13',
       '2011-01-01',
       '140',
       'Drama,Sci-Fi',
       'Denis Villeneuve',
       'Denis Villeneuve',
       'Actor C, Actor D'
FROM SYSTEM_RANGE(1, 90);

-- Martin Scorsese (80)
INSERT INTO movies (title, release_year, rated, released, runtime, genre, director, writer, actors)
SELECT 'Scorsese Movie ' || x,
       2010 + MOD(x, 15),
       'R',
       '2012-01-01',
       '160',
       'Crime,Drama',
       'Martin Scorsese',
       'Various',
       'Actor E, Actor F'
FROM SYSTEM_RANGE(1, 80);

-- Greta Gerwig (70)
INSERT INTO movies (title, release_year, rated, released, runtime, genre, director, writer, actors)
SELECT 'Gerwig Movie ' || x,
       2010 + MOD(x, 15),
       'PG-13',
       '2013-01-01',
       '120',
       'Comedy,Drama',
       'Greta Gerwig',
       'Greta Gerwig',
       'Actor G, Actor H'
FROM SYSTEM_RANGE(1, 70);

-- Ridley Scott (60)
INSERT INTO movies (title, release_year, rated, released, runtime, genre, director, writer, actors)
SELECT 'Ridley Scott Movie ' || x,
       2010 + MOD(x, 15),
       'R',
       '2014-01-01',
       '130',
       'Action,Drama',
       'Ridley Scott',
       'Various',
       'Actor I, Actor J'
FROM SYSTEM_RANGE(1, 60);

-- Quentin Tarantino (55)
INSERT INTO movies (title, release_year, rated, released, runtime, genre, director, writer, actors)
SELECT 'Tarantino Movie ' || x,
       2010 + MOD(x, 15),
       'R',
       '2015-01-01',
       '145',
       'Crime,Drama',
       'Quentin Tarantino',
       'Quentin Tarantino',
       'Actor K, Actor L'
FROM SYSTEM_RANGE(1, 55);

-- Bong Joon-ho (50)
INSERT INTO movies (title, release_year, rated, released, runtime, genre, director, writer, actors)
SELECT 'Bong Movie ' || x,
       2010 + MOD(x, 15),
       'R',
       '2016-01-01',
       '132',
       'Thriller,Drama',
       'Bong Joon-ho',
       'Bong Joon-ho',
       'Actor M, Actor N'
FROM SYSTEM_RANGE(1, 50);

-- Taika Waititi (40)
INSERT INTO movies (title, release_year, rated, released, runtime, genre, director, writer, actors)
SELECT 'Taika Movie ' || x,
       2010 + MOD(x, 15),
       'PG-13',
       '2017-01-01',
       '110',
       'Comedy,Adventure',
       'Taika Waititi',
       'Taika Waititi',
       'Actor O, Actor P'
FROM SYSTEM_RANGE(1, 40);

-- Patty Jenkins (35)
INSERT INTO movies (title, release_year, rated, released, runtime, genre, director, writer, actors)
SELECT 'Jenkins Movie ' || x,
       2010 + MOD(x, 15),
       'PG-13',
       '2018-01-01',
       '125',
       'Action,Adventure',
       'Patty Jenkins',
       'Various',
       'Actor Q, Actor R'
FROM SYSTEM_RANGE(1, 35);

-- Damien Chazelle (30)
INSERT INTO movies (title, release_year, rated, released, runtime, genre, director, writer, actors)
SELECT 'Chazelle Movie ' || x,
       2010 + MOD(x, 15),
       'PG-13',
       '2019-01-01',
       '128',
       'Drama,Music',
       'Damien Chazelle',
       'Damien Chazelle',
       'Actor S, Actor T'
FROM SYSTEM_RANGE(1, 30);

-- Ajusta/duplica bloques para más volumen si quieres (miles de filas sin problema en memoria).
