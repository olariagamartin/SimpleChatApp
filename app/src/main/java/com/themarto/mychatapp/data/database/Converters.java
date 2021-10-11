package com.themarto.mychatapp.data.database;

import static com.themarto.mychatapp.utils.Utils.convertToBitmap;
import static com.themarto.mychatapp.utils.Utils.convertToByteCode;
import static com.themarto.mychatapp.utils.Utils.getChatRoomId;

import com.themarto.mychatapp.data.domain.ContactModel;
import com.themarto.mychatapp.data.domain.MessageModel;
import com.themarto.mychatapp.data.network.ContactDTO;
import com.themarto.mychatapp.data.network.MessageDTO;

public class Converters {

    public static ContactModel toContactModel (ContactEntity contactEntity) {
        ContactModel contactModel = new ContactModel();
        contactModel.setId(contactEntity.id);
        contactModel.setName(contactEntity.name);
        contactModel.setProfileImage(convertToBitmap(contactEntity.profileImage));
        contactModel.setOnline(contactEntity.online);
        return contactModel;
    }

    public static ContactEntity toContactEntity (ContactDTO contactDTO, byte[] imageData) {
        ContactEntity contactEntity = new ContactEntity();
        contactEntity.id = contactDTO.getUid();
        contactEntity.name = contactDTO.getName();
        contactEntity.profileImage = imageData;
        contactEntity.online = contactDTO.isOnline();
        return contactEntity;
    }

    public static MessageModel toMessageModel (MessageEntity messageEntity) {
        MessageModel messageModel = new MessageModel();
        messageModel.setMessage(messageEntity.message);
        messageModel.setReceiverId(messageEntity.receiverId);
        messageModel.setSenderId(messageEntity.senderId);
        messageModel.setTimestamp(messageEntity.timestamp);
        messageModel.setChatRoom(messageEntity.chatRoom);
        return messageModel;
    }

    public static MessageEntity toMessageEntity (MessageDTO messageDTO) {
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.id = messageDTO.getKey();
        messageEntity.message = messageDTO.getMessage();
        messageEntity.receiverId = messageDTO.getReceiverId();
        messageEntity.senderId = messageDTO.getSenderId();
        messageEntity.timestamp = messageDTO.getTimestamp();
        messageEntity.chatRoom = getChatRoomId(messageDTO.getSenderId(), messageDTO.getReceiverId());
        return messageEntity;
    }

    public static MessageDTO toMessageDTO (MessageModel messageModel, String key) {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setKey(key);
        messageDTO.setMessage(messageModel.getMessage());
        messageDTO.setReceiverId(messageModel.getReceiverId());
        messageDTO.setSenderId(messageModel.getSenderId());
        messageDTO.setTimestamp(messageModel.getTimestamp());
        return messageDTO;
    }

}
