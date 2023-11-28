package com.github.bbugsco.listeners;

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


	@Override
	public void onEvent(@NotNull GenericEvent event) {
		if (event instanceof GuildMemberJoinEvent) {

			System.out.println("Member joined: " + ((GuildMemberJoinEvent) event).getMember().getEffectiveName() + " in guild " + ((GuildMemberJoinEvent) event).getGuild().getName() + ".");

			// Add member role
			Guild guild = ((GuildMemberJoinEvent) event).getGuild();
			if (getMemberRole(guild) == null) {
				createMemberRole(guild);
			}
			while (getMemberRole(guild) == null) {
				try {
					TimeUnit.MILLISECONDS.sleep(50);
				} catch (InterruptedException e) {
					e.fillInStackTrace();
					System.out.println(e.getMessage());
				}
			}
			addRoleToMember(((GuildMemberJoinEvent) event).getMember(), getMemberRole(guild));

		}
	}


	private void createMemberRole(Guild guild) {
		guild.createRole()
				.setName("Member")
				.setColor(0x2e4d80)
				.setHoisted(true)
				.queue(role -> System.out.println("Role created: " + role.getName() + " in guild " + guild.getName()));
	}


	private Role getMemberRole(Guild guild) {
		for (Role role : guild.getRoles()) {
			if (role.getName().equalsIgnoreCase("member")) {
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
