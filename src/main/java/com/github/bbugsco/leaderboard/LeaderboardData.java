package com.github.bbugsco.leaderboard;

import net.dv8tion.jda.api.entities.Member;

import java.io.Serializable;
import java.util.HashMap;

public class LeaderboardData implements Serializable {

	private final HashMap<Member, Double> data;

	public LeaderboardData() {
		this.data = new HashMap<>();
	}

	public HashMap<Member, Double> getData() {
		return data;
	}

	public double getRawXP(Member member) {
		return data.getOrDefault(member, 0.0);
	}

}


