# ========================================================================================================================
# Activity: RPC Activity - Movie Management | Distributed Systems | Universidade Tecnológica Federal do Paraná
# Authors: Gabriel Craco Tasarz, Leonardo Jun'Ity Ozima
# Professor: Prof. Rodrigo Campiolo
# Release Date: May 10, 2026
# Last Change At: May 12, 2026
# ========================================================================================================================

import re
from bson import ObjectId

import movie_pb2
import movie_pb2_grpc
from helpers import validate_movie, movie_to_doc, doc_to_movie

class MovieServiceServicer(movie_pb2_grpc.MovieServiceServicer):
    """
    Initializes the MovieService gRPC servicer.

    Args:
        movies_collection (Collection): The PyMongo collection object for the movies database.
    """
    def __init__(self, movies_collection):
        self.movies = movies_collection

    """
    Handles the gRPC request to create a new movie in the database.

    Args:
        request (movie_pb2.Movie): The movie data sent by the client.
        context (grpc.ServicerContext): The RPC context.
    Returns:
        movie_pb2.MovieResponse: A response containing success status and the created movie or an error.
    """
    def CreateMovie(self, request, context):
        print("handling CREATE operation")
        try:
            err = validate_movie(request)
            if err:
                return movie_pb2.MovieResponse(success=False, error=err)

            doc = movie_to_doc(request)
            result = self.movies.insert_one(doc)
            created = self.movies.find_one({"_id": result.inserted_id})

            return movie_pb2.MovieResponse(success=True, movie=doc_to_movie(created))
        except Exception as e:
            return movie_pb2.MovieResponse(success=False, error=f"CREATE error: {e}")

    """
    Handles the gRPC request to retrieve a single movie by its MongoDB ID.

    Args:
        request (movie_pb2.MovieIdRequest): The request containing the movie ID.
        context (grpc.ServicerContext): The RPC context.
    Returns:
        movie_pb2.MovieResponse: A response containing success status and the retrieved movie or an error.
    """
    def GetMovie(self, request, context):
        print("handling GET operation")
        try:
            oid = ObjectId(request.id)
            doc = self.movies.find_one({"_id": oid})

            if doc is None:
                return movie_pb2.MovieResponse(success=False, error="movie not found")

            return movie_pb2.MovieResponse(success=True, movie=doc_to_movie(doc))
        except Exception as e:
            return movie_pb2.MovieResponse(success=False, error=f"GET error: {e}")

    """
    Handles the gRPC request to update an existing movie in the database.

    Args:
        request (movie_pb2.Movie): The updated movie data, which must include a valid ID.
        context (grpc.ServicerContext): The RPC context.
    Returns:
        movie_pb2.MovieResponse: A response containing success status and the updated movie or an error.
    """
    def UpdateMovie(self, request, context):
        print("handling UPDATE operation")
        try:
            if not request.id:
                return movie_pb2.MovieResponse(success=False, error="field 'id' is required for update")
            
            err = validate_movie(request)
            if err:
                return movie_pb2.MovieResponse(success=False, error=err)

            oid = ObjectId(request.id)
            update_fields = movie_to_doc(request)
            result = self.movies.update_one({"_id": oid}, {"$set": update_fields})

            if result.matched_count == 0:
                return movie_pb2.MovieResponse(success=False, error="movie not found")

            updated = self.movies.find_one({"_id": oid})
            return movie_pb2.MovieResponse(success=True, movie=doc_to_movie(updated))
        except Exception as e:
            return movie_pb2.MovieResponse(success=False, error=f"UPDATE error: {e}")
        
    """
    Handles the gRPC request to delete a movie by its MongoDB ID.

    Args:
        request (movie_pb2.MovieIdRequest): The request containing the movie ID to be deleted.
        context (grpc.ServicerContext): The RPC context.
    Returns:
        movie_pb2.DeleteResponse: A response containing success status and a success message or an error.
    """
    def DeleteMovie(self, request, context):
        print("handling DELETE operation")
        try:
            oid = ObjectId(request.id)
            result = self.movies.delete_one({"_id": oid})

            if result.deleted_count is None or result.deleted_count == 0:
                return movie_pb2.DeleteResponse(success=False, error="movie not found")

            return movie_pb2.DeleteResponse(success=True, message="movie deleted successfully")
        except Exception as e:
            return movie_pb2.DeleteResponse(success=False, error=f"DELETE error: {e}")
        
    """
    Handles the gRPC request to list movies that feature a specific actor.

    Args:
        request (movie_pb2.ActorRequest): The request containing the actor's name.
        context (grpc.ServicerContext): The RPC context.
    Returns:
        movie_pb2.MovieListResponse: A response containing success status and a list of matched movies.
    """
    def ListByActor(self, request, context):
        print(f"handling LIST_BY_ACTOR operation for: {request.actor}")
        try:
            actor_query = re.compile(request.actor, re.IGNORECASE)
            response = self.movies.find({"cast": actor_query}).limit(20)
            movie_list = [doc_to_movie(doc) for doc in response]

            if not movie_list:
                return movie_pb2.MovieListResponse(success=False, error="no movies found for this actor")

            return movie_pb2.MovieListResponse(success=True, movies=movie_list)
        except Exception as e:
            return movie_pb2.MovieListResponse(success=False, error=f"LIST_BY_ACTOR error: {e}")

    """
    Handles the gRPC request to list movies that belong to a specific genre.

    Args:
        request (movie_pb2.GenreRequest): The request containing the genre name.
        context (grpc.ServicerContext): The RPC context.
    Returns:
        movie_pb2.MovieListResponse: A response containing success status and a list of matched movies.
    """
    def ListByGenre(self, request, context):
        print(f"handling LIST_BY_GENRE operation for: {request.genre}")
        try:
            genre_query = re.compile(request.genre, re.IGNORECASE)
            response = self.movies.find({"genres": genre_query}).limit(20)
            movie_list = [doc_to_movie(doc) for doc in response]

            if not movie_list:
                return movie_pb2.MovieListResponse(success=False, error="no movies found for this genre")

            return movie_pb2.MovieListResponse(success=True, movies=movie_list)
        except Exception as e:
            return movie_pb2.MovieListResponse(success=False, error=f"LIST_BY_GENRE error: {e}")