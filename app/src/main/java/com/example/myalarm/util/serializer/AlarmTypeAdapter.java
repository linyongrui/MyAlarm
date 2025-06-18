package com.example.myalarm.util.serializer;

import com.example.myalarm.alarmtype.BaseAlarmType;
import com.example.myalarm.data.converter.AlarmTypeConverter;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class AlarmTypeAdapter implements JsonSerializer<BaseAlarmType>, JsonDeserializer<BaseAlarmType> {

    @Override
    public BaseAlarmType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return AlarmTypeConverter.stringToAlarmType(json.getAsString());
    }

    @Override
    public JsonElement serialize(BaseAlarmType src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(AlarmTypeConverter.alarmTypeToString(src));
    }
}
