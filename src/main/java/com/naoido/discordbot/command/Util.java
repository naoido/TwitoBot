package com.naoido.discordbot.command;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.utils.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.naoido.discordbot.util.Util.isPrime;

public class Util extends ListenerAdapter {
    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        if (event.getName().equalsIgnoreCase("util")) {
            switch (Objects.requireNonNull(event.getSubcommandName())) {
                case "prime" -> {
                    Pair<Boolean, String> result = isPrime((int) event.getOptions().get(0).getAsLong());
                    String answer = result.getLeft() ? "です！！！！" : "ではありません！！(" + result.getRight() + "で割り切れます！ " + ")";
                    event.reply(event.getOptions().get(0).getAsString() + "は" + "素数" + answer).setEphemeral(false).queue();
                }
                case "say" -> {
                    event.reply("正常実行！").setEphemeral(true).queue();
                    event.getChannel().sendMessage(event.getOptions().get(0).getAsString()).queue();
                }
            }
        }
    }
}
