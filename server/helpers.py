# ========================================================================================================================
# Activity: RPC Activity - Movie Management | Distributed Systems | Universidade Tecnológica Federal do Paraná
# Authors: Gabriel Craco Tasarz, Leonardo Jun'Ity Ozima
# Professor: Prof. Rodrigo Campiolo
# Release Date: May 10, 2026
# Last Change At: May 12, 2026
# ========================================================================================================================

import movie_pb2

"""
Validates the mandatory fields of a Movie protobuf object.

Args:
    movie (movie_pb2.Movie): The protobuf movie object to be validated.
Returns:
    str | None: An error message string if a field is missing, None otherwise.
"""
def validate_movie(movie: movie_pb2.Movie) -> str | None:
    if not movie.title.strip():
        return "field 'title' is required"
    if not movie.year:
        return "field 'year' is required"
    return None

"""
Safely parses a value into an integer year, handling strings with mixed characters.

Args:
    val (any): The value to be parsed (can be string, int, etc.).
Returns:
    int: The parsed integer year, or 0 if parsing fails.
"""
def safe_parse_year(val):
    try:
        return int(val)
    except (ValueError, TypeError):
        cleaned = ''.join(filter(str.isdigit, str(val)))
        return int(cleaned) if cleaned else 0

"""
Converts a MongoDB document dictionary into a Protobuf Movie object.

Args:
    doc (dict): The dictionary retrieved from MongoDB.
Returns:
    movie_pb2.Movie | None: The mapped Protobuf Movie object, or None if the input is None.
"""
def doc_to_movie(doc) -> movie_pb2.Movie | None:
    if doc is None:
        return None
    return movie_pb2.Movie(
        id              = str(doc.get("_id", "")),
        title           = doc.get("title", ""),
        plot            = doc.get("plot", ""),
        fullplot        = doc.get("fullplot", ""),
        genres          = doc.get("genres", []),
        cast            = doc.get("cast", []),
        directors       = doc.get("directors", []),
        countries       = doc.get("countries", []),
        languages       = doc.get("languages", []),
        released        = str(doc.get("released", "")),
        runtime         = doc.get("runtime", 0),
        rated           = doc.get("rated", ""),
        year            = safe_parse_year(doc.get("year", 0)),
        type            = doc.get("type", ""),
        poster          = doc.get("poster", ""),
        num_mflix_comments = doc.get("num_mflix_comments", 0),
        lastupdated     = doc.get("lastupdated", ""),
    )

"""
Converts a Protobuf Movie object into a Python dictionary suitable for MongoDB insertion.

Args:
    movie (movie_pb2.Movie): The Protobuf movie object.
Returns:
    dict: A Python dictionary mapping the object fields.
"""
def movie_to_doc(movie: movie_pb2.Movie) -> dict:
    return {
        "title":             movie.title,
        "plot":              movie.plot,
        "fullplot":          movie.fullplot,
        "genres":            list(movie.genres),
        "cast":              list(movie.cast),
        "directors":         list(movie.directors),
        "countries":         list(movie.countries),
        "languages":         list(movie.languages),
        "released":          movie.released,
        "runtime":           movie.runtime,
        "rated":             movie.rated,
        "year":              movie.year,
        "type":              movie.type,
        "poster":            movie.poster,
        "num_mflix_comments": movie.num_mflix_comments,
        "lastupdated":       movie.lastupdated,
    }