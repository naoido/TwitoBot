package com.naoido.discordbot.listener;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.naoido.discordbot.command.Omikuji.schedule;

public class ReadyListener extends ListenerAdapter {
    public static final String ADMIN_ID = "279583321766494208";
    public static final String LASTED_START_TIME = new SimpleDateFormat("yyyy年MM月dd日HH時mm分ss秒").format(new Date());

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        event.getJDA().getUserById(ADMIN_ID).openPrivateChannel().complete().sendMessage("起動").queue();
        schedule();
    }
}
