package com.github.bbugsco.listeners;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.EventListener;

import org.jetbrains.annotations.NotNull;

public class MemberJoin implements EventListener {

	@Override
	public void onEvent(@NotNull GenericEvent event) {
		if (event instanceof GuildMemberJoinEvent) {

			// Welcome message
			if (((GuildMemberJoinEvent) event).getGuild().getDefaultChannel() == null) {
				return;
			}

			((GuildMemberJoinEvent) event).getGuild().getDefaultChannel().asTextChannel().sendMessage(">    **Welcome " + ((GuildMemberJoinEvent) event).getMember().getAsMention() + " to Bugs SMP!**").queue();

		}
	}
}
