package com.naoido.discordbot.command;

import com.naoido.discordbot.util.TwitterUtil;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import twitter4j.TwitterException;

import java.io.IOException;
import java.util.Objects;

import static com.naoido.discordbot.util.TwitterUtil.isTweetURL;

public class Twitter extends ListenerAdapter {
    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {

        if (event.getName().equalsIgnoreCase("media")) {
            if (isTweetURL(event.getOptions().get(0).getAsString())) {
                try {
                    event.reply(new TwitterUtil(event.getUser().getId(), event.getChannelType().isGuild()).getMediaUrl(event.getOptions().get(0).getAsString()))
                            .setEphemeral(false).queue();
                    System.out.println(TwitterUtil.userInstance);
                } catch (IOException | TwitterException e) {
                    event.reply("エラーが発生しました。\n鍵垢の動画の場合はDMのみでダウンロードできます。").setEphemeral(true).queue();
                    throw new RuntimeException(e);
                }
            }
            return;
        }

        if (event.getName().equalsIgnoreCase("id")) {
            if (Objects.requireNonNull(event.getSubcommandName()).equals("get")) {
                if (!event.getOptions().get(0).getAsString().matches("[\\d\\w_]{3,15}")) {
                    event.reply("正しいuserIDを入力してください。").setEphemeral(true).queue();
                    return;
                }
                event.reply(event.getOptions().get(0).getAsString() + "さんのユーザーID : " +
                                Objects.requireNonNullElse(new TwitterUtil().getUserID(event.getOptions().get(0).getAsString()), "null"))
                        .setEphemeral(false).queue();
                return;
            }
            if (Objects.requireNonNull(event.getSubcommandName()).equals("link")) {
                event.reply("https://twitter.com/i/user/" + event.getOptions().get(0).getAsString()).setEphemeral(false).queue();
            }
        }
    }
}
