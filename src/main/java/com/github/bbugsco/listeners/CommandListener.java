package com.github.bbugsco.listeners;

import com.github.bbugsco.Bot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class CommandListener implements EventListener {

	private final Bot bot;

	public CommandListener(Bot bot) {
		this.bot = bot;
	}

	@Override
	public void onEvent(@NotNull GenericEvent genericEvent) {
		if (genericEvent instanceof MessageReceivedEvent) {
			Message message = ((MessageReceivedEvent) genericEvent).getMessage();
			String content = message.getContentRaw();
			if (content.startsWith("!xp")) {
				handleResponse(Command.XP, message, content);
			}
			else if (content.startsWith("!leaderboard")) {
				handleResponse(Command.LEADERBOARD, message, content);
			}

		}
	}

	public void handleResponse(Command command, Message message, String content) {
		switch (command) {
			case XP:
				String[] arguments = content.split(" ");
				if (arguments.length == 2) {
					if (message.getMentions().getMembers().size() != 1) {
						message.getChannel().sendMessage("Target player not found").queue();
						return;
					}
					Member member = message.getMentions().getMembers().get(0);
					message.getChannel().sendMessage("> " + member.getEffectiveName() + " has " + bot.getLeaderboard().getLeaderboardData().getRawXP(member) + " xp").queue();
					return;
				} else if (arguments.length == 1) {
					Member target = message.getMember();
					if (target == null) {
						message.getChannel().sendMessage("Target player not found").queue();
						return;
					}
					message.getChannel().sendMessage("> " + target.getEffectiveName() + " has " + bot.getLeaderboard().getLeaderboardData().getRawXP(target) + " xp").queue();
					return;
				}
			case LEADERBOARD:
				// Get Sender and scores
				Member commandSender = message.getMember();
				HashMap<Member, Double> scores = new HashMap<>();
				Guild guild = message.getGuild();
				loadMembers(guild).whenComplete((members, throwable) -> {
					if (throwable != null) {
						throwable.fillInStackTrace();
						System.out.println("Error loading members");
					} else {
						System.out.println(members);

						// Populate scores HashMap
						for (Member member : members) {
							double rawXP = bot.getLeaderboard().getLeaderboardData().getRawXP(member);
							System.out.println(member.getEffectiveName() + ": " + rawXP);
							if (member.getEffectiveName().equalsIgnoreCase("Bugs Server Bot")) continue;
							scores.put(member, rawXP);
						}

						// Sort Map
						Map<Member, Double> sortedMap = sortByValueDescending(scores);

						// 8 or fewer people
						if (scores.size() <= 8 || getRank(commandSender, sortedMap) <= 8) {
							StringBuilder builder = new StringBuilder();
							builder.append("Leaderboard:\n");
							int rank = 1;
							for (Map.Entry<Member, Double> entry : sortedMap.entrySet()) {
								Member member = entry.getKey();
								double xp = entry.getValue();
								builder.append("> ").append(rank++).append(") ").append(member.getEffectiveName()).append(": ").append(xp).append(" xp\n");
							}
							message.getChannel().sendMessage(builder.toString()).queue();
						} else {
							// Top 5
							StringBuilder builder = new StringBuilder();
							builder.append("Leaderboard:\n");
							for (int i = 5; i < sortedMap.size(); i++) {
								Member member = (Member) sortedMap.keySet().toArray()[i];
								double xp = sortedMap.get(member);
								builder.append("> ").append(member.getEffectiveName()).append(": ").append(xp).append(" xp\n");
							}

							// 3 close ranks to sender
							builder.append("\n");
							int rank = getRank(commandSender, sortedMap);
							if (commandSender == null) {
								message.getChannel().sendMessage(builder.toString()).queue();
								return;
							}
							builder.append("> ").append(rank - 1).append(") ").append(getByRank(rank - 1, sortedMap).getEffectiveName()).append(": ").append(bot.getLeaderboard().getLeaderboardData().getRawXP(getByRank(rank - 1, sortedMap))).append(" xp\n");
							builder.append("> ").append(rank).append(") ").append(getByRank(rank - 1, sortedMap).getEffectiveName()).append(": ").append(bot.getLeaderboard().getLeaderboardData().getRawXP(commandSender)).append(" xp\n");
							builder.append("> ").append(rank + 1).append(") ").append(getByRank(rank - 1, sortedMap).getEffectiveName()).append(": ").append(bot.getLeaderboard().getLeaderboardData().getRawXP(getByRank(rank + 1, sortedMap))).append(" xp\n");

							message.getChannel().sendMessage(builder.toString()).queue();
						}
					}
				});
		}
	}


	private Member getByRank(int i, Map<Member, Double> sortedMap) {
		return (Member) sortedMap.keySet().toArray()[i];
	}


	private int getRank(Member member, Map<Member, Double> sortedMap) {
		int rank = 0;
		for (int i = 0; i < sortedMap.size(); i++) {
			Member memberAtIndex = (Member) sortedMap.keySet().toArray()[i];
			if (memberAtIndex.equals(member)) return rank;
		}
		return -1;
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

}