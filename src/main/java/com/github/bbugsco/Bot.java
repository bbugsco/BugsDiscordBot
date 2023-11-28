package com.github.bbugsco;

import com.github.bbugsco.leaderboard.Leaderboard;
import com.github.bbugsco.leaderboard.UpdateLeaderboardListener;
import com.github.bbugsco.listeners.AutoRoleOnJoin;
import com.github.bbugsco.listeners.CommandListener;
import com.github.bbugsco.listeners.MemberJoin;
import com.github.bbugsco.listeners.ReadyListener;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Bot {

	private final Leaderboard leaderboard;
	private final Properties properties;

	public Bot(String token) {
		// Initialize leaderboard
		this.leaderboard = new Leaderboard();

		// Load properties from file
		this.properties = new Properties();
		try {
			String FILENAME = "bugs_discord_bot.properties";
			FileInputStream inputStream = new FileInputStream(FILENAME);
			properties.load(inputStream);
		} catch (IOException e) {
			e.fillInStackTrace();
			System.out.println(e.getMessage());
		}

		// Create bot
		JDABuilder builder = JDABuilder.createDefault(token);

		// Settings
		builder.setEventPassthrough(true);
		builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
		builder.setBulkDeleteSplittingEnabled(false);
		builder.setActivity(Activity.of(Activity.ActivityType.WATCHING, "you"));

		// Add intents
		builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
		builder.enableIntents(GatewayIntent.GUILD_MESSAGE_REACTIONS);
		builder.enableIntents(GatewayIntent.GUILD_MESSAGES);
		builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);

		// Add listeners
		builder.addEventListeners(new ReadyListener());
		builder.addEventListeners(new MemberJoin());
		builder.addEventListeners(new AutoRoleOnJoin());
		builder.addEventListeners(new UpdateLeaderboardListener(this.leaderboard));
		builder.addEventListeners(new CommandListener(this));

		builder.build();
	}

	public Leaderboard getLeaderboard() {
		return this.leaderboard;
	}

	public Properties getProperties() {
		return this.properties;
	}

}
