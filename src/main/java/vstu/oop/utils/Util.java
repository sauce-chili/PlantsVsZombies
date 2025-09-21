package vstu.oop.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Util {
    public final static String UNCHECKED = "unchecked";

    public static BufferedImage loadByPath(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    public record Pair<T1, T2>(T1 first, T2 second) {
//        public static <T1, T2> Pair<T1, T2> of(T1 first, T2 second) {
//            return new Pair<>(first, second);
//        }
//    }
}
