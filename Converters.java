package com.decor;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class Converters {
    private static Gson gson = new Gson();

    @TypeConverter
    public static String fromMapToString(Map<String, String> map) {
        return map == null ? null : gson.toJson(map);
    }

    @TypeConverter
    public static Map<String, String> fromStringToMap(String value) {
        Type mapType = new TypeToken<Map<String, String>>(){}.getType();
        return value == null ? null : gson.fromJson(value, mapType);
    }

    @TypeConverter
    public static String fromOrderItemsToString(List<Map<String, Object>> items) {
        return items == null ? null : gson.toJson(items);
    }

    @TypeConverter
    public static List<Map<String, Object>> fromStringToOrderItems(String value) {
        Type listType = new TypeToken<List<Map<String, Object>>>(){}.getType();
        return value == null ? null : gson.fromJson(value, listType);
    }
}
