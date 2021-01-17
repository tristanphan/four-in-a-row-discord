package com.tristanphan.fourrow;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DiscordInterface extends ListenerAdapter {

    // Instance of the Discord Game Wrapper
    public HashMap<String, DiscordGameWrapper> games = new HashMap<>();
    public final static String[] selector = {
            "1️⃣",
            "2️⃣",
            "3️⃣",
            "4️⃣",
            "5️⃣",
            "6️⃣",
            "7️⃣",
            "❌"
    };

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        super.onReady(event);

        // Ready indicator
        System.out.println("Ready!");

        // Set bot status
        Main.jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(Activity.ActivityType.DEFAULT, "!challenge @User#1234"));

    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        super.onGuildMessageReceived(event);

        // Check permissions
        if (!event.getGuild().getSelfMember().hasPermission(event.getChannel(), Permission.MESSAGE_WRITE)) return;
        if (!event.getGuild().getSelfMember().hasPermission(event.getChannel(), Permission.MESSAGE_MANAGE)) return;
        if (!event.getGuild().getSelfMember().hasPermission(event.getChannel(), Permission.MESSAGE_HISTORY)) return;
        if (!event.getGuild().getSelfMember().hasPermission(event.getChannel(), Permission.MESSAGE_ADD_REACTION)) return;

        // Check if the channel is blacklisted or whitelisted
        if (!Main.whitelist.isEmpty()) {
            if (!Main.whitelist.contains(event.getChannel().getId())) return;
        } else if (!Main.blacklist.isEmpty()) {
            if (Main.blacklist.contains(event.getChannel().getId())) return;
        }


        // Command management
        String message = event.getMessage().getContentDisplay();
        String[] args = message.split(" ");

        // Start game
        if (args[0].equalsIgnoreCase("!challenge")) {

            // Users that are mentioned
            User[] mentioned = event.getMessage().getMentionedUsers().toArray(new User[0]);

            // Check to make sure that one player is mentioned
            if (mentioned.length != 1) {
                event.getChannel().sendMessage("To start a game with a player, type\n!challenge @User#1234").complete().delete().queueAfter(2, TimeUnit.SECONDS);
                event.getMessage().delete().queueAfter(2, TimeUnit.SECONDS);
                return;
            }

            // Check to make sure that a player, not a bot, is mentioned
            if (mentioned[0].isBot()) {
                event.getChannel().sendMessage("The player must not be a bot!").complete().delete().queueAfter(2, TimeUnit.SECONDS);
                event.getMessage().delete().queueAfter(2, TimeUnit.SECONDS);
                return;
            }

            // Check to make sure that the player is not yourself, which will cause issues
            if (mentioned[0].getId().equals(event.getAuthor().getId())) {
                event.getChannel().sendMessage("You cannot play against yourself!").complete().delete().queueAfter(5, TimeUnit.SECONDS);
                event.getMessage().delete().queueAfter(2, TimeUnit.SECONDS);
                return;
            }

            // Create an instance of the game wrapper for Discord
            // Each Discord Game Wrapper is assigned an ID
            String id = UUID.randomUUID().toString();
            while (games.containsKey(id)) id = UUID.randomUUID().toString();
            games.put(id, new DiscordGameWrapper(event.getChannel(), event.getAuthor(), mentioned[0]));

            // Print the board
            String[] board = games.get(id).getDisplay();
            games.get(id).setMessage(games.get(id).getChannel().sendMessage(board[0]).complete());

            // Add emotes
            for (String s : selector) games.get(id).getMessage().addReaction(s).queue();

            // Get rid of the game if there is a win or tie
            if (Integer.parseInt(board[1]) >= 0) {
                games.get(id).getMessage().clearReactions().queue();
                games.remove(id);
            }

        }

    }

    @Override
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
        super.onGuildMessageReactionAdd(event);

        // Defer from bots
        if (event.getUser().isBot()) return;

        event.getReaction().removeReaction(event.getUser()).queue();

        // Make sure the emote is from the right game
        if (!games.values().stream().map(instance -> instance.getMessage().getId()).collect(Collectors.toList()).contains(event.getMessageId())) return;

        // Find game instance
        DiscordGameWrapper game = (DiscordGameWrapper) games.values().toArray()[games.values().stream().map(instance -> instance.getMessage().getId()).collect(Collectors.toList()).indexOf(event.getMessageId())];

        // Find emote used
        int selected = Arrays.asList(selector).indexOf(event.getReactionEmote().getName()) + 1;

        // Check if selected exists
        if (selected == 0) return;

        // If forfeit and is a player
        if (selected == 8 && Arrays.stream(game.getPlayers()).map(ISnowflake::getId).collect(Collectors.toList()).contains(event.getUser().getId())) {

                // Announce forfeit
                game.getMessage().delete().complete();
                game.setMessage(game.getChannel().sendMessage(game.getBoard() + "\n\n**<@" + event.getUser().getId() + "> FORFEITED!**").complete());

                // Clear the reactions
                for (String s : games.keySet()) if (games.get(s).equals(game)) {
                    games.get(s).getMessage().clearReactions().queue();
                    games.remove(s);
                }
                return;

        }

        // Pass argument onto the game wrapper
        String result = game.playMove(event.getUser(), selected);

        // Second-hand error management through the wrapper

        // If message is not from a player
        if (result.isEmpty()) return;

        // Edit the board on success
        String[] board = game.getDisplay();
        if (game.getMessage().getId().equals(game.getChannel().getLatestMessageId())) {
            // If the board is the most recent message, edit it
            game.getMessage().editMessage(board[0]).queue();
        } else {
            // Otherwise, delete the message and resend it
            game.getMessage().delete().complete();
            game.setMessage(game.getChannel().sendMessage(board[0]).complete());
            for (String s : selector) game.getMessage().addReaction(s).queue();
        }

        // Get rid of the game if there is a win or tie
        if (Integer.parseInt(board[1]) >= 0) {
            for (String s : games.keySet()) if (games.get(s).equals(game)) {
                games.get(s).getMessage().clearReactions().queue();
                games.remove(s);
            }
        }
    }
}
