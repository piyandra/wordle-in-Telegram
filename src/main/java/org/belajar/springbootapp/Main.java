package org.belajar.springbootapp;


import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

public class Main {

    static final String TOKEN = "7282002640:AAHJlmR52O_HDE_i-hjYI2QDK-VXG8aI9nY";

    public static void main(String[] args) {
        try (TelegramBotsLongPollingApplication botsLongPollingApplication = new TelegramBotsLongPollingApplication()){
            botsLongPollingApplication.registerBot(TOKEN, new BotTebakHurufMain("7282002640:AAHJlmR52O_HDE_i-hjYI2QDK-VXG8aI9nY"));
            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
