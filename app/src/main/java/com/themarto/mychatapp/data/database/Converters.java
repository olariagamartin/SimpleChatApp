package com.themarto.mychatapp.data.database;

import static com.themarto.mychatapp.utils.Utils.convertToBitmap;
import static com.themarto.mychatapp.utils.Utils.convertToByteCode;

import com.themarto.mychatapp.data.domain.ContactModel;
import com.themarto.mychatapp.data.network.ContactDTO;

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

}
