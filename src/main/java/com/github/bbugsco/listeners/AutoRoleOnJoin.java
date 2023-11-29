package com.github.bbugsco.listeners;

import com.github.bbugsco.Bot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class AutoRoleOnJoin implements EventListener {

	private final Bot bot;

	public AutoRoleOnJoin(Bot bot) {
		this.bot = bot;
	}

	@Override
	public void onEvent(@NotNull GenericEvent event) {
		if (event instanceof GuildMemberJoinEvent) {
			if (!bot.getProperties().contains("auto_role")) bot.getProperties().put("auto_role", "true");
			if (!Boolean.parseBoolean(bot.getProperties().getProperty("auto_role"))) return;

			// Add member role
			String autoRoleName = bot.getProperties().getProperty("role_name");
			if (autoRoleName == null) return;

			// Check if member role exists, if not, create it.
			if (getAutoRole(((GuildMemberJoinEvent) event).getGuild()) == null) {
				createAutoRole(((GuildMemberJoinEvent) event).getGuild());
			}

			Guild guild = ((GuildMemberJoinEvent) event).getGuild();
			if (getAutoRole(guild)  == null) {
				createAutoRole(guild);
			}

			while (getAutoRole(guild) == null) {
				try {
					TimeUnit.MILLISECONDS.sleep(50);
				} catch (InterruptedException e) {
					e.fillInStackTrace();
					System.out.println(e.getMessage());
				}
			}

			addRoleToMember(((GuildMemberJoinEvent) event).getMember(), getAutoRole(guild));

		}
	}

	private void createAutoRole(Guild guild) {
		guild.createRole()
				.setName(bot.getProperties().getProperty("role_name"))
				.setColor(Integer.parseInt(bot.getProperties().getProperty("role_color").substring(2), 16))
				.setHoisted(true)
				.queue(role -> System.out.println("Role created: " + role.getName() + " in guild " + guild.getName()));
	}

	private Role getAutoRole(Guild guild) {
		for (Role role : guild.getRoles()) {
			if (role.getName().equalsIgnoreCase(bot.getProperties().getProperty("role_name"))) {
				return role;
			}
		}
		return null;
	}

	private void addRoleToMember(Member member, Role role) {
		try {
			member.getGuild().addRoleToMember(member, role).queue(success -> System.out.println("Role added to " + member.getEffectiveName() + ": " + role.getName()), failure -> System.out.println("Failed to add role to " + member.getEffectiveName() + ": " + failure.getMessage()));
		} catch(HierarchyException e) {
			e.fillInStackTrace();
			System.out.println(e.getMessage());
		}
	}

}
