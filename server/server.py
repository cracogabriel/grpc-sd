# ========================================================================================================================
# Activity: RPC Activity - Movie Management | Distributed Systems | Universidade Tecnológica Federal do Paraná
# Authors: Gabriel Craco Tasarz, Leonardo Jun'Ity Ozima
# Professor: Prof. Rodrigo Campiolo
# Release Date: May 10, 2026
# Last Change At: May 12, 2026
# ========================================================================================================================

import grpc
from concurrent import futures

import movie_pb2_grpc
from database import get_movies_collection
from service import MovieServiceServicer

"""
Initializes and starts the gRPC server. It connects to the database, 
binds the MovieServiceServicer to the server instance, and listens 
for incoming RPC calls on port 5000.

Args:
    None
Returns:
    None
"""
def serve():
    print('starting gRPC server initialization...')
    
    movies_collection = get_movies_collection()
    print(f"connected with MongoDB! total movies: {movies_collection.count_documents({})}")

    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    
    service = MovieServiceServicer(movies_collection)
    movie_pb2_grpc.add_MovieServiceServicer_to_server(service, server)
    
    server.add_insecure_port('[::]:5000')
    server.start()
    
    print("\ngRPC server is listening on [::]:5000\n")
    server.wait_for_termination()

if __name__ == "__main__":
    try:
        serve()
    except KeyboardInterrupt:
        print('\nserver shutting down manually.')