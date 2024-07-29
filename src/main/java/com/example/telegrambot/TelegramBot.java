package com.example.telegrambot;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Telegrambot extends TelegramLongPollingBot {

  private final Map<Long, List<String>> userPhotos = new HashMap<>();
  @Override
    public void onUpdateReceived(Update update) {
      if (update.getMessage().hasPhoto()) {
        long chat_id = update.getMessage().getChatId();
        String photoFileId = update.getMessage().getPhoto().get(0).getFileId();
        userPhotos.computeIfAbsent(chat_id, k -> new ArrayList<>()).add(photoFileId);
        reply(chat_id, "Photo received! Send more photos or type /order to send them to the admin.");
    }
        // Check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
      String message_text = update.getMessage().getText();
      long chat_id = update.getMessage().getChatId();
      int message_id = update.getMessage().getMessageId();
      String firstName = update.getMessage().getChat().getFirstName();
      String username = update.getMessage().getChat().getUserName();
             if (message_text.equals ("/show")) {
        SendMessage message = new SendMessage();
          message.setChatId(chat_id+"");
          message.setText("Here is your keyboard");

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<KeyboardRow>();
        KeyboardRow row;

       
          row = new KeyboardRow();
          row.add("Order Custom Stickers");
          keyboard.add(row);
          row = new KeyboardRow();
          row.add("Explore Trending Stickers");
          keyboard.add(row);
          row = new KeyboardRow();
          row.add("In Stok Stickers");
          keyboard.add(row);
        

        keyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(keyboardMarkup);

        delete (chat_id, message_id);

        sendText (message);
      }
      
      else if (message_text.equals("/start")){
        reply(chat_id, "Welcome to PixelStickers,👤 "+firstName+" !\n\n" +
                    "🎉 We're thrilled to have you here. PixelStickers, a branch of PixelLabs, specializes in high-quality physical stickers for phones and laptops. Our stickers are designed to add a touch of personality and protection to your devices. Explore our collection and find the perfect stickers for your gadgets. If you have any questions or need assistance, feel free to ask. Enjoy!");
      }
      else if(message_text.equals("/order")){
        sendPhotosToAdmin(chat_id,firstName,username);


      }

      else if (message_text.equals("Order Custom Stickers")){
        reply(chat_id, "Comming Soon!");
      }
      else if (message_text.equals("Explore Trending Stickers")){
        reply(chat_id, "Comming Soon!");
      }
      else if (message_text.equals("In Stock Stickers")){
        reply(chat_id, "Comming Soon!");
      }
        }
    }
    boolean reply (long chat_id, String message_text) {
      SendMessage message = new SendMessage();
        message.setChatId(chat_id+"");
        message.setText(message_text);
  
      return sendText (message);
    }
  boolean sendText(SendMessage message) {
    try {
      execute(message);
      return true;
    } catch (TelegramApiException e) {
      e.printStackTrace();
      return false;
    }
  }

  boolean delete(long chat_id, int message_id) {
    DeleteMessage del_message = new DeleteMessage();
    del_message.setChatId(chat_id + "");
    del_message.setMessageId(message_id);
    try {
      execute(del_message);
      return true;
    } catch (TelegramApiException e) {
      e.printStackTrace();
      return false;
    }
  }
  private void sendPhotosToAdmin(long chatId, String firstName , String username) {
        List<String> photos = userPhotos.get(chatId);
        if (photos == null || photos.isEmpty()) {
            reply(chatId, "No photos to send. Please send some photos first.");
            return;
        }

        // Replace with your admin chat ID
        long adminChatId = 6709762157L; // Replace with your admin chat ID

        for (String photoFileId : photos) {
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(String.valueOf(adminChatId));
            sendPhoto.setPhoto(new InputFile(photoFileId));
            try {
                execute(sendPhoto);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        
        reply(adminChatId,"order from "+ firstName +" @"+username );

        reply(chatId, "Your photos have been sent to the admin.");
        userPhotos.remove(chatId); // Clear the user's photos after sending
    }

  @Override
  public String getBotUsername() {
    return "YourBotUsername";
  }

  @Override
  public String getBotToken() {
    return "7488427246:AAEDjeK6omkZgU0X7gI-jNa06qeN9esUoXE";
  }
}
