current_dir=$(pwd)
file_path="$current_dir/target/BugsDiscordBot-0.1.jar"
nohup java -jar "$file_path" > output.log 2>&1 &
