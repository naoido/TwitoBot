package com.naoido.discordbot.util;

import com.naoido.discordbot.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class OmikujiUtil {
    public static final List<String> list = new ArrayList<>(Arrays.asList("大大吉","大吉","中吉","小吉","吉","末吉","凶","大凶"));
    private static final File NOTIFY_USER_FILE = new File("notifyUserList");
    public static final HashMap<String, List<String>> omikujiResult = new HashMap<>();
    public static final List<User> notifyUserList = new ArrayList<>();
    private static final Random random = new Random();
    private static int result;


     static {
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(NOTIFY_USER_FILE));
            String line;
            while ((line = br.readLine()) != null) {
                notifyUserList.add(Main.jda.getUserById(line));
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * おみくじの結果を取得
     */
    public static void getResult() {
        int r = random.nextInt(100);
        if (r == 99) {
            result = 0;
        } else if (r >= 76) {
            result = 1;
        } else if (r >= 66) {
            result = 2;
        } else if (r >= 53) {
            result = 3;
        } else if (r >= 29) {
            result = 4;
        } else if (r >= 10) {
            result = 5;
        } else if (r > 0) {
            result = 6;
        } else {
            result = 7;
        }
    }


    /**
     * おみくじの項目を評価
     * @return 評価結果
     */
    private static String eval() {
        int r = random.nextInt(4);
        if (list.get(result).equals("大凶")) r = 3;
        if (r == 0 || list.get(result).equals("大大吉")) {
            return "◎";
        } else if (r == 1) {
            return "〇";
        } else if (r == 2) {
            return "△";
        } else {
            return "×";
        }
    }


    /**
     * おみくじを引く
     * @param userID おみくじを引いたdiscordID
     */
    public static void drawOmikuji(String userID) {
        List<String> resultList = new ArrayList<>();
        getResult();
        String love = eval();
        String waiter = eval();
        String study = eval();
        String health = eval();
        String trip = eval();
        String business = eval();
        Collections.addAll(resultList, list.get(result), love, waiter, study, health, trip, business);
        omikujiResult.put(userID, resultList);
    }


    /**
     * 自動で引くユーザーを登録
     * @param user discordID
     * @throws IOException ファイルに保存できなかった場合
     */
    public static void addNotifyUser(User user) throws IOException {
        if (containUser(user)) return;
        notifyUserList.add(user);
        FileWriter filewriter = new FileWriter(NOTIFY_USER_FILE, true);
        filewriter.append(user.getId()).append("\n");
        filewriter.close();
    }


    /**
     * 自動化しているユーザーファイルにユーザーが登録されているか判定
     * @param user discordUser
     * @return 判定結果
     * @throws IOException ファイルを開けなかった場合
     */
    private static boolean containUser(User user) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(NOTIFY_USER_FILE));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.equals(user.getId())) {
                reader.close();
                return true;
            }
        }
        return false;
    }


    /**
     * 自動化リストからユーザーを削除
     * @param user discordUser
     * @throws IOException ファイルを開けなかった場合
     */
    public static void removeNotifyUser(User user) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(NOTIFY_USER_FILE));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.equals(user.getId())) {
                notifyUserList.remove(user);
                continue;
            }
            stringBuilder.append(line).append("\n");
        }
        reader.close();
        FileWriter filewriter = new FileWriter(NOTIFY_USER_FILE);
        filewriter.append(stringBuilder.toString());
        filewriter.close();
    }


    /**
     * 結果をembedにする
     * @param user discordUser
     * @return おみくじ結果をembedにしたもの
     */
    public static MessageEmbed getOmikujiEmbed(User user) {
        String userID = user.getId();
        return new EmbedBuilder().setTitle("今日の" + user.getName() + "さんのおみくじ結果")
                .setColor(new Color(0x38A1A1))
                .addField("運勢", omikujiResult.get(userID).get(0), false)
                .addField("恋愛", omikujiResult.get(userID).get(1), true)
                .addField("待ち人", omikujiResult.get(userID).get(2), true)
                .addField("勉強", omikujiResult.get(userID).get(3), true)
                .addField("健康", omikujiResult.get(userID).get(4), true)
                .addField("旅行", omikujiResult.get(userID).get(5), true)
                .addField("商売", omikujiResult.get(userID).get(6), true).build();
    }


    /**
     * 指定したユーザーにおみくじ結果を送信
     * @param user discordUser
     */
    public static void sendOmikujiResult(User user) {
        String userID = user.getId();
        drawOmikuji(userID);
        user.openPrivateChannel().flatMap(channel -> channel.sendMessage(getOmikujiEmbed(user))).queue();
    }
}
