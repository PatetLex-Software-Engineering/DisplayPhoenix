package com.patetlex.displayphoenix.util;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.StringWriter;

public class GsonHelper {

    public static String read(JsonReader jsonReader) {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);
        class Parser {
            public boolean parse() {
                try {
                    JsonToken nextToken = jsonReader.peek();
                    if (JsonToken.BEGIN_OBJECT.equals(nextToken)) {
                        jsonReader.beginObject();
                        jsonWriter.beginObject();
                        return false;
                    } else if (JsonToken.END_OBJECT.equals(nextToken)) {
                        jsonReader.endObject();
                        jsonWriter.endObject();
                        return false;
                    } else if (JsonToken.BEGIN_ARRAY.equals(nextToken)) {
                        jsonReader.beginArray();
                        jsonWriter.beginArray();
                        return false;
                    } else if (JsonToken.END_ARRAY.equals(nextToken)) {
                        jsonReader.endArray();
                        jsonWriter.endArray();
                        return false;
                    } else if (JsonToken.BOOLEAN.equals(nextToken)) {
                        boolean bool = jsonReader.nextBoolean();
                        jsonWriter.value(bool);
                        return false;
                    } else if (JsonToken.NULL.equals(nextToken)) {
                        jsonWriter.nullValue();
                        return false;
                    } else if (JsonToken.NAME.equals(nextToken)) {
                        String name = jsonReader.nextName();
                        jsonWriter.name(name);
                        return false;
                    } else if (JsonToken.STRING.equals(nextToken)) {
                        String value = jsonReader.nextString();
                        jsonWriter.value(value);
                        return false;
                    } else if (JsonToken.NUMBER.equals(nextToken)) {
                        long value = jsonReader.nextLong();
                        jsonWriter.value(value);
                        return false;
                    }
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
        }
        try {
            Parser parser = new Parser();
            while (jsonReader.hasNext()) {
                if (parser.parse()) {
                    break;
                }
            }
            parser.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }
}
