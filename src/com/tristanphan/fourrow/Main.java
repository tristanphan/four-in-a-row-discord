package com.tristanphan.fourrow;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static JDA jda;
    public static List<String> blacklist = new ArrayList<>();
    public static List<String> whitelist = new ArrayList<>();

    public static void main(String[] args) {

        if (args.length < 1 ||
                !Arrays.asList(args).contains("--token") ||
                (Arrays.asList(args).contains("--blacklist") && Arrays.asList(args).contains("--whitelist")) ||
                (Arrays.asList(args).indexOf("--whitelist") > 0 && Arrays.asList(args).indexOf("--whitelist") < Arrays.asList(args).indexOf("--token")) ||
                (Arrays.asList(args).indexOf("--blacklist") > 0 && Arrays.asList(args).indexOf("--blacklist") < Arrays.asList(args).indexOf("--token")) ||
                (args[Arrays.asList(args).indexOf("--token") + 1].startsWith("--"))) {
            System.out.println("Syntax:");
            System.out.println("java -jar FourInARow.jar --token <token> (--whitelist [channel] OR --whitelist [channel])");
            System.out.println("\nExamples:");
            System.out.println("java -jar FourInARow.jar --token MTdqrd0vGDV1dcF0QPjom6OB.NQxUhj.I4JjFHIympR3mVF3UiUbbD5VVbi --whitelist 132859372567384064 627243459867319745)");
            return;
        }

        // Establish connection
        JDABuilder builder = JDABuilder.createDefault(args[Arrays.asList(args).indexOf("--token") + 1]);

        // Blacklist and Whitelist Features
        if (Arrays.asList(args).contains("--blacklist")) blacklist.addAll(Arrays.asList(args).subList(Arrays.asList(args).indexOf("blacklist") + 1, args.length));
        if (Arrays.asList(args).contains("--whitelist")) whitelist.addAll(Arrays.asList(args).subList(Arrays.asList(args).indexOf("whitelist") + 1, args.length));

        try {

            // Open the bot
            jda = builder.build();

        } catch (LoginException e) {

            // Error management
            System.out.println();
            e.printStackTrace();
            System.out.println("\nLogin failed, the program cannot continue to run!");
            return;

        }

        // Open the game interface, which interacts with discord
        jda.addEventListener(new DiscordInterface());

    }
}
