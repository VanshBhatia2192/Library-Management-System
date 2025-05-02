@echo off
echo Initializing MySQL database...

REM Read database properties
for /f "tokens=1,2 delims==" %%a in (src/main/resources/database.properties) do (
    if "%%a"=="db.username" set DB_USER=%%b
    if "%%a"=="db.password" set DB_PASS=%%b
)

REM Create database and tables
mysql -u%DB_USER% -p%DB_PASS% < src/main/resources/schema.sql

if errorlevel 1 (
    echo Database initialization failed!
    echo Please make sure MySQL is installed and running.
    echo Also verify your database credentials in src/main/resources/database.properties
    pause
    exit /b 1
)

echo Database initialized successfully!
pause 