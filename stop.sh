current_dir=$(pwd)
file_path="$current_dir/target/BugsDiscordBot-0.1.jar"
pids=$(pgrep -f "java -jar $file_path")

echo "PIDs: $pids"

if [ -n "$pids" ]; then
    for pid in $pids; do
        echo "Killing process $pid"
        kill "$pid"
    done
else
    echo "No matching process found"
fi