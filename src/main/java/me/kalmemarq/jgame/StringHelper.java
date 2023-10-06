package me.kalmemarq.jgame;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class StringHelper {
    private StringHelper() {
    }

    /**
     * Reads an InputStream as a string, with utf-8 encoding.
     * <p>
     * If any kind of exception is throwned an empty string will be returned.
     * </p>
     * @param inputStream the InputStream to read
     * @return the string content of the input stream
     */
    public static String readString(InputStream inputStream) {
        return StringHelper.readString(inputStream, StandardCharsets.UTF_8);
    }

    /**
     * Reads an InputStream as a string with the specified encoding.
     * <p>
     * If any kind of exception is throwned an empty string will be returned.
     * </p>
     * @param inputStream the InputStream to read
     * @return the string content of the input stream
     */
    public static String readString(InputStream inputStream, Charset encoding) {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, encoding))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append('\n');
            }
            builder.deleteCharAt(builder.length() - 1);
        } catch (Exception ignored) {
        }
        return builder.toString();
    }

    /**
     * Reads the lines of a InputStream, with utf-8 encoding.
     * <p>
     * If any kind of exception is throwned an empty list will be returned.
     * </p>
     * @param inputStream the InputStream to read
     * @return the lines of the InputStream
     */
    public static List<String> readLines(InputStream inputStream) {
        return StringHelper.readLines(inputStream, StandardCharsets.UTF_8);
    }

    /**
     * Reads the lines of a InputStream with the specified encoding.
     * <p>
     * If any kind of exception is throwned an empty list will be returned.
     * </p>
     * @param inputStream the InputStream to read
     * @return the lines of the InputStream
     */
    public static List<String> readLines(InputStream inputStream, Charset encoding) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, encoding))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (Exception ignored) {
        }
        return lines;
    }
}
