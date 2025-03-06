@echo off

set DB_NAME=library
set DB_USER=postgres

psql -U %DB_USER% -c "CREATE DATABASE %DB_NAME%;"


