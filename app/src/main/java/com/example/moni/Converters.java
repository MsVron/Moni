package com.example.moni;

import androidx.room.TypeConverter;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class Converters {
    private static final String DELIMITER = ";;";

    @TypeConverter
    public static String fromStringList(List<String> strings) {
        if (strings == null || strings.isEmpty()) {
            return null;
        }
        return String.join(DELIMITER, strings);
    }

    @TypeConverter
    public static List<String> toStringList(String data) {
        if (data == null) {
            return new ArrayList<>();
        }
        return Arrays.asList(data.split(DELIMITER));
    }
}