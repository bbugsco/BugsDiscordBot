package com.github.bbugsco;

import com.github.bbugsco.listeners.MemberJoin;
import com.github.bbugsco.listeners.ReadyListener;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;


public class Main {
    public static void main( String[] args ) {

		String token = System.getenv("API_KEY");

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

	    builder.build();

    }
}
