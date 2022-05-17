package com.naoido.discordbot.listener;

import com.naoido.discordbot.util.TwitterUtil;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import twitter4j.TwitterException;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.naoido.discordbot.Main.jda;

public class URLListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getMessage().getContentDisplay().matches(TwitterUtil.TWEET_URI_PATTERN)) {
            event.getMessage().addReaction("⬇️").queue();
        }
    }


    /**
     * Bot自身の反応かどうかを判定
     * @param msg Message
     * @return 結果
     */
    private boolean isSelfBotReaction(Message msg) {
        AtomicBoolean isContain = new AtomicBoolean(false);
        msg.retrieveReactionUsers("⬇️").forEach(user -> {
            if (user.equals(jda.getSelfUser())) isContain.set(true);
        });
        return isContain.get();
    }

    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (Objects.requireNonNull(event.getUser()).isBot()) return;

        if (event.getReactionEmote().getName().equals("⬇️")) {
            String url = event.getChannel().retrieveMessageById(event.getMessageId()).complete().getContentRaw();

            if (event.getUser().isBot() || !isSelfBotReaction(event.getChannel().retrieveMessageById(event.getMessageId()).complete())) return;

            event.getChannel().removeReactionById(event.getMessageId(), "⬇️").queue();

            try {
                event.getChannel().retrieveMessageById(event.getMessageId()).complete()
                        .reply(new TwitterUtil(event.getUser().getId(), event.getChannelType().isGuild()).getMediaUrl(url))
                        .mentionRepliedUser(false).queue();
            } catch (TwitterException | IOException e) {
                event.getChannel().retrieveMessageById(event.getMessageId()).complete()
                        .reply("エラーが発生しました。").mentionRepliedUser(false).queue();
                throw new RuntimeException(e);
            }
        }
    }
}
