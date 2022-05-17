package com.naoido.discordbot.command;

import com.naoido.discordbot.util.TwitterUtil;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class Logout extends ListenerAdapter {
    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        if (event.getName().equals("logout")) {
            if (TwitterUtil.isRegistered(event.getUser().getId())) {
                TwitterUtil.userInstance.remove(event.getUser().getId());
                event.reply("ログアウトに成功しました！").setEphemeral(true).queue();
            } else {
                event.reply("ログインしていなかったためログアウトできませんでした。").setEphemeral(true).queue();
            }
        }
    }
}
