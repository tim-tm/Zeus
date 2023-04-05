package me.tim.features.event.api;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EventManager {
    private static final Map<Class<? extends Event>, ArrayList<EventData>> REG_MAP = new HashMap<>();

    private static void sortListVal(final Class<? extends Event> clazz) {
        final ArrayList<EventData> tempData = new ArrayList<>();
        for (final byte b : EventPriority.VALUE_ARRAY) {
            for (EventData eventData : EventManager.REG_MAP.get(clazz)) {
                if (eventData.priority == b) {
                    tempData.add(eventData);
                }
            }
        }

        EventManager.REG_MAP.put(clazz, tempData);
    }

    private static boolean isBadMethod(final Method method) {
        return method.getParameterTypes().length != 1 || !method.isAnnotationPresent(EventTarget.class);
    }

    private static boolean isBadMethod(final Method method, final Class<? extends Event> clazz) {
        return isBadMethod(method) || method.getParameterTypes()[0].equals(clazz);
    }

    public static ArrayList<EventData> get(final Class<? extends Event> clazz) {
        return REG_MAP.get(clazz);
    }

    public static void cleanMap(final boolean onlyEmpty) {
        final Iterator<Map.Entry<Class<? extends Event>, ArrayList<EventData>>> iterator = EventManager.REG_MAP.entrySet().iterator();
        while (iterator.hasNext()) {
            if (!onlyEmpty || iterator.next().getValue().isEmpty()) {
                iterator.remove();
            }
        }
    }

    public static void unregister(final Object o, final Class<? extends Event> clazz) {
        if (REG_MAP.containsKey(clazz)) {
            REG_MAP.get(clazz).removeIf(eventData -> eventData.source.equals(o));
        }
        cleanMap(true);
    }

    public static void unregister(final Object o) {
        for (ArrayList<EventData> value : REG_MAP.values()) {
            for (int i = value.size() - 1; i >= 0; i--) {
                if (value.get(i).source.equals(o)) {
                    value.remove(i);
                }
            }
        }
        cleanMap(true);
    }

    public static void register(final Method method, final Object o) {
        final Class<?> clazz = method.getParameterTypes()[0];
        final EventData methodData = new EventData(o, method, method.getAnnotation(EventTarget.class).value());

        if (!methodData.target.isAccessible()) {
            methodData.target.setAccessible(true);
        }

        if (REG_MAP.containsKey(clazz)) {
            if (!REG_MAP.get(clazz).contains(methodData)) {
                REG_MAP.get(clazz).add(methodData);
                sortListVal((Class<? extends Event>) clazz);
            }
        } else {
            REG_MAP.put((Class<? extends Event>) clazz, new ArrayList<EventData>() {
                {
                    this.add(methodData);
                }
            });
        }
    }

    public static void register(final Object o, final Class<? extends Event> clazz) {
        for (Method method : o.getClass().getDeclaredMethods()) {
            if (!isBadMethod(method)) {
                register(method, o);
            }
        }
    }

    public static void register(Object o) {
        for (Method method : o.getClass().getDeclaredMethods()) {
            if (!isBadMethod(method)) {
                register(method, o);
            }
        }
    }
}
