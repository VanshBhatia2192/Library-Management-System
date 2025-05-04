!/bin/bash

url="https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-j-8.0.33.zip"
output="mysql-connector.zip"
extractPath="temp"

# Create temp and lib directories
mkdir -p $extractPath
mkdir -p lib

# Download the file
echo "Downloading MySQL JDBC driver..."
curl -L $url -o $output

# Extract the zip file
echo "Extracting files..."
unzip -q $output -d $extractPath

# Copy the jar file to lib directory
echo "Copying jar file to lib directory..."
cp "$extractPath/mysql-connector-j-8.0.33/mysql-connector-j-8.0.33.jar" "lib/mysql-connector-j-8.0.33.jar"

# Clean up
echo "Cleaning up..."
rm $output
rm -rf $extractPath

echo "Setup complete!"