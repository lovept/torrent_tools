package com.github.lovept.utils;

import com.github.lovept.options.CreateCommandOptions;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author lovept
 * @date 2024/5/26 17:17
 * @description 种子生成工具
 */
public class TorrentUtils {
    private static File inputFile;
    private static String outputPath;
    private static String announceUrl;
    private static String comment;
    private static String torrentName;
    private static Integer pieceLength;
    private static boolean privateFlag;
    private static String source;
    private static String createdBy;
    private static String publisher;
    private static Long creationDate;
    private static boolean noCreatedBy;
    private static boolean noCreationDate;
    private static boolean noPublisher;
    private static boolean noSource;
    private static final List<String> ANNOUNCE_LIST = new ArrayList<>();

    private static void init(CreateCommandOptions options) {
        inputFile = options.getInputFile();
        outputPath = inputFile.getParent();
        if (options.getOutputPath() != null) {
            String output = options.getOutputPath();
            if (output.endsWith(".torrent")) {
                File outputFile = new File(output);
                outputPath = outputFile.getParent();
                torrentName = outputFile.getName().substring(0, outputFile.getName().lastIndexOf(".torrent"));
            } else {
                outputPath = options.getOutputPath();
            }
        }

        String announce = options.getAnnounceUrl();
        if (announce.contains(",")) {
            String[] announceList = announce.split(",");
            announceUrl = announceList[0];
            Collections.addAll(ANNOUNCE_LIST, announceList);
        } else {
            announceUrl = announce;
            ANNOUNCE_LIST.add(announceUrl);
        }
        comment = options.getComment();
        String inputName = options.getTorrentName();
        if (inputName != null && inputName.endsWith(".torrent")) {
            torrentName = inputName.substring(0, inputName.lastIndexOf(".torrent"));
        } else {
            torrentName = Objects.requireNonNullElseGet(inputName, () -> inputFile.getName());
        }
        privateFlag = options.isPrivateFlag();
        pieceLength = options.getPieceLength();
        source = options.getSource();
        createdBy = options.getCreatedBy();
        publisher = options.getPublisher();
        String inputDate = options.getCreationDate();
        if (inputDate != null) {
            LocalDateTime localDateTime = LocalDateTime.parse(inputDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            creationDate = localDateTime.toEpochSecond(ZoneOffset.ofHours(8));
        } else {
            creationDate = Instant.now().getEpochSecond();
        }
        noCreatedBy = options.isNoCreatedBy();
        noCreationDate = options.isNoCreationDate();
        noPublisher = options.isNoPublisher();
        noSource = options.isNoSource();
    }

    public static void create(CreateCommandOptions options) {
        init(options);
        TreePrinter.printer(inputFile.getAbsolutePath(), true);
        try {
            handleInput();
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void handleInput() throws IOException, NoSuchAlgorithmException {
        if (inputFile.isDirectory()) {
            createTorrentForDirectory();
        } else {
            createTorrentForFile();
        }
    }

    private static void createTorrent(Map<String, Object> info) throws IOException {
        Map<String, Object> metaInfo = new HashMap<>();
        metaInfo.put("announce", announceUrl);
        if (!noCreatedBy) {
            metaInfo.put("created by", createdBy);
        }
        if (!noPublisher) {
            metaInfo.put("publisher", publisher);
        }
        if (!noCreationDate) {
            metaInfo.put("creation date", creationDate);
        }
        metaInfo.put("comment", comment);
        metaInfo.put("announce-list", ANNOUNCE_LIST);
        metaInfo.put("info", info);

        File outputFile = new File(outputPath, torrentName + ".torrent");
        OutputStream out = new FileOutputStream(outputFile);
        encodeMap(metaInfo, out);
        out.close();
    }

    private static void createTorrentForFile() throws IOException, NoSuchAlgorithmException {
        long totalSize = inputFile.length();
        if (pieceLength == null) {
            pieceLength = calculateOptimalPieceLength(totalSize);
        }
        byte[] piecesHash = hashPieces(inputFile, pieceLength);
        Map<String, Object> info = createInfoMap(pieceLength, piecesHash, privateFlag, source);
        info.put("name", torrentName);
        info.put("length", totalSize);
        createTorrent(info);
    }

    private static Map<String, Object> createInfoMap(int pieceLength, byte[] piecesHash, boolean privateFlag, String source) {
        Map<String, Object> info = new HashMap<>();
        info.put("piece length", pieceLength);
        info.put("pieces", piecesHash);
        if (privateFlag) {
            info.put("private", 1);
        }
        if (!noSource) {
            info.put("source", source);
        }
        return info;
    }

    private static void createTorrentForDirectory() throws IOException, NoSuchAlgorithmException {
        List<File> files = listFiles(inputFile);
        long totalSize = getTotalSize(files);
        if (files.isEmpty() && totalSize == 0) {
            return;
        }
        if (pieceLength == null) {
            // 动态计算pieceLength
            pieceLength = calculateOptimalPieceLength(totalSize);
        }
        List<Map<String, Object>> fileInfoList = new ArrayList<>();

        for (File file : files) {
            Map<String, Object> fileInfo = new HashMap<>();
            fileInfo.put("length", file.length());

            String split = "";
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                split = "\\\\";
            } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
                split = "/";
            }

            fileInfo.put("path", Arrays.asList(inputFile.toPath().relativize(file.toPath()).toString().split(split)));
            fileInfoList.add(fileInfo);
        }
        byte[] piecesHash = hashPieces(files, pieceLength);
        Map<String, Object> info = createInfoMap(pieceLength, piecesHash, privateFlag, source);
        info.put("name", torrentName);
        info.put("files", fileInfoList);
        createTorrent(info);
    }


    private static byte[] hashPieces(File file, int pieceLength) throws IOException, NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        FileInputStream in = new FileInputStream(file);
        ByteArrayOutputStream pieces = new ByteArrayOutputStream();

        byte[] buffer = new byte[pieceLength];
        int read;
        while ((read = in.read(buffer)) != -1) {
            sha1.update(buffer, 0, read);
            if (read == pieceLength) {
                pieces.write(sha1.digest());
                // Reset the digest for the next piece
                sha1.reset();
            }
        }

        // Handle the last piece
        if (buffer.length > 0) {
            pieces.write(sha1.digest());
        }
        in.close();

        return pieces.toByteArray();
    }


    private static long getTotalSize(List<File> files) {
        long totalSize = 0;
        for (File file : files) {
            totalSize += file.length();
        }
        return totalSize;
    }


    private static byte[] hashPieces(List<File> files, int pieceLength) throws IOException, NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        ByteArrayOutputStream piecesHashes = new ByteArrayOutputStream();
        byte[] buffer = new byte[pieceLength];
        int bytesRead;
        int currentLength = 0;

        for (File file : files) {
            InputStream in = new FileInputStream(file);
            while ((bytesRead = in.read(buffer, currentLength, pieceLength - currentLength)) != -1) {
                currentLength += bytesRead;
                if (currentLength == pieceLength) {
                    sha1.update(buffer, 0, pieceLength);
                    piecesHashes.write(sha1.digest());
                    // 重置SHA-1 MessageDigest
                    sha1.reset();
                    currentLength = 0;
                }
            }
            in.close();
        }

        // 处理最后一个片段（如果存在）并且其大小小于pieceLength
        if (currentLength > 0) {
            sha1.update(buffer, 0, currentLength);
            piecesHashes.write(sha1.digest());
        }

        return piecesHashes.toByteArray();
    }

    private static List<File> listFiles(File directory) {
        List<File> fileList = new ArrayList<>();
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    fileList.add(file);
                } else if (file.isDirectory()) {
                    fileList.addAll(listFiles(file));
                }
            }
        }
        return fileList;
    }

    private static int calculateOptimalPieceLength(long totalFileSize) {
        final int MIN_PIECES = 512;
        final int MAX_PIECES = 1024;
        // 32 KB
        final int MIN_PIECE_LENGTH = 32 * 1024;
        // 16 MB
        final int MAX_PIECE_LENGTH = 16 * 1024 * 1024;

        // Step 1: Initial guess based on desired number of pieces
        long pieceLength = totalFileSize / MIN_PIECES;
        pieceLength = clamp(nearestPowerOfTwo(pieceLength), MIN_PIECE_LENGTH, MAX_PIECE_LENGTH);

        // Step 2: Adjust pieceLength if number of pieces is out of range
        int numPieces = (int) (totalFileSize / pieceLength);

        while (numPieces < MIN_PIECES && pieceLength > MIN_PIECE_LENGTH) {
            pieceLength /= 2;
            numPieces = (int) (totalFileSize / pieceLength);
        }

        while (numPieces > MAX_PIECES && pieceLength < MAX_PIECE_LENGTH) {
            pieceLength *= 2;
            numPieces = (int) (totalFileSize / pieceLength);
        }

        return (int) pieceLength;
    }

    private static int nearestPowerOfTwo(long value) {
        if (value < 1) {
            return 1;
        }
        return (int) Math.pow(2, Math.ceil(Math.log(value) / Math.log(2)));
    }

    private static long clamp(long value, long min, long max) {
        return Math.max(min, Math.min(value, max));
    }


    private static void encodeObject(Object o, OutputStream out) throws IOException {
        if (o instanceof String) {
            encodeString((String) o, out);
        } else if (o instanceof Map<?, ?>) {
            encodeMap(castToMapStringObject(o), out);
        } else if (o instanceof List<?>) {
            encodeList(castToListObject(o), out);
        } else if (o instanceof byte[]) {
            encodeBytes((byte[]) o, out);
        } else if (o instanceof Number) {
            encodeLong(((Number) o).longValue(), out);
        } else {
            throw new IllegalArgumentException("Unsupported object type for encoding: " + o.getClass().getName());
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> castToMapStringObject(Object obj) {
        if (obj instanceof Map<?, ?> map) {
            if (map.isEmpty()) {
                return new HashMap<>();
            } else {
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    if (!(entry.getKey() instanceof String) || entry.getValue() == null) {
                        throw new IllegalArgumentException("All keys must be Strings and values must be Objects in the map.");
                    }
                }
                return (Map<String, Object>) map;
            }
        } else {
            throw new IllegalArgumentException("Object is not a Map: " + obj.getClass().getName());
        }
    }

    @SuppressWarnings("unchecked")
    private static List<Object> castToListObject(Object obj) {
        if (obj instanceof List<?> list) {
            for (Object item : list) {
                if (item == null) {
                    throw new IllegalArgumentException("List elements must be Objects");
                }
            }
            return (List<Object>) list;
        } else {
            throw new IllegalArgumentException("Object is not a List: " + obj.getClass().getName());
        }
    }

    private static void encodeLong(long value, OutputStream out) throws IOException {
        out.write('i');
        out.write(Long.toString(value).getBytes(StandardCharsets.US_ASCII));
        out.write('e');
    }

    private static void encodeBytes(byte[] bytes, OutputStream out) throws IOException {
        out.write(Integer.toString(bytes.length).getBytes(StandardCharsets.US_ASCII));
        out.write(':');
        out.write(bytes);
    }

    private static void encodeString(String str, OutputStream out) throws IOException {
        encodeBytes(str.getBytes(StandardCharsets.UTF_8), out);
    }

    private static void encodeList(List<Object> list, OutputStream out) throws IOException {
        out.write('l');
        for (Object elem : list) {
            encodeObject(elem, out);
        }
        out.write('e');
    }

    private static void encodeMap(Map<String, Object> map, OutputStream out) throws IOException {
        SortedMap<String, Object> sortedMap = new TreeMap<>(map);
        out.write('d');
        for (Map.Entry<String, Object> e : sortedMap.entrySet()) {
            encodeString(e.getKey(), out);
            encodeObject(e.getValue(), out);
        }
        out.write('e');
    }
}


