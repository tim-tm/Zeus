package me.tim.util.common;

import me.tim.Statics;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class FileUtil {
    public static String readFile(ResourceLocation location) {
        String path = getRealPath(location);
        StringBuilder builder = new StringBuilder();
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    builder.append(scanner.nextLine()).append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return builder.toString();
    }

    public static String getRealPath(ResourceLocation resourceLocation) {
        return Statics.getMinecraft().mcDataDir.getAbsolutePath().replace(".", "") + "assets/" + resourceLocation.getResourceDomain() + "/" + resourceLocation.getResourcePath();
    }

    public static File getDataDir() {
        File file = new File(Statics.getMinecraft().mcDataDir.getAbsolutePath().replace(".", "") + "Zeus");
        if (!file.exists()) file.mkdirs();
        return file;
    }
}
