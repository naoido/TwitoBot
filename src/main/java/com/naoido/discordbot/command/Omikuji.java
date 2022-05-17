package com.naoido.discordbot.command;

import com.naoido.discordbot.util.OmikujiUtil;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

public class Omikuji extends ListenerAdapter {
    public static void schedule() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                schedule();
                OmikujiUtil.omikujiResult.clear();
                OmikujiUtil.notifyUserList.forEach(OmikujiUtil::sendOmikujiResult);
            }
        };

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tokyo"));
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE) + 1, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        new Timer(false).schedule(task, calendar.getTime());
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        if (event.getName().equalsIgnoreCase("omikuji")) {
            if (Objects.requireNonNull(event.getSubcommandName()).equals("auto")) {
                if (event.getOptions().get(0).getAsString().equals("on")) {
                    try {
                        OmikujiUtil.addNotifyUser(event.getUser());
                        event.reply("自動でおみくじを毎日引きます！").setEphemeral(true).queue();
                    } catch (IOException e) {
                        event.reply("エラーが発生しました。").setEphemeral(true).queue();
                        throw new RuntimeException(e);
                    }
                    return;
                }
                if (event.getOptions().get(0).getAsString().equals("off")) {
                    try {
                        OmikujiUtil.removeNotifyUser(event.getUser());
                        event.reply("自動リストから削除したよ！").setEphemeral(true).queue();
                    } catch (IOException e) {
                        event.reply("エラーが発生しました。").setEphemeral(true).queue();
                        throw new RuntimeException(e);
                    }
                    return;
                }
            }
            if (Objects.requireNonNull(event.getSubcommandName()).equals("draw")) {
                event.reply("おみくじを引いたよ！！").setEphemeral(true).queue();
                if (OmikujiUtil.omikujiResult.containsKey(event.getUser().getId())) {
                    event.getChannel().sendMessage(OmikujiUtil.getOmikujiEmbed(event.getUser())).queue();
                    return;
                }
                OmikujiUtil.drawOmikuji(event.getUser().getId());
                event.getChannel().sendMessage(OmikujiUtil.getOmikujiEmbed(event.getUser())).queue();
            }
        }
    }
}
