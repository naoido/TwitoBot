package com.naoido.discordbot.web;

import com.naoido.discordbot.command.Login;
import com.naoido.discordbot.util.TwitterUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;

import java.io.IOException;

@WebServlet("/callback")
public class Callback extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setStatus(HttpServletResponse.SC_OK);
        res.setContentType(ContentType.HTML_UTF8.getType());

        String token = req.getParameter("oauth_token");
        String verifier = req.getParameter("oauth_verifier");

        //Tokenが保存されているものか確認
        if (token != null && TwitterUtil.loginData.containsKey(token)) {
            try {
                String discordName = TwitterUtil.getLoginUserName(token);
                AccessToken accessToken = TwitterUtil.getAccessToken(verifier, token);
                String twitterName = accessToken.getScreenName();

                res.getWriter().println("<h1>認証されました。</h1>");
                res.getWriter().println("<p>Discord-User : " + discordName + "</p>");
                res.getWriter().println("<p>Twitter-User : " + twitterName + "</p>");

                //認証が成功した場合ユーザーに通知する
                Login.sendMessage((String) TwitterUtil.loginData.get(token).get(0));
                TwitterUtil.saveLoginData(token, accessToken);

            } catch (TwitterException e) {
                res.getWriter().println("エラーが発生しました。");
                throw new RuntimeException(e);
            }
        } else {
            res.getWriter().println("""
                    <h1>認証できませんでした。</h1>
                    <p>もう一度最初からやり直してください。</p>
                    <br>
                    <p>※何度も発生する場合は運営に問い合わせてください。</p>
                    """);
        }
    }
}
