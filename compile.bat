@echo off
echo Creating bin directory...
if not exist bin mkdir bin

echo Checking for MySQL JDBC driver...
if not exist lib\mysql-connector-j-8.0.33.jar (
    echo MySQL JDBC driver not found!
    echo Please download mysql-connector-j-8.0.33.jar and place it in the lib folder.
    echo Download from: https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-j-8.0.33.zip
    pause
    exit /b 1
)

echo Compiling Java files...
javac -d bin -cp ".;bin;lib\mysql-connector-j-8.0.33.jar" src/main/java/com/library/MainApplication.java src/main/java/com/library/models/*.java src/main/java/com/library/utils/*.java src/main/java/com/library/views/*.java src/main/java/com/library/views/panels/*.java

if errorlevel 1 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo Compilation successful!
echo Running application...
java -cp ".;bin;lib\mysql-connector-j-8.0.33.jar" com.library.MainApplication
pause 