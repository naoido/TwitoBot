package com.naoido.discordbot.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import com.naoido.discordbot.util.TwitterUtil;
import twitter4j.TwitterException;

import java.util.concurrent.TimeUnit;

import static com.naoido.discordbot.Main.jda;

public class Login extends ListenerAdapter {
    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        if (event.getName().equals("login")) {
            if (TwitterUtil.isRegistered(event.getUser().getId())) {
                event.reply("既に登録済みです。").setEphemeral(true).queue(msg ->
                        msg.deleteOriginal().queueAfter(3, TimeUnit.MINUTES));
                return;
            }
            try {
                event.reply("以下のURLを開き認証作業を行ってください。\n" +
                        TwitterUtil.getLoginUrl(event.getUser().getId())).setEphemeral(true).queue(msg ->
                        msg.deleteOriginal().queueAfter(3, TimeUnit.MINUTES));
            } catch (TwitterException e) {
                event.reply("エラーが発生しました。").setEphemeral(true).queue(msg ->
                        msg.deleteOriginal().queueAfter(3, TimeUnit.MINUTES));
            }
        }
    }
    public static void sendMessage(String userID) {
        MessageEmbed embed = new EmbedBuilder()
                .setTitle("ログインに成功しました！")
                .setDescription("心当たりのないログインの場合は/logoutを実行してください。").build();
        jda.getUserById(userID).openPrivateChannel().complete().sendMessage(embed).queue();
    }
}
