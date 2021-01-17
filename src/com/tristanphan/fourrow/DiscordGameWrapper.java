package com.tristanphan.fourrow;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class DiscordGameWrapper {

    // Interpret the display through emojis
    String symbol1 = "🟡";
    String symbol2 = "🔴";
    String emptySymbol = "⬛";
    String arrowUp = "🔼";
    // 🔴⚫🔵⚪🟣🟡🟤🟢🟠🔘

    // Game properties
    private final TextChannel channel;
    private Message message;
    private final User player1;
    private final User player2;

    // Keeps track of the last move to display
    private int mostRecentMove = 0;

    // Actual game itself, with game settings
    private final FourInARow game = new FourInARow(6, 7, 4);

    // Set up game parameters
    public DiscordGameWrapper(TextChannel aChannel, User aPlayer1, User aPlayer2) {
        channel = aChannel;
        player1 = aPlayer1;
        player2 = aPlayer2;
    }


    public String[] getDisplay() {

        // Convert the text-based game output to emoji-based for Discord

        String display = getBoard();

        display += "\n";

        // Indicator of where the last player went
        if (mostRecentMove != 0) {
            // Create char array for string multiplication {\000, \000, \000 ,\000}
            char[] chars = new char[7];
            // Set the placeholder "a" for the indicator
            chars[mostRecentMove - 1] = 'a';
            display += new String(chars).replace("\0", emptySymbol).replace("a",arrowUp);
        } else {
            display += new String(new char[7]).replace("\0", emptySymbol);
        }

        display += "\n";

        // Bottom line
        int gameStatus = game.getWinner();

        // If there is no win
        if (gameStatus == -1) {

            // If it is Player 1's turn
            if (game.getTurn() == 1) display += "**<@" + player1.getId() + "> " + symbol1 + ", it is your turn!**";

            // If it is Player 2's turn
            if (game.getTurn() == 2) display += "**<@" + player2.getId() + "> " + symbol2 + ", it is your turn!**";
        }

        // If there is a tie
        if (gameStatus == 0) display += "**IT IS A TIE!** Thank you for playing, <@" + player1.getId() + "> " + symbol1 + " and <@" + player2.getId() + "> " + symbol2;

        // If Player 1 won
        if (gameStatus == 1) display += "**<@" + player1.getId() + "> WON!**  Thank you for participating, <@" + player2.getId() + "> " + symbol2;

        // If Player 2 won
        if (gameStatus == 2) display += "**<@" + player2.getId() + "> WON!**  Thank you for participating, <@" + player1.getId() + "> " + symbol1;

        // Return both the print and the round's exit code for win check
        return new String[]{display, Integer.toString(gameStatus)};
    }

    public String getBoard() {

        // Title
        String display = "**__"
                + player1.getName()
                + " vs. "
                + player2.getName()
                + "__**\n";

        // Board
        display += game.getBoard().replace("0", emptySymbol).replace("1", symbol1).replace("2", symbol2);

        // Guide
        display += "\n1️⃣2️⃣3️⃣4️⃣5️⃣6️⃣7️⃣";

        return display;
    }

    public String playMove(User user, int column) {

        // Get author's identity
        int player = -1;
        if (player1.getId().equals(user.getId())) player = 1;
        else if (player2.getId().equals(user.getId())) player = 2;

        // If author is not a player
        if (player == -1) return "";

        // Pass the returning print from the game and pass it to the interface
        String result = game.playMove(player, column);
        if (result.equalsIgnoreCase("Done!")) mostRecentMove = column;
        return result;
    }

    public TextChannel getChannel() {
        return channel;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public User[] getPlayers() {
        return new User[]{ player1, player2 };
    }
}
