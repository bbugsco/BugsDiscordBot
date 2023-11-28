package com.github.bbugsco;

import com.github.bbugsco.leaderboard.Leaderboard;
import com.github.bbugsco.listeners.CommandListener;
import com.github.bbugsco.listeners.MemberJoin;
import com.github.bbugsco.listeners.ReadyListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class Bot {

	private final Leaderboard leaderboard;

	public Bot(String token) {
		this.leaderboard = new Leaderboard();


		// Create bot
		JDABuilder builder = JDABuilder.createDefault(token);

		builder.setEventPassthrough(true);
		builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
		builder.setBulkDeleteSplittingEnabled(false);
		builder.setActivity(Activity.of(Activity.ActivityType.WATCHING, "you"));


		// Add intents
		builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
		builder.enableIntents(GatewayIntent.GUILD_MESSAGE_REACTIONS);
		builder.enableIntents(GatewayIntent.GUILD_MESSAGES);

		// Add listeners
		builder.addEventListeners(new ReadyListener());
		builder.addEventListeners(new MemberJoin());
		builder.addEventListeners(this.leaderboard);
		builder.addEventListeners(new CommandListener(this));

		builder.build();
	}

	public Leaderboard getLeaderboard() {
		return this.leaderboard;
	}

}
