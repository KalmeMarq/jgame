package me.kalmemarq.jgame.bso;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;

public class BsoIo {
    public static void write(Path filePath, BsoTag tag) {
        try (DataOutputStream output = new DataOutputStream(new FileOutputStream(filePath.toFile()))) {
            output.write(tag.getIdWithAdditionalData());
            tag.write(output);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static BsoTag read(Path filePath) {
        try (DataInputStream input = new DataInputStream(new FileInputStream(filePath.toFile()))) {
            byte id = input.readByte();
            BsoType<?> type = BsoTypes.byId((byte) (id & 0x0F));
            if (type != null) {
                return type.read(input, (byte) ((id & 0xF0) >> 4));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
