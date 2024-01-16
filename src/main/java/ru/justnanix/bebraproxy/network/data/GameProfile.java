package ru.justnanix.bebraproxy.network.data;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.*;

@Getter @Setter @ToString
public class GameProfile {
    private String name;
    private UUID uuid;
    private List<Property> properties;
    private Map<TextureType, Texture> textures;
    private Skin skin;

    public GameProfile(final String uuid, final String name) {
        this((uuid == null || uuid.isEmpty()) ? null : UUID.fromString(uuid), name);
    }

    public GameProfile(final UUID uuid, final String name) {
        if (uuid == null && (name == null || name.isEmpty())) {
            throw new IllegalArgumentException("Name and ID cannot both be blank");
        }
        this.uuid = uuid;
        this.name = name;
    }

    public GameProfile(final String name, final UUID uuid) {
        if (uuid == null && (name == null || name.isEmpty())) {
            throw new IllegalArgumentException("Name and ID cannot both be blank");
        }
        this.uuid = uuid;
        this.name = name;
    }

    public boolean isComplete() {
        return this.uuid != null && this.name != null && !this.name.equals("");
    }

    public String getIdAsString() {
        return (this.uuid != null) ? this.uuid.toString() : "";
    }

    public List<Property> getProperties() {
        if (this.properties == null) {
            this.properties = new ArrayList<>();
        }

        return this.properties;
    }

    public Property getProperty(final String name) {
        for (final Property property : this.getProperties()) {
            if (property.getName().equals(name)) {
                return property;
            }
        }
        return null;
    }

    public Map<TextureType, Texture> getTextures() {
        if (this.textures == null) {
            this.textures = new HashMap<>();
        }
        return this.textures;
    }

    public Texture getTexture(final TextureType type) {
        return this.getTextures().get(type);
    }

    public enum TextureType {
        SKIN,
        CAPE
    }

    public enum TextureModel {
        NORMAL,
        SLIM
    }

    @Data @ToString
    public static class Property {
        private final String name;
        private final String value;
        private final String signature;

        public boolean hasSignature() {
            return this.signature != null;
        }
    }

    public static class Texture {
        private final String url;
        private final Map<String, String> metadata;

        public Texture(final String url, final Map<String, String> metadata) {
            this.url = url;
            this.metadata = metadata;
        }

        public String getURL() {
            return this.url;
        }

        public TextureModel getModel() {
            final String model = (this.metadata != null) ? this.metadata.get("model") : null;
            return (model != null && model.equals("slim")) ? TextureModel.SLIM : TextureModel.NORMAL;
        }
    }
}