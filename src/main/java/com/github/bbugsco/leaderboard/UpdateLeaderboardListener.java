package com.github.bbugsco.leaderboard;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class UpdateLeaderboardListener implements EventListener {

	private final Leaderboard leaderboard;
	private final HashMap<Member, Long> lastMessage;


	public UpdateLeaderboardListener(Leaderboard leaderboard) {
		this.leaderboard = leaderboard;
		this.lastMessage = new HashMap<>();
	}

	@Override
	public void onEvent(@NotNull GenericEvent event) {
		if (event instanceof MessageReceivedEvent) {

			Member member = ((MessageReceivedEvent) event).getMember();
			if (member == null) return;
			if (member.getEffectiveName().equalsIgnoreCase("Bugs Server Bot")) return;
			// Calculate message xp
			Message message = ((MessageReceivedEvent) event).getMessage();
			String messageContent = message.getContentRaw();
			// Ignore bot commands
			if (messageContent.contains("!level") || messageContent.contains("!leaderboard") || messageContent.contains("!xp"))
				return;
			double length = messageContent.length();
			int xp = (int) (Math.floor(Math.log(length) / Math.log(1.1D)) + 1D);

			// Check cool down
			if (lastMessage.containsKey(member) || length < 1) {
				if (System.currentTimeMillis() - 5000 < lastMessage.get(member)) {
					// Cool down
					return;
				}
			}

			// Update leaderboard
			leaderboard.addXP(member, xp);
			this.lastMessage.put(member, System.currentTimeMillis());
			leaderboard.serialize();

		}
	}
}