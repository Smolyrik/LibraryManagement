@echo off

set DB_NAME=library
set DB_USER=postgres

psql -U %DB_USER% -f "delete_db.sql"

echo Database successfuly deleted.