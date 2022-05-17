package com.naoido.discordbot.command;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;


public class Ping extends ListenerAdapter {

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        if (event.getUser().isBot()) return;
        if (event.getName().equalsIgnoreCase("ping")) {
            long time = System.currentTimeMillis();
            event.reply("Pong!").setEphemeral(true)
                    .flatMap(v -> event.getHook().editOriginalFormat("Pong: %d ms", System.currentTimeMillis() - time))
                    .queue();
        }
    }
}
