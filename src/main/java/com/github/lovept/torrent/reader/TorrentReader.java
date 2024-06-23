package com.github.lovept.torrent.reader;


import com.github.lovept.torrent.generator.MagnetUriGenerator;
import com.github.lovept.torrent.entity.Torrent;
import com.github.lovept.torrent.struct.TorrentStruct;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lovept :)
 * @date 2024/6/13 16:47
 * @description 种子读取器
 */
public class TorrentReader {
    private Torrent torrent;
    private TorrentStruct struct;

    public static Torrent build(String absolutionPath) throws IOException {
        TorrentReader reader = new TorrentReader();
        reader.initialize(absolutionPath);
        reader.read();
        MagnetUriGenerator.generator(reader.torrent);
        return reader.torrent;
    }

    private void initialize(String absolutionPath) throws IOException {
        if (absolutionPath == null || absolutionPath.isEmpty()) {
            throw new IllegalArgumentException("文件路径不能为空");
        }

        Path torrentPath = Path.of(absolutionPath);
        if (!Files.exists(torrentPath)) {
            throw new IOException("文件不存在: " + absolutionPath);
        }

        File torrentFile = torrentPath.toFile();
        torrent = new Torrent(torrentFile.getParent(), torrentFile.getName());
        struct = new TorrentStruct(Files.readAllBytes(torrentPath), 0);
    }

    private void read() {
        torrent.setMetaInfo(readMap());
        torrent.setInfoHash(readHash());
    }

    private String readHash() {
        int length = struct.getInfoEnd() - struct.getInfoStart();
        byte[] infoByte = new byte[length];
        System.arraycopy(struct.getInfo(), struct.getInfoStart(), infoByte, 0, length);

        MessageDigest sha1;
        try {
            sha1 = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-1算法不可用", e);
        }

        byte[] hashBytes = sha1.digest(infoByte);
        StringBuilder hexBuilder = new StringBuilder(hashBytes.length * 2);
        for (byte b : hashBytes) {
            hexBuilder.append(String.format("%02x", b));
        }

        return hexBuilder.toString();
    }


    private Map<String, Object> readMap() {
        struct.advanceIndex(1);
        Map<String, Object> metaInfoMap = new HashMap<>();
        String key = null;

        while (true) {
            char symbol = struct.getChar();
            if (symbol == 'e') {
                break;
            }

            if (Character.isDigit(symbol)) {
                String data = readString();
                if (key == null) {
                    key = data;
                } else {
                    metaInfoMap.put(key, data);
                    key = null;
                }
                continue;
            }

            if (key == null) {
                throw new IllegalArgumentException("找不到关联的键");
            }

            switch (symbol) {
                case 'l' -> metaInfoMap.put(key, readList());
                case 'd' -> {
                    boolean isInfo = "info".equals(key);
                    if (isInfo) {
                        struct.setInfoStart(struct.getIndex());
                    }
                    metaInfoMap.put(key, readMap());
                    if (isInfo) {
                        struct.setInfoEnd(struct.getIndex());
                    }
                }
                case 'i' -> metaInfoMap.put(key, readInt());
                default -> throw new IllegalArgumentException("读取到不符合规范的标识符：" + symbol);
            }

            key = null;
        }

        struct.advanceIndex(1);
        return metaInfoMap;
    }

    private String readString() {
        StringBuilder builder = new StringBuilder();
        byte[] messages = struct.getInfo();
        int index = struct.getIndex();

        while (messages[index] != ':') {
            builder.append((char) messages[index]);
            index++;
        }

        int length = Integer.parseInt(builder.toString());
        if (length < 0) {
            throw new IllegalArgumentException("读取到不符合规范的字符串长度：" + length);
        }

        index++;
        String data = new String(messages, index, length);
        struct.setIndex(index + length);
        return data;
    }

    private long readInt() {
        StringBuilder builder = new StringBuilder();
        byte[] messages = struct.getInfo();
        int offset = struct.getIndex() + 1;

        while (messages[offset] != 'e') {
            builder.append((char) messages[offset++]);
        }

        struct.setIndex(offset + 1);
        return Long.parseLong(builder.toString());
    }

    private List<Object> readList() {
        struct.advanceIndex(1);
        List<Object> list = new ArrayList<>();

        while (true) {
            char symbol = struct.getChar();
            if (symbol == 'e') {
                break;
            }

            switch (symbol) {
                case 'l' -> list.add(readList());
                case 'd' -> list.add(readMap());
                case 'i' -> list.add(readInt());
                default -> {
                    if (Character.isDigit(symbol)) {
                        list.add(readString());
                    } else {
                        throw new IllegalArgumentException("读取到不符合规范的标识符：" + symbol);
                    }
                }
            }
        }

        struct.advanceIndex(1);
        return list;
    }
}



