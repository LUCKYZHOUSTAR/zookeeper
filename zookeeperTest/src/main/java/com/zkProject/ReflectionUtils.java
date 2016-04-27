package com.zkProject;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReflectionUtils {
    private static final Logger logger = LoggerFactory.getLogger(ReflectionUtils.class);

    public static Method getDeclaredMethod(Object object, String methodName,
                                           Class<?>[] parameterTypes) {
        Method method = null;
        for (Class clazz = object.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                return clazz.getDeclaredMethod(methodName, parameterTypes);
            } catch (Throwable ex) {
            }

        }

        return null;
    }

    public static Object invokeMethod(Object object, String methodName, Class<?>[] parameterTypes,
                                      Object[] parameters) {
        Method method = getDeclaredMethod(object, methodName, parameterTypes);
        try {
            if (null != method) {
                method.setAccessible(true);

                return method.invoke(object, parameters);
            }
        } catch (IllegalArgumentException e) {
            LogUtils.error(logger, "invokeMethod %s process methodName:[%s] error[%s]. ",
                new Object[] { object, methodName, e.getMessage() });
        } catch (IllegalAccessException e) {
            LogUtils.error(logger, "invokeMethod %s process methodName:[%s] error[%s]. ",
                new Object[] { object, methodName, e.getMessage() });
        } catch (InvocationTargetException e) {
            LogUtils.error(logger, "invokeMethod %s process methodName:[%s] error[%s]. ",
                new Object[] { object, methodName, e.getMessage() });
        }

        return null;
    }

    public static Field getDeclaredField(Object object, String fieldName) {
        Field field = null;
        for (Class clazz = object.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (Throwable ex) {
            }

        }

        return null;
    }

    public static void setFieldValue(Object object, String fieldName, Object value) {
        Field field = getDeclaredField(object, fieldName);

        field.setAccessible(true);
        try {
            field.set(object, value);
        } catch (IllegalArgumentException e) {
            LogUtils.error(logger, "setFieldValue %s process fieldName:[%s] error[%s]. ",
                new Object[] { object, fieldName, e.getMessage() });
        } catch (IllegalAccessException e) {
            LogUtils.error(logger, "setFieldValue %s process fieldName:[%s] error[%s]. ",
                new Object[] { object, fieldName, e.getMessage() });
        }
    }

    public static Object getFieldValue(Object object, String fieldName) {
        Field field = getDeclaredField(object, fieldName);

        field.setAccessible(true);
        try {
            return field.get(object);
        } catch (Exception e) {
            LogUtils.error(logger, "getFieldValue %s process fieldName:[%s] error[%s]. ",
                new Object[] { object, fieldName, e.getMessage() });
        }

        return null;
    }

    public static void writeField(String fieldName, Object obj, Object value) {
        try {
            Class tClass = obj.getClass();
            Field field = tClass.getDeclaredField(fieldName);
            if (field != null) {
                PropertyDescriptor pd = new PropertyDescriptor(field.getName(), tClass);

                Method method = pd.getWriteMethod();
                method.invoke(obj, new Object[] { value });
                logger.debug("field:" + field.getName() + "---getValue:" + value);
            }
        } catch (Exception ex) {
            logger.error("set field[%s] error", fieldName, ex);
        }
    }
}
