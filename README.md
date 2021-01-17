# Four In A Row Bot for Discord
A simple Discord bot that allows you to play Four In A Row (Connect 4) with other users!

## Screenshots
![An example of a game](https://user-images.githubusercontent.com/10486660/104428881-d6ca2180-5539-11eb-8706-250c333bb50e.png)

## How it works
This is a Discord bot, and the players interact with the bot through commands. Essentially, this is the game of Connect 4.

You challenge another player on Discord. You win when you get 4 of your discs in a row (horizontally, vertically, or diagonally) while preventing the opponent from getting the four-in-a-row.

### NOTE: As of right now, you must host the bot yourself!

## How To Play
1. Run the command `!challenge @User#1234`, replacing the user with an actual username
2. React with emojis (1-7) to select a column to place your disc in
    - React with ❌ to forfeit
    
## How To Install The Bot
### Creating the bot account
1. Go to the [Discord Developer Portal](https://discord.com/developers/applications)
2. Click `New Application`
3. Click on `Bot` on the left side to switch to that tab
4. Click `Add Bot` on the right side to add a bot
5. Copy the token and put it aside for later

### Inviting the bot to your server
6. Click on `OAuth2` on the left side
7. Under Scopes, check the `bot` option
8. Open the link to invite the bot to your server 
    - The link should look like this: `https://discord.com/api/oauth2/authorize?client_id=732668501328641883&permissions=0&scope=bot`
    
  #### Alternatively, use [this link](https://discord.com/api/oauth2/authorize?client_id=<client_id>&permissions=0&scope=bot) to skip <mark>Steps 8-9</mark>. Replace the `<client_id>` with your client ID from the `General Information` page

9. For the target Discord channel, make sure the bot has the following permissions:
   - Send Messages
      - Allows the bot to send the board into the chat
   - Manage Messages
      - Deletes older instances of the game board when if refreshes 
   - Read Message History
      - Safety check, ensure that the bot can find old instances of the board that need to be deleted
   - Add Reactions
      - The game is interacted with through reactions, so this is necessary to create the buttons
   
If you fail to provide the correct permissions, games will not run as intended!
10. Make sure you have Java installed on your target computer (version 8 minimum)
11. Download (or compile) the JAR file for this bot
12. To run the program, type:
```
java -jar FourInARow.jar --token <token you just copied>
```

### Channel Management
If you want to whitelist channels, add this to the end of your command:
`--whitelist <channel ID #1> <channel ID #2...>`

Or you can blacklist channels:
`--blacklist <channel ID #1> <channel ID #2...>`

You can add as many channel ID's as you need, but you cannot use whitelist AND blacklist together.

###Example command with whitelisting:
```
java -jar FourInARow.jar --token Nzk4NTg5Nzg1NDEyMzM3NzQ1.X_3OsQ.J9O6BvG-RfCTXlpXtb-Ck34qffY --whitelist 617175382444032030 652809232755943378
```

## Resources Used
- Java Runtime Environment (JRE) version 1.8.0_271
- Java Discord API (JDA) version 4.2.0_168
- Simple Logging Facade for Java (SLF4J) version 1.7.9