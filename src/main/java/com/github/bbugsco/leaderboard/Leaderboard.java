package com.github.bbugsco.leaderboard;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class Leaderboard implements EventListener {

	private final LeaderboardData leaderboardData;
	private final String FILENAME = "leaderboard.ser";

	private final HashMap<Member, Long> lastMessage;

	public Leaderboard() {
		this.lastMessage = new HashMap<>();

		if (this.saveFileExists()) {
			this.leaderboardData = deserialize();
		} else {
			this.leaderboardData = new LeaderboardData();
		}
	}

	public LeaderboardData getLeaderboardData() {
		return this.leaderboardData;
	}

	@Override
	public void onEvent(@NotNull GenericEvent genericEvent) {
		if (genericEvent instanceof MessageReceivedEvent) {

			Member member = ((MessageReceivedEvent) genericEvent).getMember();
			if (member == null) return;
			if (member.getEffectiveName().equalsIgnoreCase("Bugs Server Bot")) return;

			// Calculate message xp
			Message message = ((MessageReceivedEvent) genericEvent).getMessage();
			String messageContent = message.getContentRaw();
			// Ignore bot commands
			if (messageContent.contains("!level") || messageContent.contains("!leaderboard") || messageContent.contains("!xp")) return;
			double length = messageContent.length();
			int xp = (int) (Math.floor(Math.log(length) / Math.log(1.1D)) + 1D);

			if (leaderboardData.getData().containsKey(member.getId())) {
				// Check cool down
				if (lastMessage.containsKey(member)) {
					if (System.currentTimeMillis() - 5000 < lastMessage.get(member)) {
						// Cool down
						return;
					}
				}

				// Update leaderboard
				leaderboardData.getData().put(member.getId(), leaderboardData.getData().get(member.getId()) + xp);
			} else {
				leaderboardData.getData().put(member.getId(), xp);
			}

			this.lastMessage.put(member,  System.currentTimeMillis());
			this.serialize();
		}
	}


	/*
	Serialization Section
	 */

	private LeaderboardData deserialize() {

		LeaderboardData deserializedObject = null;
		try (ObjectInputStream objectInputStream = new ObjectInputStream(Files.newInputStream(Paths.get(FILENAME)))) {
			deserializedObject = (LeaderboardData) objectInputStream.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.fillInStackTrace();
			System.out.println("Error deserializing file");
		}
		return deserializedObject != null ? deserializedObject : new LeaderboardData();

	}


	private void serialize() {

		try (FileOutputStream fileOutputStream = new FileOutputStream(FILENAME);
		     ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
			objectOutputStream.writeObject(this.leaderboardData);
		} catch (IOException e) {
			e.fillInStackTrace();
			System.out.println("Error serializing file");
		}

	}

	private boolean saveFileExists() {
		File file = new File(FILENAME);
		return file.exists();
	}

}
