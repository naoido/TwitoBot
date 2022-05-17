package com.naoido.discordbot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naoido.discordbot.command.*;
import com.naoido.discordbot.listener.PrefixListener;
import com.naoido.discordbot.listener.ReadyListener;
import com.naoido.discordbot.listener.URLListener;
import com.naoido.discordbot.token.Token;
import com.naoido.discordbot.util.TwitterUtil;
import com.naoido.discordbot.web.WebServer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.Arrays;

public class Main {
    public static JDA jda;
    public static final String PREFIX = "!";

    public static void main(String[] args) throws Exception {
        //Cashを読み込む
        TwitterUtil.json = new ObjectMapper().readTree(TwitterUtil.JSON_FILE);
        System.out.println(TwitterUtil.json);

        //botを起動
        jda = JDABuilder.create(Arrays.asList(GatewayIntent.values())).setToken(Token.DISCORD_TOKEN.getValue())
                .addEventListeners(new Ping(), new Twitter(), new URLListener(), new ReadyListener(), new Omikuji(), new Invite(),
                        new PrefixListener(), new Util(), new Logout(), new Login())
                .build();

        //SlashCommandを設定
        jda.updateCommands()
                .addCommands(new CommandData("ping", "Pingを表示します。"))
                .addCommands(new CommandData("omikuji", "一日に1回おみくじを引けます。")
                        .addSubcommands(new SubcommandData("auto", "自動化設定")
                                .addOptions(new OptionData(OptionType.STRING, "mode", "自動化を有効/無効にする。").setRequired(true)
                                        .addChoice("on", "on").addChoice("off", "off").addChoice("list", "list")))
                        .addSubcommands(new SubcommandData("draw", "おみくじを引くよ！")))
                .addCommands(new CommandData("invite", "招待URLを取得できます。"))
                .addCommands(new CommandData("media", "twitterURLからMediaURLを取得します。")
                        .addOptions(new OptionData(OptionType.STRING, "url", "TweetURL").setRequired(true)))
                .addCommands(new CommandData("id", "ユーザーIDに関するデータを取得します。")
                        .addSubcommands(new SubcommandData("get", "ユーザーIDを取得します。")
                                .addOptions(new OptionData(OptionType.STRING, "name", "ユーザー名").setRequired(true)))
                        .addSubcommands(new SubcommandData("link", "ユーザーIDからURLを生成します。")
                                .addOptions(new OptionData(OptionType.STRING, "id", "ユーザーID").setRequired(true))))
                .addCommands(new CommandData("util", "様々なコマンドが使えます。")
                        .addSubcommands(new SubcommandData("prime", "素数を判定できます。")
                                .addOptions(new OptionData(OptionType.INTEGER, "数字", "判定する数を入力してください。(仕様上intの最大値までです。)").setRequired(true)))
                        .addSubcommands(new SubcommandData("say", "botに喋らせれるよ！")
                                .addOptions(new OptionData(OptionType.STRING, "内容", "話す内容を指定してね！").setRequired(true))))
                .addCommands(new CommandData("login", "ログインすることによって鍵垢の動画も取得可能になります。"))
                .addCommands(new CommandData("logout", "ログイン中のアカウントからログアウトできます。"))
                .queue();
        System.out.println("startup");
        //webServer起動
        WebServer.run();
    }
}
