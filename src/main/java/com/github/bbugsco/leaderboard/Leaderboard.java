package com.github.bbugsco.leaderboard;

import net.dv8tion.jda.api.entities.Member;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Leaderboard {

	private final LeaderboardObject leaderboardObject;
	// private final String FILENAME = "/home/*****/discord/leaderboard.ser";
	private final String FILENAME = "/home/bbugsco/discord/leaderboard.ser";

	public Leaderboard() {
		// Load leaderboard object from file
		if (this.objectSaveFileExists()) {
			this.leaderboardObject = this.deserialize();
		} else {
			this.leaderboardObject = new LeaderboardObject();
		}
		validate();
	}

	private void validate() {
		if (this.leaderboardObject == null) {
			throw new RuntimeException("Leaderboard object is null");
		}
		if (this.leaderboardObject.getData() == null) {
			throw new RuntimeException("Leaderboard data is null");
		}
		for (String username : leaderboardObject.getData().keySet()) {
			if (leaderboardObject.getData().get(username) < 0) {
				System.out.println("Invalid data for user " + username + ": xp=" + leaderboardObject.getData().get(username));
				leaderboardObject.getData().put(username, 0);
			}
		}
	}

	public int getXP(Member member) {
		return this.leaderboardObject.getData().getOrDefault(member.getId(), 0);
	}

	public void addXP(Member member, int amount) {
		this.leaderboardObject.getData().put(member.getId(), this.getXP(member) + amount);
	}

	public void serialize() {
		try (FileOutputStream fileOutputStream = new FileOutputStream(FILENAME);
		     ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
			 objectOutputStream.writeObject(this.leaderboardObject);
		} catch (IOException e) {
			e.fillInStackTrace();
			System.out.println("Error serializing file");
		}
	}

	private LeaderboardObject deserialize() {
		LeaderboardObject deserializedObject = null;
		try (ObjectInputStream objectInputStream = new ObjectInputStream(Files.newInputStream(Paths.get(FILENAME)))) {
			deserializedObject = (LeaderboardObject) objectInputStream.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.fillInStackTrace();
			System.out.println("Error deserializing file");
		}
		return deserializedObject != null ? deserializedObject : new LeaderboardObject();
	}

	private boolean objectSaveFileExists() {
		File file = new File(FILENAME);
		return file.exists();
	}

}
