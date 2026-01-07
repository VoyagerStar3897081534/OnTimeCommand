#!/bin/bash
echo "Starting Minecraft Server..."
echo "Make sure you have installed Java 21 or higher."
echo

# Check if server jar exists
if [ ! -f "paper-1.21.4.jar" ]; then
    echo "Paper 1.21.4 server jar not found."
    echo "Please download it manually from:"
    echo "https://papermc.io/downloads/paper/1.21.4"
    echo "Download the latest build and rename it to 'paper-1.21.4.jar'"
    exit 1
fi

# Start Minecraft server
echo "Starting Paper server..."
java -Xms2G -Xmx2G -jar paper-1.21.4.jar