package com.naoido.discordbot.web;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.servlet.ServletHandler;

public class WebServer extends HandlerWrapper {
    private static final ServletHandler handler = new ServletHandler();
    private static final int PORT = 8070;


    public static void run() throws Exception {
        Server server = new Server(PORT);

        handler.addServletWithMapping(Callback.class, "/callback");
        server.setHandler(handler);

        /*Webサーバを起動*/
        server.start();
        server.join();
    }
}
