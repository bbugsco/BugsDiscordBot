package com.github.bbugsco.command;

import com.github.bbugsco.Bot;

import net.dv8tion.jda.api.entities.Message;

public interface BotCommand {

	/**
	 * @param bot instance of bot class
	 * @param message message that was sent
	 */
	void handle(Bot bot, Message message);

}
