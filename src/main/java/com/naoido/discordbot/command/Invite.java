package com.naoido.discordbot.command;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class Invite extends ListenerAdapter {
    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        if (event.getName().equalsIgnoreCase("invite")) {
            event.reply("https://discord.com/api/oauth2/authorize?client_id=381680291384655873&permissions=8&scope=applications.commands%20bot")
                    .setEphemeral(true).queue();
        }
    }
}
