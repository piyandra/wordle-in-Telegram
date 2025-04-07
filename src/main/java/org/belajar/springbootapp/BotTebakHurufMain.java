package org.belajar.springbootapp;

import lombok.extern.slf4j.Slf4j;
import org.belajar.springbootapp.utils.UserWordUtils;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class BotTebakHurufMain implements LongPollingSingleThreadUpdateConsumer {

    private static final String GREEN_BOX = "\uD83D\uDFE9";
    private static final String YELLOW_BOX = "\uD83D\uDFE8";
    private static final String RED_BOX = "\uD83D\uDFE5";

    private final TelegramClient telegramClient;
    private final Map<Long, UserState> userStateMap;
    private final Map<Long, String> userAnswerMap;

    public BotTebakHurufMain(String botToken) {
        this.telegramClient = new OkHttpTelegramClient(botToken);
        this.userStateMap = new HashMap<>();
        this.userAnswerMap = new HashMap<>();
    }

    @Override
    public void consume(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        Long chatId = update.getMessage().getChatId();
        String inputText = update.getMessage().getText().trim();

        if (inputText.equalsIgnoreCase("/start")) {
            startGame(chatId);
            return;
        }

        if (userStateMap.get(chatId) != UserState.PLAYING) {
            message("Ketik /start untuk memulai permainan.", chatId);
            return;
        }

        if (inputText.equalsIgnoreCase("/nyerah")) {
            giveUp(chatId);
            return;
        }

        if (inputText.length() != 5) {
            message("Tebakanmu harus terdiri dari 5 huruf!", chatId);
            return;
        }

        handleGuess(chatId, inputText.toUpperCase());
    }

    private void startGame(Long chatId) {
        try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/wordlist.csv"))) {
            String[] words = br.lines().toArray(String[]::new);
            int randomIndex = (int) (Math.random() * words.length);
            String answer = words[randomIndex].toUpperCase();

            userAnswerMap.put(chatId, answer);
            userStateMap.put(chatId, UserState.PLAYING);

            message("Tebak huruf dari kata:\n" + GREEN_BOX + GREEN_BOX + GREEN_BOX + GREEN_BOX + GREEN_BOX + "\nKata terdiri dari 5 huruf yang terdaftar di KBBI.", chatId);
        } catch (Exception e) {
            log.error("Error reading word list: {}", e.getMessage());
            message("Terjadi kesalahan saat memulai permainan.", chatId);
        }
    }

    private void giveUp(Long chatId) {
        String answer = userAnswerMap.get(chatId);
        message("Kata yang benar adalah: " + answer, chatId);
        userStateMap.put(chatId, UserState.STARTED);
        userAnswerMap.remove(chatId);
    }

    private void handleGuess(Long chatId, String guess) {
        String answer = userAnswerMap.get(chatId);
        char[] resultChars = new UserWordUtils().userWordListRequest(answer, guess);

        StringBuilder result = new StringBuilder();
        for (char c : resultChars) {
            switch (c) {
                case 'Y' -> result.append(GREEN_BOX);
                case 'B' -> result.append(YELLOW_BOX);
                case 'X' -> result.append(RED_BOX);
            }
        }

        if (result.toString().contains(YELLOW_BOX) || result.toString().contains(RED_BOX)) {
            message("Tebakanmu: " + result, chatId);
        } else {
            message("Selamat, kamu sudah menebak dengan benar!", chatId);
            userStateMap.put(chatId, UserState.STARTED);
            userAnswerMap.remove(chatId);
        }
    }

    private void message(String text, Long chatId) {
        try {
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text(text)
                    .build();
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Gagal mengirim pesan: {}", e.getMessage());
        }
    }
}
