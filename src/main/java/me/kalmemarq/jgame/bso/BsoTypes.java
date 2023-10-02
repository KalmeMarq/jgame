package me.kalmemarq.jgame.bso;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class BsoTypes {
    public static final BsoType<BsoByte> BYTE = new BsoType<>() {
        @Override
        public byte getId() {
            return 1;
        }

        @Override
        public BsoByte read(DataInput input, byte additionalData) throws IOException {
            return switch (additionalData) {
                case 4 -> new BsoBoolean(false);
                case 5 -> new BsoBoolean(true);
                default -> new BsoByte(input.readByte());
            };
        }

        @Override
        public void write(DataOutput output, BsoByte tag, byte additionalData) throws IOException {
            if (additionalData < 4) {
                output.write(tag.byteValue());
            }
        }
    };
    public static final BsoType<BsoShort> SHORT = new BsoType<>() {
        @Override
        public byte getId() {
            return 2;
        }

        @Override
        public BsoShort read(DataInput input, byte additionalData) throws IOException {
            short value;
            if (additionalData == 0) {
                value = input.readShort();
            } else if (additionalData == 4) {
                return new BsoUShort(input.readUnsignedShort());
            } else if (additionalData == 5) {
                return new BsoUShort(input.readUnsignedByte());
            } else {
                value = input.readByte();
            }
            return new BsoShort(value);
        }

        @Override
        public void write(DataOutput output, BsoShort tag, byte additionalData) throws IOException {
            if ((additionalData & 3) == 0) {
                output.writeShort(tag.shortValue());
            } else {
                output.write(tag.byteValue());
            }
        }
    };
    public static final BsoType<BsoInt> INT = new BsoType<>() {
        @Override
        public byte getId() {
            return 3;
        }

        @Override
        public BsoInt read(DataInput input, byte additionalData) throws IOException {
            int value = switch (additionalData & 3) {
                case 1 -> input.readShort();
                case 2 -> input.readByte();
                default -> input.readInt();
            };
            return additionalData > 3 ? new BsoUInt(value) : new BsoInt(value);
        }

        @Override
        public void write(DataOutput output, BsoInt tag, byte additionalData) throws IOException {
            switch (additionalData) {
                case 1:
                case 5:
                    output.writeShort(tag.shortValue());
                    break;
                case 2:
                case 6:
                    output.writeByte(tag.byteValue());
                    break;
                default:
                    output.writeInt(tag.intValue());
            }
        }
    };
    public static final BsoType<BsoLong> LONG = new BsoType<>() {
        @Override
        public byte getId() {
            return 4;
        }

        @Override
        public BsoLong read(DataInput input, byte additionalData) throws IOException {
            long value = switch (additionalData) {
                case 1 -> input.readInt();
                case 2 -> input.readShort();
                case 3 -> input.readByte();
                default -> input.readLong();
            };
            return new BsoLong(value);
        }

        @Override
        public void write(DataOutput output, BsoLong tag, byte additionalData) throws IOException {
            switch (additionalData) {
                case 1:
                    output.writeInt(tag.intValue());
                    break;
                case 2:
                    output.writeShort(tag.shortValue());
                    break;
                case 3:
                    output.writeByte(tag.byteValue());
                    break;
                default:
                    output.writeLong(tag.longValue());
            }
        }
    };
    public static final BsoType<BsoFloat> FLOAT = new BsoType<>() {
        @Override
        public byte getId() {
            return 5;
        }

        @Override
        public BsoFloat read(DataInput input, byte additionalData) throws IOException {
            return new BsoFloat(input.readFloat());
        }

        @Override
        public void write(DataOutput output, BsoFloat tag, byte additionalData) throws IOException {
            output.writeFloat(tag.floatValue());
        }
    };
    public static final BsoType<BsoDouble> DOUBLE = new BsoType<>() {
        @Override
        public byte getId() {
            return 6;
        }

        @Override
        public BsoDouble read(DataInput input, byte additionalData) throws IOException {
            return new BsoDouble(input.readDouble());
        }

        @Override
        public void write(DataOutput output, BsoDouble tag, byte additionalData) throws IOException {
            output.writeDouble(tag.doubleValue());
        }
    };
    public static final BsoType<BsoString> STRING = new BsoType<>() {
        @Override
        public byte getId() {
            return 7;
        }

        @Override
        public BsoString read(DataInput input, byte additionalData) throws IOException {
            return new BsoString(input.readUTF());
        }

        @Override
        public void write(DataOutput output, BsoString tag, byte additionalData) throws IOException {
            output.writeUTF(tag.getValue());
        }
    };
    public static final BsoType<BsoMap> MAP = new BsoType<>() {
        @Override
        public byte getId() {
            return 8;
        }

        @Override
        public BsoMap read(DataInput input, byte additionalData) throws IOException {
            LinkedHashMap<String, BsoTag> map = new LinkedHashMap<>();
            int size = switch (additionalData) {
                case 1 -> input.readShort();
                case 2 -> input.readByte();
                default -> input.readInt();
            };

            for (int i = 0; i < size; ++i) {
                byte id = input.readByte();
                String key = input.readUTF();
                BsoType<?> type = BsoTypes.byId((byte) (id & 0x0F));
                if (type != null) {
                    map.put(key, type.read(input, (byte) ((id & 0xF0) >> 4)));
                } else {
                    throw new IOException("Unknown bso type id " + id);
                }
            }
            return new BsoMap(map);
        }

        @Override
        public void write(DataOutput output, BsoMap tag, byte additionalData) throws IOException {
            switch (additionalData) {
                case 1:
                    output.writeShort((byte) (tag.size() & 0xFFFF));
                    break;
                case 2:
                    output.writeByte((byte) (tag.size() & 0xFF));
                    break;
                default:
                    output.writeInt(tag.size());
                    break;
            }

            for (Map.Entry<String, BsoTag> entry : tag.entrySet()) {
                BsoTag value = entry.getValue();
                output.writeByte(value.getIdWithAdditionalData());
                output.writeUTF(entry.getKey());
                value.write(output);
            }
        }
    };
    public static final BsoType<BsoList> LIST = new BsoType<>() {
        @Override
        public byte getId() {
            return 9;
        }

        @Override
        public BsoList read(DataInput input, byte additionalData) throws IOException {
            List<BsoTag> list = new ArrayList<>();
            int size = switch (additionalData) {
                case 1 -> input.readShort();
                case 2 -> input.readByte();
                default -> input.readInt();
            };
            for (int i = 0; i < size; ++i) {
                byte id = input.readByte();
                BsoType<?> type = BsoTypes.byId((byte) (id & 0x0F));
                if (type != null) {
                    list.add(type.read(input, (byte) ((id >> 4) & 0xF)));
                } else {
                    throw new IOException("Unknown bso type id " + id);
                }
            }
            return new BsoList(list);
        }

        @Override
        public void write(DataOutput output, BsoList tag, byte additionalData) throws IOException {
            switch (additionalData) {
                case 1:
                    output.writeShort((byte) (tag.size() & 0xFFFF));
                    break;
                case 2:
                    output.writeByte((byte) (tag.size() & 0xFF));
                    break;
                default:
                    output.writeInt(tag.size());
                    break;
            }

            for (BsoTag item : tag) {
                output.write(item.getType().getId());
                item.write(output, false);
            }
        }
    };
    public static final BsoType<BsoByteArray> BYTE_ARRAY = new BsoType<>() {
        @Override
        public byte getId() {
            return 10;
        }

        @Override
        public BsoByteArray read(DataInput input, byte additionalData) throws IOException {
            int size = switch (additionalData & 3) {
                case 1 -> input.readShort();
                case 2 -> input.readByte();
                default -> input.readInt();
            };
            byte[] values = new byte[size];
            if (additionalData > 3) {
                for (int i = 0; i < size; ++i) {
                    byte b = input.readByte();
                    values[i] = (byte) ((b >> 4) & 0xF);
                    if (i + 1 != size || size % 2 == 0) {
                        values[i + 1] = (byte) (b & 0x0F);
                    }
                    ++i;
                }
            } else {
                for (int i = 0; i < size; ++i) {
                    values[i] = input.readByte();
                }
            }
            return new BsoByteArray(values);
        }

        @Override
        public void write(DataOutput output, BsoByteArray tag, byte additionalData) throws IOException {
            switch (additionalData & 3) {
                case 1:
                    output.writeShort((byte) (tag.size() & 0xFFFF));
                    break;
                case 2:
                    output.writeByte((byte) (tag.size() & 0xFF));
                    break;
                default:
                    output.writeInt(tag.size());
                    break;
            }
            if (additionalData > 3) {
                byte[] values = tag.getValues();
                for (int i = 0, len = values.length; i < len; ++i) {
                    byte b = (byte) (values[i] << 4);
                    if (i + 1 != len || len % 2 == 0) {
                        b |= values[i + 1];
                    }
                    output.write(b);
                    ++i;
                }
            } else {
                for (byte value : tag.getValues()) {
                    output.write(value);
                }
            }
        }
    };
    public static final BsoType<BsoShortArray> SHORT_ARRAY = new BsoType<>() {
        @Override
        public byte getId() {
            return 11;
        }

        @Override
        public BsoShortArray read(DataInput input, byte additionalData) throws IOException {
            int size = switch (additionalData & 3) {
                case 1 -> input.readShort();
                case 2 -> input.readByte();
                default -> input.readInt();
            };
            short[] values = new short[size];
            for (int i = 0; i < size; ++i) {
                values[i] = additionalData > 3 ? input.readByte() : input.readShort();
            }
            return new BsoShortArray(values);
        }

        @Override
        public void write(DataOutput output, BsoShortArray tag, byte additionalData) throws IOException {
            switch (additionalData & 3) {
                case 1:
                    output.writeShort((byte) (tag.size() & 0xFFFF));
                    break;
                case 2:
                    output.writeByte((byte) (tag.size() & 0xFF));
                    break;
                default:
                    output.writeInt(tag.size());
                    break;
            }

            boolean writeAsByte = additionalData > 3;
            for (short value : tag.getValues()) {
                if (writeAsByte) output.write((byte) (value & 0xFF));
                else output.writeShort(value);
            }
        }
    };
    public static final BsoType<BsoIntArray> INT_ARRAY = new BsoType<>() {
        @Override
        public byte getId() {
            return 12;
        }

        @Override
        public BsoIntArray read(DataInput input, byte additionalData) throws IOException {
            int size = switch (additionalData & 3) {
                case 1 -> input.readShort();
                case 2 -> input.readByte();
                default -> input.readInt();
            };
            int[] values = new int[size];
            for (int i = 0; i < size; ++i) {
                values[i] = additionalData > 7 ? input.readByte() : additionalData > 3 ? input.readShort() : input.readInt();
            }
            return new BsoIntArray(values);
        }

        @Override
        public void write(DataOutput output, BsoIntArray tag, byte additionalData) throws IOException {
            switch (additionalData & 3) {
                case 1:
                    output.writeShort((byte) (tag.size() & 0xFFFF));
                    break;
                case 2:
                    output.writeByte((byte) (tag.size() & 0xFF));
                    break;
                default:
                    output.writeInt(tag.size());
                    break;
            }

            for (int value : tag.getValues()) {
                if (additionalData > 7) output.write((byte) (value & 0xFF));
                else if (additionalData > 3) output.writeShort((byte) (value & 0xFFFF));
                else output.writeShort(value);
            }
        }
    };
    public static final BsoType<BsoFloatArray> FLOAT_ARRAY = new BsoType<>() {
        @Override
        public byte getId() {
            return 14;
        }

        @Override
        public BsoFloatArray read(DataInput input, byte additionalData) throws IOException {
            int size = switch (additionalData) {
                case 1 -> input.readShort();
                case 2 -> input.readByte();
                default -> input.readInt();
            };
            float[] values = new float[size];
            for (int i = 0; i < size; ++i) {
                values[i] = input.readFloat();
            }
            return new BsoFloatArray(values);
        }

        @Override
        public void write(DataOutput output, BsoFloatArray tag, byte additionalData) throws IOException {
            switch (additionalData) {
                case 1:
                    output.writeShort((byte) (tag.size() & 0xFFFF));
                    break;
                case 2:
                    output.writeByte((byte) (tag.size() & 0xFF));
                    break;
                default:
                    output.writeInt(tag.size());
                    break;
            }

            for (float value : tag.getValues()) {
                output.writeFloat(value);
            }
        }
    };
    public static final BsoType<BsoDoubleArray> DOUBLE_ARRAY = new BsoType<>() {
        @Override
        public byte getId() {
            return 14;
        }

        @Override
        public BsoDoubleArray read(DataInput input, byte additionalData) throws IOException {
            int size = switch (additionalData) {
                case 1 -> input.readShort();
                case 2 -> input.readByte();
                default -> input.readInt();
            };
            double[] values = new double[size];
            for (int i = 0; i < size; ++i) {
                values[i] = input.readDouble();
            }
            return new BsoDoubleArray(values);
        }

        @Override
        public void write(DataOutput output, BsoDoubleArray tag, byte additionalData) throws IOException {
            switch (additionalData) {
                case 1:
                    output.writeShort((byte) (tag.size() & 0xFFFF));
                    break;
                case 2:
                    output.writeByte((byte) (tag.size() & 0xFF));
                    break;
                default:
                    output.writeInt(tag.size());
                    break;
            }

            for (double value : tag.getValues()) {
                output.writeDouble(value);
            }
        }
    };

    private static final BsoType<?>[] VALUES = { BsoTypes.BYTE, BsoTypes.SHORT, BsoTypes.INT, BsoTypes.LONG, BsoTypes.FLOAT, BsoTypes.DOUBLE, BsoTypes.STRING, BsoTypes.MAP, BsoTypes.LIST, BsoTypes.BYTE_ARRAY,BsoTypes.SHORT_ARRAY, BsoTypes.INT_ARRAY, BsoTypes.FLOAT_ARRAY, BsoTypes.DOUBLE };

    private BsoTypes() {
    }

    public static BsoType<?> byId(byte id) {
        for (BsoType<?> type : BsoTypes.VALUES) {
            if (type.getId() == id) {
                return type;
            }
        }
        return null;
    }
}
