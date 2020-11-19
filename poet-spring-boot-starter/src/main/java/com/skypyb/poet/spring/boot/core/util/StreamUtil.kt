package com.skypyb.poet.spring.boot.core.util;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class StreamUtil {

    public static void close(Closeable closeable) {
        if (Objects.isNull(closeable)) return;

        try {
            closeable.close();
        } catch (IOException e) {
            //忽略
        }
    }

    public static void close(Closeable... closeable) {
        if (Objects.isNull(closeable)) return;
        Arrays.stream(closeable).forEach(StreamUtil::close);
    }
}
