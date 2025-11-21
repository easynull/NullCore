package com.mw.nullcore.core;

import javax.management.ReflectionException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;

public final class NcReflect {
    public static MethodHandles.Lookup privateLookup(final Class<?> targetClass) {
        try {
            return MethodHandles.privateLookupIn(targetClass, MethodHandles.lookup());
        } catch (IllegalAccessException e) {
            try {
                throw new ReflectionException(e);
            } catch (ReflectionException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static MethodHandle findMethod(final MethodHandles.Lookup privateLookup, final String methodName, final Class<?> returnType, final Class<?>... parameterTypes) {
        try {
            return privateLookup.findVirtual(privateLookup.lookupClass(), methodName, MethodType.methodType(returnType, parameterTypes));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            try {
                throw new ReflectionException(e);
            } catch (ReflectionException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static VarHandle findField(final MethodHandles.Lookup privateLookup, String fieldName, Class<?> fieldType) {
        try {
            return privateLookup.findVarHandle(privateLookup.lookupClass(), fieldName, fieldType);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            try {
                throw new ReflectionException(e);
            } catch (ReflectionException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
