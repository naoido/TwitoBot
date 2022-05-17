package com.naoido.discordbot.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.naoido.discordbot.token.Token;
import twitter4j.*;
import twitter4j.auth.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.naoido.discordbot.Main.jda;

public class TwitterUtil {
    //HashMap<token, List[userID, reqToken]>
    public static final HashMap<String, List<Object>> loginData = new HashMap<>();
    private static final String CALLBACK_URI = "https://twi.naoido.com/callback";
    //HashMap<String userID, Twitter>
    public static final HashMap<String, Twitter> userInstance = new HashMap<>();
    public static final String TWEET_URI_PATTERN = "^https?://(mobile.)?(www.)?twitter\\.com/\\w{4,15}/status/\\d{16,19}(\\?.*)?$";
    private static final List<String> mediaURL = new ArrayList<>();
    public static final File JSON_FILE = new File("urls.json");
    public static JsonNode json;
    private Long tweetID;
    private String userID;
    private boolean isGuild;
    private Twitter twitter;
    private String url;


    /**
     * インスタンス作成時にuserIDとguildかどうかを格納する
     * @param userID discordID
     * @param isGuild メッセージがGuildかどうか
     */
    public TwitterUtil(String userID, Boolean isGuild) {
        this.userID = userID;
        this.isGuild = isGuild;
    }

    public TwitterUtil() {}

    /**
     * メディアURLを取得する
     * @param tweetUrl 取得したいtweetURL
     * @return 取得したメディアURL
     * @throws TwitterException メディアURLやインスタンスが取得、作成できなかった場合
     * @throws IOException JSONファイルに格納できなかった場合
     */
    public String getMediaUrl(String tweetUrl) throws TwitterException, IOException {

        /*
        Guild内でダウンロードした場合 : true
        その場合、デフォルトのアカウントが使用される
        */
        if (this.isGuild) twitterBuild();
        else twitterBuildUser();

        this.getTweetID(tweetUrl);
        this.getMediaList(twitter.showStatus(this.tweetID));

        if (mediaURL.get(0).contains("tweet_video")) this.url = mediaURL.get(0);
        else if (mediaURL.get(0).contains("video")) this.url = this.getMaxSize();
        if (mediaURL.get(0).contains("pbs")) this.url = String.join(" ", mediaURL);
        if (!mediaURL.isEmpty()) saveCache();
        return this.url;
    }


    /**
     * URLからTweetIDを取得し、tweetIDに保存
     * @param url twitterのURL
     */
    private void getTweetID(String url) {
        Matcher m = Pattern.compile("\\d{16,19}").matcher(url);
        if (m.find()) this.tweetID = Long.parseLong(m.group());
        else this.tweetID = 0L;
    }


    /**
     * URLが正しいTweetIDかチェックする
     * @param url 文字列
     * @return tweetIDの場合true
     */
    public static boolean isTweetURL(String url) { return url.matches(TWEET_URI_PATTERN); }


    /**
     * デフォルトのインスタンスを作成しtwitterに格納
     */
    private void twitterBuild() {
        this.twitter = new TwitterFactory(new ConfigurationBuilder().setDebugEnabled(true)
                .setOAuthConsumerKey(Token.CONSUMER_KEY.getValue())
                .setOAuthConsumerSecret(Token.CONSUMER_SECRET.getValue())
                .setOAuthAccessToken(Token.ACCESS_TOKEN.getValue())
                .setOAuthAccessTokenSecret(Token.ACCESS_SECRET.getValue()).build()).getInstance();
    }

    /**
     * ユーザーがLoginしたアカウントのインスタンスを取得し、
     * twitterに格納
     */
    private void twitterBuildUser() {
        if (userInstance.containsKey(this.userID)) {
            this.twitter =  userInstance.get(this.userID);
        } else { twitterBuild(); }
    }

    /**
     * @param status tweetのStatus
     * 取得したStatus内に入っているmediaを取得し、
     * mediaURLに格納する
     */
    private void getMediaList(Status status) {
        mediaURL.clear();
        for (MediaEntity media :status.getMediaEntities()) {
            if (media.getType().equals("video")) {
                for (MediaEntity.Variant variant :media.getVideoVariants()) {
                    if (!variant.toString().contains(".m3u8")) mediaURL.add(variant.getUrl());
                }
            }
            if (media.getType().equals("animated_gif")) {
                for (MediaEntity.Variant variant :media.getVideoVariants()) {
                    mediaURL.add(variant.getUrl());
                }
            } else {
                mediaURL.add(media.getMediaURL() + "?name=orig");
            }
        }
    }

    /**
     * mediaURL内の動画URLを解析し、
     * 解像度が最大のURLを抽出する
     * @return 解像度が最大のURL
     */
    private String getMaxSize() {
        String maxURL = null;
        int max = 0;

        for (String url :mediaURL) {
            Matcher m = Pattern.compile("vid/\\d+x").matcher(url);
            if (m.find()) {
                int size = Integer.parseInt(m.group().replaceAll("\\D", ""));
                if (size > max) {
                    max = size;
                    maxURL = url;
                }
            }
        }
        return maxURL;
    }


    /**
     * APIのコール回数を節約するためにダウンロードされたURLをjsonファイルに格納
     * tweetIDからメディアURLを取得可能にする。
     * @throws IOException jsonファイルが読み込めない場合
     */
    private void saveCache() throws IOException {
        ObjectNode root = (ObjectNode) json;
        ObjectNode obj = root.putObject(String.valueOf(this.tweetID));

        obj.put("media", this.url);
        obj.put("public", this.isGuild);

        new ObjectMapper().writeValue(JSON_FILE, json);
    }


    /**
     * userIDからtwitterのscreen_nameを取得する。
     * @param userName twitterのID
     * @return twitterのscreen_name
     */
    public String getUserID(String userName) {
        twitterBuild();
        try {
            return String.valueOf(this.twitter.showUser(userName).getId());
        } catch (TwitterException e) {
            return null;
        }
    }


    /**
     * userInstanceにユーザーが格納されてるか確認する
     * @param userID discordID
     * @return 格納されていたらtrue
     */
    public static boolean isRegistered(String userID) { return userInstance.containsKey(userID); }


    /**
     * ログインするためのURLを取得する
     * @param userID discordID
     * @return ログイン用のURL
     * @throws TwitterException RequestTokenを取得できなかった場合
     */
    public static String getLoginUrl(String userID) throws TwitterException {
        RequestToken reqToken = new TwitterFactory(new ConfigurationBuilder().setDebugEnabled(true)
                .setOAuthConsumerKey(Token.CONSUMER_KEY.getValue())
                .setOAuthConsumerSecret(Token.CONSUMER_SECRET.getValue())
                .setOAuthAccessToken(null)
                .setOAuthAccessTokenSecret(null).build()).getInstance().getOAuthRequestToken(CALLBACK_URI);
        String uri = reqToken.getAuthenticationURL();
        loginData.put(getOAuth(uri), Arrays.asList(userID, reqToken));
        return uri;
    }


    /**
     * 生成されたログイン用のURIからoauth_tokenを取得
     * @param uri ログイン用のURI
     * @return oauth_token
     */
    private static String getOAuth(String uri) {
        return uri.replace("https://api.twitter.com/oauth/authenticate?oauth_token=", "");
    }


    /**
     * tokenからLoginを実行しているユーザーを取得
     * @param token oauth_token
     * @return Loginを実行しているユーザー名
     */
    public static String getLoginUserName(String token) {
        if (loginData.containsKey(token)) {
            return jda.getUserById((String) loginData.get(token).get(0)).getName();
        }
        return null;
    }


    /**
     * callbackされたverifierからAccessTokenを取得
     * @param verifier callbackされたoauth_verifier
     * @param token callbackされたoauth_token
     * @return ログインしたユーザーのAccessToken
     * @throws TwitterException アクセストークンが発行できなかった場合
     */
    public static AccessToken getAccessToken(String verifier, String token) throws TwitterException {
        RequestToken requestToken = (RequestToken) loginData.get(token).get(1);
        return new TwitterFactory(new ConfigurationBuilder().setDebugEnabled(true)
                .setOAuthConsumerKey(Token.CONSUMER_KEY.getValue())
                .setOAuthConsumerSecret(Token.CONSUMER_SECRET.getValue())
                .setOAuthAccessToken(null)
                .setOAuthAccessTokenSecret(null).build()).getInstance().getOAuthAccessToken(requestToken, verifier);
    }


    /**
     * ログインしたユーザーを保持する
     * @param token Login時のoauth_token
     * @param accessToken 保持するAccessToken
     * @throws TwitterException インスタンスを作成できなかった場合
     */
    public static void saveLoginData(String token, AccessToken accessToken) throws TwitterException {
        String userID = (String) loginData.get(token).get(0);
        Twitter twitter = new TwitterFactory(new ConfigurationBuilder().setDebugEnabled(true)
                .setOAuthConsumerKey(Token.CONSUMER_KEY.getValue())
                .setOAuthConsumerSecret(Token.CONSUMER_SECRET.getValue())
                .setOAuthAccessToken(null)
                .setOAuthAccessTokenSecret(null).build()).getInstance();

        twitter.setOAuthAccessToken(accessToken);
        loginData.remove(token);
        userInstance.put(userID, twitter);
    }
}
