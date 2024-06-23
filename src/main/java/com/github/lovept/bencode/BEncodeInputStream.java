package com.github.lovept.bencode;

import lombok.Getter;

import java.io.*;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static com.github.lovept.bencode.BType.*;

/**
 * @author lovept
 * @date 2024/5/26 17:17
 * @description bEncoded data InputStream
 */
@Getter
public class BEncodeInputStream extends FilterInputStream {

    // EOF Constant
    private static final int EOF = -1;


    private final Charset charset;
    private final boolean useBytes;
    private final PushbackInputStream pis;


    public BEncodeInputStream(final InputStream pis, final Charset charset, boolean useBytes) {
        super(new PushbackInputStream(pis));
        this.pis = (PushbackInputStream) super.in;

        if (charset == null) {
            throw new NullPointerException("charset cannot be null");
        }
        this.charset = charset;
        this.useBytes = useBytes;
    }


    public BEncodeInputStream(final InputStream pis, final Charset charset) {
        this(pis, charset, false);
    }


    public BEncodeInputStream(final InputStream pis) {
        this(pis, BEncode.DEFAULT_CHARSET);
    }


    private int peek() throws IOException {
        int b = pis.read();
        pis.unread(b);
        return b;
    }


    public BType nextType() throws IOException {
        int token = peek();
        checkEof(token);

        return typeForToken(token);
    }

    private BType typeForToken(int token) {
        for (BType BType : BType.values()) {
            if (BType.validate(token)) {
                return BType;
            }
        }

        return BType.UNKNOWN;
    }


    public String readString() throws IOException {
        return new String(readStringBytesInternal(), getCharset());
    }


    public ByteBuffer readStringBytes() throws IOException {
        return ByteBuffer.wrap(readStringBytesInternal());
    }

    private byte[] readStringBytesInternal() throws IOException {
        int token = pis.read();
        validateToken(token, STRING);

        StringBuilder buffer = new StringBuilder();
        buffer.append((char) token);
        while ((token = pis.read()) != BEncode.SEPARATOR) {
            validateToken(token, STRING);

            buffer.append((char) token);
        }

        int length = Integer.parseInt(buffer.toString());
        byte[] bytes = new byte[length];
        int read = super.read(bytes);
        return bytes;
    }


    public Long readNumber() throws IOException {
        int token = pis.read();
        validateToken(token, NUMBER);

        StringBuilder buffer = new StringBuilder();
        while ((token = pis.read()) != BEncode.END) {
            checkEof(token);

            buffer.append((char) token);
        }

        return new BigDecimal(buffer.toString()).longValue();
    }


    public List<Object> readList() throws IOException {
        int token = pis.read();
        validateToken(token, LIST);

        List<Object> list = new ArrayList<>();
        while ((token = pis.read()) != BEncode.END) {
            checkEof(token);

            list.add(readObject(token));
        }

        return list;
    }


    public Map<String, Object> readDictionary() throws IOException {
        int token = pis.read();
        validateToken(token, BType.DICTIONARY);

        Map<String, Object> map = new LinkedHashMap<>();
        while ((token = pis.read()) != BEncode.END) {
            checkEof(token);

            pis.unread(token);
            map.put(readString(), readObject(pis.read()));
        }

        return map;
    }

    private Object readObject(final int token) throws IOException {
        pis.unread(token);

        BType BType = typeForToken(token);

        if (BType == STRING && !useBytes) {
            return readString();
        }
        if (BType == STRING) {
            return readStringBytes();
        }
        if (BType == BType.NUMBER) {
            return readNumber();
        }
        if (BType == LIST) {
            return readList();
        }
        if (BType == BType.DICTIONARY) {
            return readDictionary();
        }

        throw new InvalidObjectException("Unexpected token '" + new String(Character.toChars(token)) + "'");
    }


    private void validateToken(final int token, final BType BType) throws IOException {
        checkEof(token);

        if (!BType.validate(token)) {
            pis.unread(token);
            throw new InvalidObjectException("Unexpected token '" + new String(Character.toChars(token)) + "'");
        }
    }

    private void checkEof(final int b) throws EOFException {
        if (b == EOF) {
            throw new EOFException();
        }
    }
}
