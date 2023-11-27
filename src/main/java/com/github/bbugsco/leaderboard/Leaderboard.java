package com.github.bbugsco.leaderboard;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;

import org.jetbrains.annotations.NotNull;

import java.io.*;
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

			// Calculate message xp
			Message message = ((MessageReceivedEvent) genericEvent).getMessage();
			int length = message.getContentRaw().length();
			double xp = (Math.log(length) / Math.log(1.1)) + 1;

			if (leaderboardData.getData().containsKey(member)) {
				// Check cool down
				if (lastMessage.containsKey(member)) {
					if (System.currentTimeMillis() - 5000 < lastMessage.get(member)) {
						// Cool down
						return;
					}
				}

				// Update leaderboard
				leaderboardData.getData().put(member, leaderboardData.getData().get(member) + xp);
			} else {
				leaderboardData.getData().put(member, xp);
			}

			System.out.println(member.getEffectiveName() + " said something");
			this.lastMessage.put(member,  System.currentTimeMillis());
			this.serialize();
		}
	}


	/*
	Serialization Section
	 */

	private LeaderboardData deserialize() {
		LeaderboardData deserializedObject = null;
		try (FileInputStream fileInputStream = new FileInputStream("serializedObject.ser");
		     ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
			 deserializedObject = (LeaderboardData) objectInputStream.readObject();
			 System.out.println("Object deserialized. Data: " + deserializedObject.getData());
		} catch (IOException | ClassNotFoundException e) {
			e.fillInStackTrace();
			System.out.println(e.getMessage());
		}
		return deserializedObject;
	}


	private void serialize() {

		try (FileOutputStream fileOutputStream = new FileOutputStream(FILENAME);
		     ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
			objectOutputStream.writeObject(this.leaderboardData);
			System.out.println("Object serialized and stored in 'serializedObject.ser'");
		} catch (IOException e) {
			e.fillInStackTrace();
			System.out.println(e.getMessage());
		}

	}


	private boolean saveFileExists() {
		File file = new File(FILENAME);
		return file.exists();
	}

}
