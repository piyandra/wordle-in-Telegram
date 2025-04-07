package org.belajar.springbootapp;


import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

public class Main {

    static final String TOKEN = "";

    public static void main(String[] args) {
        try (TelegramBotsLongPollingApplication botsLongPollingApplication = new TelegramBotsLongPollingApplication()){
            botsLongPollingApplication.registerBot(TOKEN, new BotTebakHurufMain(""));
            Thread.currentThread().join();
        } catch (Exception e) {
        }
    }
}
