package ru.justnanix.bebraproxy.network.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Skin {
    private final GameProfile ownerGameProfile;

    public String getValue() {
        return ownerGameProfile.getProperty("textures").getValue();
    }

    public String getSignature() {
        return ownerGameProfile.getProperty("textures").getSignature();
    }
}