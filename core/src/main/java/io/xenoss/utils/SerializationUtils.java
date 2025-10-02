package io.xenoss.utils;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;

public class SerializationUtils {
    public static final Gson GSON_PRETTY_PRINT = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Timestamp.class, new TimestampAdapter())
            .create();

    public static final Gson GSON_NO_PRETTY_PRINT = new GsonBuilder()
            .registerTypeAdapter(Timestamp.class, new TimestampAdapter())
            .create();

    public static final XmlMapper XML_MAPPER = new XmlMapper();


    public static <T> T fromJson(byte[] json, Class<T> classOfT) {
        return fromJson(new String(json, StandardCharsets.UTF_8), classOfT);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return GSON_PRETTY_PRINT.fromJson(json, classOfT);
    }

    public static <T> T fromJson(JsonElement json, Class<T> classOfT) {
        return GSON_PRETTY_PRINT.fromJson(json, classOfT);
    }

    public static <T> String toJson(T object) {
        return toJson(object, true);
    }

    public static <T> String toJson(T object, boolean prettyPrint) {
        return (prettyPrint ? GSON_PRETTY_PRINT : GSON_NO_PRETTY_PRINT).toJson(object);
    }

    public static <T> JsonElement toJsonTree(T object) {
        return GSON_PRETTY_PRINT.toJsonTree(object);
    }

    @SneakyThrows
    public static <T> T fromXml(String xml, Class<T> classOfT) {
        return XML_MAPPER.readValue(xml, classOfT);
    }

    @SneakyThrows
    public static <T> String toXml(T object) {
        return XML_MAPPER.writeValueAsString(object);
    }

    public static <T> T clone(T object) {
        return fromJson(toJson(object), (Class<T>)object.getClass());
    }

    public static class TimestampAdapter extends TypeAdapter<Timestamp> {
        @Override
        public Timestamp read(JsonReader in) throws IOException {
            return new Timestamp(in.nextLong() * 1000);  // convert seconds to milliseconds
        }

        @Override
        public void write(JsonWriter out, Timestamp timestamp) throws IOException {
            out.value(timestamp == null ? null : timestamp.getTime() / 1000);  // convert milliseconds to seconds
        }
    }
}
