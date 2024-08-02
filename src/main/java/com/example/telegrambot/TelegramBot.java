package com.example.telegrambot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
// import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
// import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;


public class Telegrambot extends TelegramLongPollingBot {

  private final Map<Long, UserPhotos> userPhotos = new HashMap<>();
  private static final long ADMIN_USER_ID = 6709762157L;
  @Override
    public void onUpdateReceived(Update update) {
      if (update.hasCallbackQuery()) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        int messageId = update.getCallbackQuery().getMessage().getMessageId();
        deleteMessage(chatId, messageId);
        String callbackData = update.getCallbackQuery().getData();
        if (callbackData.startsWith("view_order_")) {
            long userId = Long.parseLong(callbackData.split("_")[2]);
            showUserOrderPhotos(userId);
        }
        else if (callbackData.startsWith("complete_order_")) {
          long userId = Long.parseLong(callbackData.split("_")[2]);
          completeOrder(userId);
      }
      else  if (callbackData.startsWith("view_my_photos_")) {
        long userId = Long.parseLong(callbackData.split("_")[3]);
        showUserPhotos(userId, chatId, messageId);
    }
    }
      if (update.getMessage().hasPhoto()) {
        handlePhotoMessage(update);
    }
        // Check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
      String message_text = update.getMessage().getText();
      long chat_id = update.getMessage().getChatId();
      String firstName = update.getMessage().getChat().getFirstName();
      if (chat_id == ADMIN_USER_ID) {
        handleAdminCommands(message_text);
    }
            else if (message_text.equals ("/my_orders")) {
              showMyOrders(chat_id);
      }
      
      else if (message_text.equals("/start")){
        reply(chat_id, "Welcome to PixelStickers,👤 "+firstName+" !\n\n" +
                    "🎉 We're thrilled to have you here. PixelStickers, a branch of PixelLabs, specializes in high-quality physical stickers for phones and laptops. Our stickers are designed to add a touch of personality and protection to your devices. Explore our collection and find the perfect stickers for your gadgets. If you have any questions or need assistance, feel free to ask. Enjoy!", null);
      }
      else if(message_text.equals("/order")){
        sendPhotosToAdmin(chat_id);


      }
      else if (message_text.equals("/help")){

        reply(chat_id,"List of Available commands \n /order - place your order \n /my_orders - view your pending orders \n /clear - cancel any pending orders \n /trending - view trending stickers \n /feedback - give us your feedback on the bot \n /help - list of available commands ",null);
      }
        }
        
    }
    boolean reply (long chat_id, String message_text ,  InlineKeyboardMarkup markup) {
      SendMessage message = new SendMessage();
        message.setChatId(chat_id+"");
        message.setText(message_text);
  if(markup != null){
    message.setReplyMarkup(markup);
  }
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
  private void completeOrder(long userId) {
    UserPhotos userPhotosData = userPhotos.get(userId);
    if (userPhotosData != null) {
        reply(userId, "Your order is complete! You can now pick it up.", null);
        userPhotos.remove(userId); // Remove the user's photos after marking as complete
    }
}
  private void showUserOrderPhotos(long userId) {
    UserPhotos userPhotosData = userPhotos.get(userId);
    if (userPhotosData == null || userPhotosData.getPhotoFileIds().isEmpty()) {
        sendTextToAdmin("No photos found for User ID: " + userId,null);
        return;
    }

    StringBuilder photoList = new StringBuilder("Photos for User ID: " + userId + "\n");
    for (String photoId : userPhotosData.getPhotoFileIds()) {
        photoList.append("Photo ID: ").append(photoId).append("\n");
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(String.valueOf(ADMIN_USER_ID));
        sendPhoto.setPhoto(new InputFile(photoId));
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // Add buttons for "Mark as Complete"
    InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
    List<InlineKeyboardButton> buttons = new ArrayList<>();

    InlineKeyboardButton completeButton = new InlineKeyboardButton();
    completeButton.setText("Mark as Complete");
    completeButton.setCallbackData("complete_order_" + userId);

    buttons.add(completeButton);
    markup.setKeyboard(Collections.singletonList(buttons));

    sendTextToAdmin(photoList.toString(), markup);
}
  private void handleAdminCommands(String message_text) {
    if (message_text.startsWith("/view_photos")){
      displayPendingOrders();
    }
    else if (message_text.startsWith("/clear_data")){
      clearUserData();
    }
    else if (message_text.startsWith("/respond")){
      String[] parts = message_text.split(" ", 3);
      if (parts.length == 3) {
          long userId = Long.parseLong(parts[1]);
          String responseMessage = parts[2];
          respondToUser(userId, responseMessage);
      }
    }
}
private void displayPendingOrders() {
    StringBuilder ordersList = new StringBuilder("Pending Orders:\n");
    List<InlineKeyboardButton> buttons = new ArrayList<>();

    for (Map.Entry<Long, UserPhotos> entry : userPhotos.entrySet()) {
        long userId = entry.getKey();
        UserPhotos userPhotosData = entry.getValue();
        ordersList.append("User ID: ").append(userId)
                  .append(" | Name: ").append(userPhotosData.getFirstName())
                  .append(" | Username: @").append(userPhotosData.getUsername()).append("\n");
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("View " + userPhotosData.getFirstName());
        button.setCallbackData("view_order_" + userId);
        buttons.add(button);
    }

    InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
    markup.setKeyboard(Collections.singletonList(buttons));

    sendTextToAdmin(ordersList.toString(), markup);
}
private void showMyOrders(long chatId) {
  UserPhotos userPhotosData = userPhotos.get(chatId);
  
  if (userPhotosData == null || userPhotosData.getPhotoFileIds().isEmpty()) {
      reply(chatId, "You have no pending orders.", null);
      return;
  }

  int photoCount = userPhotosData.getPhotoFileIds().size();
  StringBuilder ordersList = new StringBuilder("Your Pending Orders:\n");
  ordersList.append("Number of Photos: ").append(photoCount).append("\n");
  ordersList.append("Status: Pending\n\n");

  // Create inline keyboard button
  InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
  List<InlineKeyboardButton> buttons = new ArrayList<>();

  InlineKeyboardButton viewPhotosButton = new InlineKeyboardButton();
  viewPhotosButton.setText("View Photos");
  viewPhotosButton.setCallbackData("view_my_photos_" + chatId);

  buttons.add(viewPhotosButton);
  markup.setKeyboard(Collections.singletonList(buttons));

  reply(chatId, ordersList.toString(), markup);
}
private void showUserPhotos(long userId, long chatId, int messageId) {
  UserPhotos userPhotosData = userPhotos.get(userId);
  if (userPhotosData == null || userPhotosData.getPhotoFileIds().isEmpty()) {
      sendTextToAdmin("No photos found for User ID: " + userId, null);
      return;
  }

  for (String photoId : userPhotosData.getPhotoFileIds()) {
      SendPhoto sendPhoto = new SendPhoto();
      sendPhoto.setChatId(String.valueOf(chatId));
      sendPhoto.setPhoto(new InputFile(photoId));
      try {
          execute(sendPhoto);
      } catch (TelegramApiException e) {
          e.printStackTrace();
      }
  }

}


private void handlePhotoMessage(Update update) {
  long chatId = update.getMessage().getChatId();
  String photoFileId = update.getMessage().getPhoto().get(0).getFileId();
  String firstName = update.getMessage().getChat().getFirstName();
  String username = update.getMessage().getChat().getUserName();

  // Store the photo file ID along with user information
  UserPhotos userPhotosData = userPhotos.computeIfAbsent(chatId, k -> new UserPhotos(firstName, username));
  userPhotosData.addPhoto(photoFileId);

  // Create the acknowledgment message
  String acknowledgmentMessage = "Photo received! Total photos uploaded: " + userPhotosData.getPhotoCount() + "\n ℹ️ /order if you are done uploading photos to place an order";

  // If there's a previous message, delete it
  if (userPhotosData.getLastMessageId() != null) {
      deleteMessage(chatId, userPhotosData.getLastMessageId());
  }

  // Send the new acknowledgment message and store the message ID
  SendMessage sendMessage = new SendMessage();
  sendMessage.setChatId(String.valueOf(chatId));
  sendMessage.setText(acknowledgmentMessage);
  
  try {
      // Send the message and store the message ID
      Message message = execute(sendMessage);
      userPhotosData.setLastMessageId(message.getMessageId());
  } catch (TelegramApiException e) {
      e.printStackTrace();
  }
}

private void clearUserData() {
  userPhotos.clear();
  sendTextToAdmin("All user data has been cleared.",null);
}
private void respondToUser(long userId, String message) {
  SendMessage sendMessage = new SendMessage();
  sendMessage.setChatId(String.valueOf(userId));
  sendMessage.setText(message);
  sendText(sendMessage);
}

private void sendTextToAdmin(String message, InlineKeyboardMarkup markup) {
  SendMessage sendMessage = new SendMessage();
  sendMessage.setChatId(String.valueOf(ADMIN_USER_ID)); // Replace with your admin chat ID
  sendMessage.setText(message);

  // Set the reply markup if provided
  if (markup != null) {
      sendMessage.setReplyMarkup(markup);
  }

  sendText(sendMessage);
}
boolean deleteMessage(long chatId, int messageId) {
  DeleteMessage delMessage = new DeleteMessage();
  delMessage.setChatId(String.valueOf(chatId));
  delMessage.setMessageId(messageId);
  try {
      execute(delMessage);
      return true;
  } catch (TelegramApiException e) {
      e.printStackTrace();
      return false;
  }
}
private void sendPhotosToAdmin(long chatId) {
  UserPhotos userPhotosData = userPhotos.get(chatId);
  if (userPhotosData == null || userPhotosData.getPhotoFileIds().isEmpty()) {
      reply(chatId, "No photos to send. Please send some photos first.",null);
      return;
  }

  int photoCount = userPhotosData.getPhotoFileIds().size();
  int totalPrice = photoCount * 10; // 10 Ethiopian Birr per sticker

  // Notify admin with user details and total price
  sendTextToAdmin("New order from " + userPhotosData.getFirstName() + " @" + userPhotosData.getUsername() +
                  ". Total Price: " + totalPrice + " Ethiopian Birr. Photos sent.",null);

  reply(chatId, "Your photos have been sent to the admin. Total Estimated Price: " + totalPrice + " Ethiopian Birr.",null);
  
}


  @Override
  public String getBotUsername() {
    return "YourBotUsername";
  }

  @Override
  public String getBotToken() {
    return "yourtoken";
  }
}
