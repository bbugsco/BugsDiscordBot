package com.github.bbugsco.leaderboard;

import java.io.Serializable;
import java.util.HashMap;

public class LeaderboardObject implements Serializable {

	private final HashMap<String, Integer> data;

	public LeaderboardObject() {
		this.data = new HashMap<>();
	}

	public HashMap<String, Integer> getData() {
		return data;
	}

}


