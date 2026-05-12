# ========================================================================================================================
# Activity: RPC Activity - Movie Management | Distributed Systems | Universidade Tecnológica Federal do Paraná
# Authors: Gabriel Craco Tasarz, Leonardo Jun'Ity Ozima
# Professor: Prof. Rodrigo Campiolo
# Release Date: May 10, 2026
# Last Change At: May 12, 2026
# ========================================================================================================================

import os
from dotenv import load_dotenv
from pymongo import MongoClient
from urllib.parse import quote_plus

"""
Establishes a connection to the MongoDB Atlas cluster and retrieves the movies collection.
    
Returns:
    Collection: A PyMongo Collection object representing the 'movies' collection.
Raises:
    ValueError: If the DB_PASSWORD environment variable is not found.
"""
def get_movies_collection():
    print("connecting to MongoDB...")
    load_dotenv()
    
    password = os.getenv("DB_PASSWORD")
    if not password:
        raise ValueError("DB_PASSWORD não encontrado no arquivo .env")
        
    password_encoded = quote_plus(password)
    
    uri = f"mongodb+srv://sd-red-mongodb:{password_encoded}@cluster0.nyepcaz.mongodb.net/?appName=Cluster0"
    
    client = MongoClient(uri)
    db = client["sample_mflix"]
    
    return db["movies"]