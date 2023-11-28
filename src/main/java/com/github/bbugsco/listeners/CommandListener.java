package com.github.bbugsco.listeners;

import com.github.bbugsco.Bot;

import com.github.bbugsco.command.LeaderboardCommand;
import com.github.bbugsco.command.XpCommand;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;

import org.jetbrains.annotations.NotNull;

public class CommandListener implements EventListener {

	private final Bot bot;

	public CommandListener(Bot bot) {
		this.bot = bot;
	}

	@Override
	public void onEvent(@NotNull GenericEvent genericEvent) {
		if (genericEvent instanceof MessageReceivedEvent) {
			Message message = ((MessageReceivedEvent) genericEvent).getMessage();
			String content = message.getContentRaw();
			if (content.startsWith("!xp")) {
				new XpCommand().handle(bot, message);
			} else if (content.startsWith("!leaderboard")) {
				new LeaderboardCommand().handle(bot, message);
			}
		}
	}
}
