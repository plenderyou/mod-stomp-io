package com.jetdrone.vertx.mods.stomp;

import org.vertx.java.core.json.JsonObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Frame {

    final String command;
    final Map<String, String> headers = new HashMap<>();
    String body;

    Frame(String command) {
        this.command = command.toUpperCase();
    }

    JsonObject toJSON() {
        JsonObject json = new JsonObject();

        if (headers.keySet().size() > 0) {
            JsonObject jHeaders = new JsonObject();
            for (Map.Entry<String, String> kv : this.headers.entrySet()) {
                jHeaders.putString(kv.getKey(), kv.getValue());
            }
            json.putObject("headers", jHeaders);
        }
        if (body != null) {
            String mapping = headers.get("transformation");
            if ("jms-map-json".equals(mapping)) {
                json.putObject("body", new JsonObject(body));
            } else {
                json.putString("body", body);
            }
        }

        return json;
    }

    void parseHeader(String key, String value) {
        headers.put(key, unescape(value));
    }

    void putHeader(String key, String value) {
        headers.put(key, escape(value));
    }

    static String escape(String value) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bytes = value.getBytes();
        for( byte b : bytes) {
            switch(b) {
                case ':':
                    baos.write(92);
                    baos.write(99);
                    break;
                case 13:
                    baos.write(92);
                    baos.write(114);
                    break;
                case 10:
                    baos.write(92);
                    baos.write(110);
                    break;
                case 92:
                    baos.write(92);
                    baos.write(92);
                    break;
                default:
                    baos.write(b);
                    break;
            }
        }
        return new String( baos.toByteArray() );
    }

    static String unescape(String value) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        boolean backSlash = false;
        byte[] bytes = value.getBytes();
        for( byte b : bytes) {
            switch(b) {
                case 92:
                    if( backSlash ) { // Second backslash
                        baos.write('\\');
                        backSlash = false;
                    } else {
                        backSlash = true;
                    }
                    break;
                case 99:
                    if( backSlash ) {
                        baos.write(':');
                        backSlash = false;
                    } else {
                        baos.write(b);
                    }
                    break;
                case 114:
                    if( backSlash ) {
                        baos.write(13);
                        backSlash = false;
                    } else {
                        baos.write(b);
                    }
                    break;
                case 110:
                    if( backSlash ) {
                        baos.write(10);
                        backSlash = false;
                    } else {
                        baos.write(b);
                    }
                    break;
                default:
                    baos.write(b);
            }
        }
        return new String( baos.toByteArray() );
    }
}
