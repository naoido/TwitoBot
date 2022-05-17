package com.naoido.discordbot.web;

public enum ContentType {
    HTML_UTF8("text/html;charset=utf-8"),
    APPLICATION_JSON("application/json");

    private final String type;
    ContentType(String type) { this.type = type; }
    public String getType() { return type; }
}
