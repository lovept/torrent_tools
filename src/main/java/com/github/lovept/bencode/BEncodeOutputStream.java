package com.github.lovept.bencode;

import lombok.Getter;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author lovept
 * @date 2024/5/26 17:17
 * @description bEncoded data OutputStream
 */
@Getter
public class BEncodeOutputStream extends FilterOutputStream {

    private final Charset charset;

    public BEncodeOutputStream(final OutputStream out, final Charset charset) {
        super(out);

        if (charset == null) {
            throw new NullPointerException("charset cannot be null");
        }
        this.charset = charset;
    }


    public BEncodeOutputStream(final OutputStream out) {
        this(out, BEncode.DEFAULT_CHARSET);
    }

    public void writeString(final String s) throws IOException {
        write(encode(s));
    }


    public void writeString(final ByteBuffer buff) throws IOException {
        write(encode(buff.array()));
    }


    public void writeString(final byte[] array) throws IOException {
        write(encode(array));
    }


    public void writeNumber(final Number n) throws IOException {
        write(encode(n));
    }


    public void writeList(final Iterable<?> l) throws IOException {
        write(encode(l));
    }


    public void writeDictionary(final Map<?, ?> m) throws IOException {
        write(encode(m));
    }

    private byte[] encode(final String s) throws IOException {
        if (s == null) {
            throw new NullPointerException("s cannot be null");
        }

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] bytes = s.getBytes(charset);
        buffer.write(Integer.toString(bytes.length).getBytes(charset));
        buffer.write(BEncode.SEPARATOR);
        buffer.write(bytes);

        return buffer.toByteArray();
    }

    private byte[] encode(final byte[] b) throws IOException {
        if (b == null) {
            throw new NullPointerException("b cannot be null");
        }

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        buffer.write(Integer.toString(b.length).getBytes(charset));
        buffer.write(BEncode.SEPARATOR);
        buffer.write(b);

        return buffer.toByteArray();
    }

    private byte[] encode(final Number n) throws IOException {
        if (n == null) {
            throw new NullPointerException("n cannot be null");
        }

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        buffer.write(BEncode.NUMBER);
        buffer.write(Long.toString(n.longValue()).getBytes(charset));
        buffer.write(BEncode.END);

        return buffer.toByteArray();
    }

    private byte[] encode(final Iterable<?> l) throws IOException {
        if (l == null) {
            throw new NullPointerException("l cannot be null");
        }

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        buffer.write(BEncode.LIST);
        for (Object o : l) {
            buffer.write(encodeObject(o));
        }
        buffer.write(BEncode.END);

        return buffer.toByteArray();
    }

    private byte[] encode(final Map<?, ?> m) throws IOException {
        if (m == null) {
            throw new NullPointerException("m cannot be null");
        }

        Map<?, ?> map;
        if (!(m instanceof SortedMap<?, ?>)) {
            map = new TreeMap<>(m);
        } else {
            map = m;
        }

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        buffer.write(BEncode.DICTIONARY);
        for (Map.Entry<?, ?> e : map.entrySet()) {
            buffer.write(encode(e.getKey().toString()));
            buffer.write(encodeObject(e.getValue()));
        }
        buffer.write(BEncode.END);

        return buffer.toByteArray();
    }

    private byte[] encodeObject(final Object o) throws IOException {
        if (o == null) {
            throw new NullPointerException("Cannot write null objects");
        }

        if (o instanceof Number) {
            return encode((Number) o);
        }
        if (o instanceof Iterable<?>) {
            return encode((Iterable<?>) o);
        }
        if (o instanceof Map<?, ?>) {
            return encode((Map<?, ?>) o);
        }
        if (o instanceof ByteBuffer) {
            return encode(((ByteBuffer) o).array());
        }
        if (o instanceof byte[]) {
            return encode((byte[]) o);
        }

        return encode(o.toString());
    }
}
