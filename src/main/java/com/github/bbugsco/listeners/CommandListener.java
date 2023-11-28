package com.github.bbugsco.listeners;

import com.github.bbugsco.Bot;
import net.dv8tion.jda.api.entities.Member;
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
				message.getChannel().sendMessage(getResponse(Command.XP, message, content)).queue();
			}

		}
	}

	public String getResponse(Command command, Message message, String content) {
		switch (command) {
			case XP:
				String[] arguments = content.split(" ");
				if (arguments.length == 2) {
					Member target = message.getGuild().getMemberById(arguments[1]);
					if (target == null) return "Target player not found: " + arguments[1];
					return "- " + target.getEffectiveName() + "\n > " + bot.getLeaderboard().getLeaderboardData().getRawXP(target) + " xp";
				} else if (arguments.length == 1) {
					Member target = message.getMember();
					if (target == null) return "Target player not found: " + arguments[1];
					return "- " + target.getEffectiveName() + "\n > " + bot.getLeaderboard().getLeaderboardData().getRawXP(target) + " xp";
				}
			case LEADERBOARD:

			case LEVEL:

			default:
				return null;
		}
	}

}

