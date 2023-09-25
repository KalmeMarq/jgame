package me.kalmemarq.jgame.bso;

import java.util.Map;
import java.util.regex.Pattern;

public class BsoHelper {
    private static final Pattern SIMPLE_KEY_NAME = Pattern.compile("[A-Za-z0-9._+-]+");

    public static String writeAsString(BsoTag tag) {
        return BsoHelper.writeAsString(tag, false);
    }

    public static String writeAsString(BsoTag tag, boolean indented) {
        StringBuilder builder = new StringBuilder();
        BsoHelper.writeAsString(builder, tag, indented, 0);
        return builder.toString();
    }

    private static void writeAsString(StringBuilder builder, BsoTag tag, boolean indented, int level) {
        if (tag instanceof BsoBoolean booleanTag) {
            builder.append(booleanTag.booleanValue() ? "true" : "false");
        } else if (tag instanceof BsoUByte ubyteTag) {
            builder.append(ubyteTag.unsignedByteValue()).append("ub");
        } else if (tag instanceof BsoByte byteTag) {
            builder.append(byteTag.byteValue()).append('b');
        } else if (tag instanceof BsoUShort ushortTag) {
            builder.append(ushortTag.unsignedShortValue()).append("us");
        } else if (tag instanceof BsoShort shortTag) {
            builder.append(shortTag.shortValue()).append('s');
        } else if (tag instanceof BsoUInt uintTag) {
            builder.append(uintTag.unsignedIntValue()).append("ui");
        } else if (tag instanceof BsoInt intTag) {
            builder.append(intTag.intValue());
        } else if (tag instanceof BsoLong longTag) {
            builder.append(longTag.longValue()).append('L');
        } else if (tag instanceof BsoFloat floatTag) {
            builder.append(floatTag.floatValue()).append('f');
        } else if (tag instanceof BsoDouble doubleTag) {
            builder.append(doubleTag.doubleValue()).append('d');
        } else if (tag instanceof BsoString stringTag) {
            builder.append(BsoHelper.escape(stringTag.getValue()));
        } else if (tag instanceof BsoMap mapTag) {
            builder.append('{');
            int size = mapTag.size();

            if (indented && size > 0) {
                builder.append('\n');
            }

            int i = 0;
            for (Map.Entry<String, BsoTag> entry : mapTag.entrySet()) {
                String key = entry.getKey();
                if (indented) {
                    builder.append("  ".repeat(Math.max(0, level + 1)));
                }
                builder.append(BsoHelper.SIMPLE_KEY_NAME.matcher(key).matches() ? key : BsoHelper.escape(key));
                builder.append(':');
                if (indented) {
                    builder.append(' ');
                }
                BsoHelper.writeAsString(builder, entry.getValue(), indented, level + 1);

                if (i + 1 < size) {
                    builder.append(',');
                    if (indented) {
                        builder.append('\n');
                    }
                }
                ++i;
            }

            if (indented && size > 0) {
                builder.append('\n');
                builder.append("  ".repeat(Math.max(0, level)));
            }
            builder.append('}');
        } else if (tag instanceof BsoList listTag) {
            builder.append('[');
            int size = listTag.size();

            if (indented && size > 0) {
                builder.append('\n');
            }

            int i = 0;
            for (BsoTag item : listTag) {
                if (indented) {
                    builder.append("  ".repeat(Math.max(0, level + 1)));
                }
                BsoHelper.writeAsString(builder, item, indented, level + 1);
                if (i + 1 < size) {
                    builder.append(',');
                    if (indented) {
                        builder.append('\n');
                    }
                }
                ++i;
            }
            if (indented && size > 0) {
                builder.append('\n');
                builder.append("  ".repeat(Math.max(0, level)));
            }
            builder.append(']');
        } else if (tag instanceof BsoByteArray byteArrayTag) {
            builder.append("[B;");
            byte[] bytes = byteArrayTag.getValues();
            for (int i = 0; i < bytes.length; ++i) {
                builder.append(bytes[i]).append('b');
                if (i + 1 < bytes.length) {
                    builder.append(',');
                    if (indented) {
                        builder.append(' ');
                    }
                }
            }
            builder.append(']');
        } else if (tag instanceof BsoShortArray shortArrayTag) {
            builder.append("[S;");
            short[] values = shortArrayTag.getValues();
            for (int i = 0; i < values.length; ++i) {
                builder.append(values[i]).append('s');
                if (i + 1 < values.length) {
                    builder.append(',');
                    if (indented) {
                        builder.append(' ');
                    }
                }
            }
            builder.append(']');
        } else if (tag instanceof BsoIntArray intArrayTag) {
            builder.append("[I;");
            int[] values = intArrayTag.getValues();
            for (int i = 0; i < values.length; ++i) {
                builder.append(values[i]);
                if (i + 1 < values.length) {
                    builder.append(',');
                    if (indented) {
                        builder.append(' ');
                    }
                }
            }
            builder.append(']');
        }
    }

    private static String escape(String value) {
        StringBuilder stringBuilder = new StringBuilder(" ");
        int c = 0;
        for (int i = 0; i < value.length(); ++i) {
            int d = value.charAt(i);
            if (d == 92) {
                stringBuilder.append('\\');
            } else if (d == 34 || d == 39) {
                if (c == 0) {
                    int n = c = d == 34 ? 39 : 34;
                }
                if (c == d) {
                    stringBuilder.append('\\');
                }
            }
            stringBuilder.append((char)d);
        }
        if (c == 0) {
            c = 34;
        }
        stringBuilder.setCharAt(0, (char)c);
        stringBuilder.append((char)c);
        return stringBuilder.toString();
    }
}
