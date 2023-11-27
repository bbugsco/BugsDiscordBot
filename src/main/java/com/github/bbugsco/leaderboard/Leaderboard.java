package com.github.bbugsco.leaderboard;

import net.dv8tion.jda.api.entities.Member;
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

	public Leaderboard(HashMap<Member, Long> lastMessage) {
		this.lastMessage = lastMessage;

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

			if (leaderboardData.getData().containsKey(member)) {
				leaderboardData.getData().replace(member, leaderboardData.getData().get(member) + 1);
			} else {
				leaderboardData.getData().put(member, 1);
			}

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
