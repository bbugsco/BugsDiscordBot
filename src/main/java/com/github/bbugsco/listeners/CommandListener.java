package com.github.bbugsco.listeners;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;


public class CommandListener implements EventListener {

	public CommandListener() {

	}

	@Override
	public void onEvent(@NotNull GenericEvent genericEvent) {
		if (genericEvent instanceof MessageReceivedEvent) {

			}
		}
}
