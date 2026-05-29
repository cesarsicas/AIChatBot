cmake_minimum_required(VERSION 3.22)
project(vectorsearch)

add_library(vectorsearch SHARED
    sqlite3.c
    sqlite-vec.c
    vector_search.cpp)

target_compile_definitions(vectorsearch PRIVATE
    SQLITE_CORE
    SQLITE_THREADSAFE=1
    SQLITE_DEFAULT_MEMSTATUS=0)

target_link_libraries(vectorsearch android log)
