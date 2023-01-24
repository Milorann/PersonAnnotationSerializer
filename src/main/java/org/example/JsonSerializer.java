package org.example;

import org.json.JSONObject;
import org.reflections.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class JsonSerializer<T> {
    private final Set<Field> publishedFields;

    public JsonSerializer(Class<T> serializedClass) {
        publishedFields = new HashSet<>();
        Objects.requireNonNull(serializedClass);
        for (Field field : ReflectionUtils.getAllFields(serializedClass)) {
            Published published = field.getAnnotation(Published.class);
            if (published != null) {
                field.setAccessible(true);
                publishedFields.add(field);
            }
        }
    }

    public JSONObject serialize(T o) {
        JSONObject result = new JSONObject();
        for (Field field :
                publishedFields) {
            /* Вообще, выстрелить не должно, т. к.
            в конструкторе в сет добавлялись поля с setAccessible(true). */
            try {
                result.put(field.getName(), field.get(o));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }
}
