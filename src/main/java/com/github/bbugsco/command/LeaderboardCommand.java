package com.github.bbugsco.command;

import com.github.bbugsco.Bot;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class LeaderboardCommand implements BotCommand {

	@Override
	public void handle(Bot bot, Message message) {
		// Get Sender and scores
		Member commandSender = message.getMember();
		HashMap<Member, Integer> scores = new HashMap<>();
		Guild guild = message.getGuild();
		loadMembers(guild).whenComplete((members, throwable) -> {
			if (throwable != null) {
				throwable.fillInStackTrace();
				System.out.println("Error loading members");
			} else {
				// Populate scores HashMap
				for (Member member : members) {
					int rawXP = bot.getLeaderboard().getXP(member);
					if (member.getEffectiveName().equalsIgnoreCase("Bugs Server Bot")) continue;
					scores.put(member, rawXP);
				}

				// Sort Map
				Map<Member, Integer> sortedMap = sortByValueDescending(scores);

				// 8 or fewer people
				if (scores.size() <= 8 || getRank(commandSender, sortedMap) <= 8) {
					StringBuilder builder = new StringBuilder();
					builder.append("Leaderboard:\n");
					int rank = 1;
					int count = 0;
					for (Map.Entry<Member, Integer> entry : sortedMap.entrySet()) {
						if (count >= 8) break;
						Member member = entry.getKey();
						int xp = entry.getValue();
						builder.append("> ").append(rank++).append(") ").append(member.getEffectiveName()).append(": ").append(xp).append(" xp\n");
						count++;
					}
					message.getChannel().sendMessage(builder.toString()).queue();
				} else {
					// Top 5
					StringBuilder builder = new StringBuilder();
					builder.append("Leaderboard:\n");
					for (int i = 0; i < 5 || i < sortedMap.size(); i++) {
						Member member = (Member) sortedMap.keySet().toArray()[i];
						int xp = sortedMap.get(member);
						builder.append("> ").append(member.getEffectiveName()).append(": ").append(xp).append(" xp\n");
					}

					// 3 close ranks to sender
					builder.append("\n");
					int rank = getRank(commandSender, sortedMap);
					if (commandSender == null) {
						message.getChannel().sendMessage(builder.toString()).queue();
						return;
					}
					builder.append("> ").append(rank - 1).append(") ").append(getByRank(rank - 1, sortedMap).getEffectiveName()).append(": ").append(bot.getLeaderboard().getXP(getByRank(rank - 1, sortedMap))).append(" xp\n");
					builder.append("> ").append(rank).append(") ").append(getByRank(rank - 1, sortedMap).getEffectiveName()).append(": ").append(bot.getLeaderboard().getXP(commandSender)).append(" xp\n");
					builder.append("> ").append(rank + 1).append(") ").append(getByRank(rank - 1, sortedMap).getEffectiveName()).append(": ").append(bot.getLeaderboard().getXP(getByRank(rank + 1, sortedMap))).append(" xp\n");

					message.getChannel().sendMessage(builder.toString()).queue();
				}
			}
		});
	}

	private static <K, V extends Comparable<? super V>> Map<K, V> sortByValueDescending(Map<K, V> map) {
		return map.entrySet()
				.stream()
				.sorted(Map.Entry.<K, V>comparingByValue().reversed())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}


	private CompletableFuture<List<Member>> loadMembers(Guild guild) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return guild.loadMembers().get();
			} catch (Exception e) {
				e.fillInStackTrace();
				System.out.println("Error loading members");
				return null;
			}
		});
	}

	private Member getByRank(int i, Map<Member, Integer> sortedMap) {
		return (Member) sortedMap.keySet().toArray()[i];
	}

	private int getRank(Member member, Map<Member, Integer> sortedMap) {
		int rank = 0;
		for (int i = 0; i < sortedMap.size(); i++) {
			Member memberAtIndex = (Member) sortedMap.keySet().toArray()[i];
			if (memberAtIndex.equals(member)) return rank;
		}
		return -1;
	}
}