@echo off

set DB_NAME=library
set DB_USER=postgres

psql -U %DB_USER% -c "CREATE DATABASE %DB_NAME%;"

if %errorlevel% neq 0 (
    echo Failed to create database.
    exit /b %errorlevel%
)

psql -U %DB_USER% -d %DB_NAME% -f "create_db_tables.sql"

echo Database and tables setup completed.
