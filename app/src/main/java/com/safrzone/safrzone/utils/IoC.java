package com.safrzone.safrzone.utils;

import java.util.HashMap;

/**
 * Simple inversion of control container
 */
public class IoC {
    private static HashMap<Class, Object> _objects = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> T resolve(Class<T> type) {
        T result = null;
        if (_objects.containsKey(type)) {
            result = (T) _objects.get(type);
        } else {
            try {
                result = type.newInstance();
                _objects.put(type, result);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public static void register(Object object) {
        _objects.put(object.getClass(), object);
    }
}
