$url = "https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-j-8.0.33.zip"
$output = "mysql-connector.zip"
$extractPath = "temp"

# Create temp directory
New-Item -ItemType Directory -Force -Path $extractPath

# Download the file
Write-Host "Downloading MySQL JDBC driver..."
Invoke-WebRequest -Uri $url -OutFile $output

# Extract the zip file
Write-Host "Extracting files..."
Expand-Archive -Path $output -DestinationPath $extractPath -Force

# Copy the jar file to lib directory
Write-Host "Copying jar file to lib directory..."
Copy-Item "$extractPath\mysql-connector-j-8.0.33\mysql-connector-j-8.0.33.jar" -Destination "lib\mysql-connector-j-8.0.33.jar" -Force

# Clean up
Write-Host "Cleaning up..."
Remove-Item -Path $output -Force
Remove-Item -Path $extractPath -Recurse -Force

Write-Host "Setup complete!" 