#!/bin/bash

echo "Creating bin directory..."
mkdir -p bin

echo "Checking for MySQL JDBC driver..."
if [ ! -f "lib/mysql-connector-j-8.0.33.jar" ]; then
    echo "MySQL JDBC driver not found!"
    echo "Please download mysql-connector-j-8.0.33.jar and place it in the lib folder."
    echo "Download from: https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-j-8.0.33.zip"
    read -p "Press Enter to exit..."
    exit 1
fi

echo "Compiling Java files..."
javac -d bin -cp ".:bin:lib/mysql-connector-j-8.0.33.jar" src/main/java/com/library/MainApplication.java src/main/java/com/library/models/*.java src/main/java/com/library/utils/*.java src/main/java/com/library/views/*.java src/main/java/com/library/views/panels/*.java

if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    read -p "Press Enter to continue..."
    exit 1
fi

echo "Compilation successful!"
echo "Running application..."
java -cp ".:bin:lib/mysql-connector-j-8.0.33.jar:src/main/resources" com.library.MainApplication

read -p "Press Enter to continue..."