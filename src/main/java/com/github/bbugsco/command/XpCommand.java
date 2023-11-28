package com.github.bbugsco.command;

import com.github.bbugsco.Bot;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

public class XpCommand implements BotCommand {

	@Override
	public void handle(Bot bot, Message message) {

		String[] arguments = message.getContentRaw().split(" ");
		if (arguments.length == 2) {
			if (message.getMentions().getMembers().size() != 1) {
				message.getChannel().sendMessage("Target player not found").queue();
				return;
			}
			Member member = message.getMentions().getMembers().get(0);
			message.getChannel().sendMessage("> " + member.getEffectiveName() + " has " + bot.getLeaderboard().getXP(member) + " xp").queue();
		} else if (arguments.length == 1) {
			Member target = message.getMember();
			if (target == null) {
				message.getChannel().sendMessage("Target player not found").queue();
				return;
			}
			message.getChannel().sendMessage("> " + target.getEffectiveName() + " has " + bot.getLeaderboard().getXP(target) + " xp").queue();
		}
	}

}
