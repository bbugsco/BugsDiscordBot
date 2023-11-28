package com.github.bbugsco.listeners;

import com.github.bbugsco.Bot;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.EventListener;

import org.jetbrains.annotations.NotNull;

public class MemberJoin implements EventListener {

	private final Bot bot;

	public MemberJoin(Bot bot) {
		this.bot = bot;
	}

	@Override
	public void onEvent(@NotNull GenericEvent event) {
		if (event instanceof GuildMemberJoinEvent) {

			// Check if default channel exists
			if (((GuildMemberJoinEvent) event).getGuild().getDefaultChannel() == null) {
				return;
			}

			// Check if property is missing
			if (!bot.getProperties().contains("welcome_message")) {
				bot.getProperties().put("welcome_message", "Welcome {user_mention} to {guild_name}!");
			}

			Member joiningMember =  ((GuildMemberJoinEvent) event).getMember();
			String welcomeMessage = bot.getProperties().getProperty("welcome_message");

			// Check for replace characters - {user_mention} and {guild_name}
			if (welcomeMessage.contains("{user_mention}")) {
				welcomeMessage = welcomeMessage.replace("{user_mention}", joiningMember.getAsMention());
			}

			if (welcomeMessage.contains("{guild_name}")) {
				welcomeMessage = welcomeMessage.replace("{guild_name}", joiningMember.getGuild().getName());
			}

			((GuildMemberJoinEvent) event).getGuild().getDefaultChannel().asTextChannel().sendMessage(welcomeMessage).queue();

		}
	}
}
