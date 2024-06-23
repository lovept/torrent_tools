package com.github.lovept.bencode;

import cn.hutool.core.io.FileUtil;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;

import static com.github.lovept.bencode.BType.*;

/**
 * @author lovept
 * @date 2024/5/26 17:17
 * @description torrent文件编码解码
 */
@Getter
public final class BEncode {

    static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    static final char NUMBER = 'i';

    static final char LIST = 'l';

    static final char DICTIONARY = 'd';

    static final char END = 'e';

    static final char SEPARATOR = ':';

    private Charset charset;
    private final boolean useBytes;

    public BEncode() {
        this(DEFAULT_CHARSET);
    }


    public BEncode(final Charset charset) {
        this(charset, false);
    }


    public BEncode(final boolean useBytes) {
        this(DEFAULT_CHARSET, useBytes);
    }


    public BEncode(final Charset charset, final boolean useBytes) {
        if (charset == null) {
            throw new NullPointerException("charset cannot be null");
        }

        this.charset = charset;
        this.useBytes = useBytes;
    }

    public BType type(final byte[] bytes) {
        if (bytes == null) {
            throw new NullPointerException("bytes cannot be null");
        }

        BEncodeInputStream ins = new BEncodeInputStream(new ByteArrayInputStream(bytes), charset, useBytes);

        try {
            return ins.nextType();
        } catch (Throwable t) {
            throw new RuntimeException("Exception thrown during type detection", t);
        }
    }


    @SuppressWarnings("unchecked")
    public <T> T decode(final byte[] bytes, final BType BType) {
        if (bytes == null) {
            throw new NullPointerException("bytes cannot be null");
        }
        if (BType == null) {
            throw new NullPointerException("BType cannot be null");
        }
        if (BType.equals(BType.UNKNOWN)) {
            throw new IllegalArgumentException("BType cannot be UNKNOWN");
        }

        BEncodeInputStream bis = new BEncodeInputStream(new ByteArrayInputStream(bytes), charset, useBytes);

        try {
            if (BType.equals(BType.NUMBER)) {
                return (T) bis.readNumber();
            }
            if (BType.equals(BType.LIST)) {
                return (T) bis.readList();
            }
            if (BType.equals(BType.DICTIONARY)) {
                return (T) bis.readDictionary();
            }
            return (T) bis.readString();
        } catch (Throwable t) {
            throw new RuntimeException("Exception thrown during decoding", t);
        }
    }

    public byte[] encode(final String s) {
        if (s == null) {
            throw new NullPointerException("s cannot be null");
        }

        return encode(s, STRING);
    }


    public byte[] encode(final Number n) {
        if (n == null) {
            throw new NullPointerException("n cannot be null");
        }

        return encode(n, BType.NUMBER);
    }


    public byte[] encode(final Iterable<?> l) {
        if (l == null) {
            throw new NullPointerException("l cannot be null");
        }

        return encode(l, BType.LIST);
    }


    public byte[] encode(final Map<?, ?> m) {
        if (m == null) {
            throw new NullPointerException("m cannot be null");
        }

        return encode(m, BType.DICTIONARY);
    }

    private byte[] encode(final Object o, final BType bType) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BEncodeOutputStream bos = new BEncodeOutputStream(out, charset);

        try {
            if (STRING.equals(bType)) {
                bos.writeString((String) o);
            } else if (BType.NUMBER.equals(bType)) {
                bos.writeNumber((Number) o);
            } else if (BType.LIST.equals(bType)) {
                bos.writeList((Iterable<?>) o);
            } else if (BType.DICTIONARY.equals(bType)) {
                bos.writeDictionary((Map<?, ?>) o);
            }
        } catch (Throwable t) {
            throw new RuntimeException("Exception thrown during encoding", t);
        }

        return out.toByteArray();
    }

    public void save(File torrentFile, Map<String, Object> dict) {
        FileUtil.mkParentDirs(torrentFile);

        if (FileUtil.exist(torrentFile)) {
            FileUtil.del(torrentFile);
        }
        FileUtil.copyFile(new ByteArrayInputStream(encode(dict)), torrentFile.toPath());
    }

    public String sha1(byte[] torrentData) {
        if (!StandardCharsets.ISO_8859_1.equals(charset)) {
            charset = StandardCharsets.ISO_8859_1;
        }
        Map<String, Object> infoMap = decode(torrentData, BType.DICTIONARY);

        return sha1((Map<?, ?>) infoMap.get("info"));
    }

    @SneakyThrows
    private String sha1(Map<?, ?> infoMap) {

        byte[] infoEncode = encode(infoMap);
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(infoEncode);
        StringBuilder sb = new StringBuilder();
        for (byte b : result) {
            sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

}
