package com.github.bbugsco.leaderboard;

import net.dv8tion.jda.api.entities.Member;

import java.io.Serializable;
import java.util.HashMap;

public class LeaderboardData implements Serializable {

	private final HashMap<Member, Integer> data;

	public LeaderboardData(HashMap<Member, Integer> data) {
		this.data = data;
	}

	public LeaderboardData() {
		this.data = new HashMap<>();
	}

	public HashMap<Member, Integer> getData() {
		return data;
	}

}


