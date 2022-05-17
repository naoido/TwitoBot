package com.naoido.discordbot.listener;

import com.naoido.discordbot.Main;
import com.naoido.discordbot.util.OmikujiUtil;
import com.naoido.discordbot.util.TwitterUtil;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import twitter4j.TwitterException;

import java.io.IOException;

import static com.naoido.discordbot.listener.ReadyListener.ADMIN_ID;
public class PrefixListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (!event.getMessage().getContentDisplay().startsWith(Main.PREFIX)) return;
        String[] args = event.getMessage().getContentRaw().split(" ");

        if (args[0].equalsIgnoreCase(Main.PREFIX + "startup")) {
            event.getChannel().sendMessage("起動した時間は`" + ReadyListener.LASTED_START_TIME + "`です。").queue();
            return;
        }

        if (args[0].equalsIgnoreCase(Main.PREFIX + "restart") && event.getAuthor().getId().equals(ADMIN_ID)) {
            event.getChannel().sendMessage("再起動します。").complete();
            try {
                Runtime.getRuntime().exec("systemctl restart twibot");
            } catch (IOException e) {
                event.getChannel().sendMessage("エラーが発生しました。").queue();
                e.printStackTrace();
            }
        }
    }
}
