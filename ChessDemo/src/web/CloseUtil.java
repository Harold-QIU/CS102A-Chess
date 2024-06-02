package web;

import java.io.Closeable;
import java.io.IOException;

public class CloseUtil {
    public static void close(Closeable...close) {
        for (Closeable closeable : close) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
