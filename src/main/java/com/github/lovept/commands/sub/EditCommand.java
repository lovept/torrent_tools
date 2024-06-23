package com.github.lovept.commands.sub;

import com.github.lovept.bencode.BEncode;
import com.github.lovept.bencode.BType;
import com.github.lovept.options.EditCommandOptions;
import lombok.SneakyThrows;
import picocli.CommandLine;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author lovopt :)
 * @date 2024/6/12 15:55
 * @description 编辑种子界面
 */
@CommandLine.Command(name = "edit",
        mixinStandardHelpOptions = true,
        version = "0.0.1",
        header = "Edit the torrent file.",
        optionListHeading = "%nOptions: %n")
public class EditCommand implements Runnable {

    @CommandLine.Mixin
    private EditCommandOptions options;

    private Map<String, Object> mediaInfoMap;

    private BEncode bEncode;

    @Override
    public void run() {
        File inputFile = options.getInputFile();
        if (!inputFile.exists()) {
            throw new RuntimeException("the file not exists.");
        }

        decode(inputFile);
        processOptions();
        save();
    }

    private void processOptions() {
        processAnnounceOption();
        processCommentOption();
        processPublisherOption();
        processCreateByOption();
        processCreationDateOption();
        processTorrentNameOption();
        processSourceOption();
        processNoCreatedByOption();
        processNoCreationDateOption();
        processNoPublisherOption();
        processNoSourceOption();
        processNoAnnounceOption();
        processPrivateFlagOption();
    }

    private void processPrivateFlagOption() {
        String privateFlag = options.getPrivateFlag();
        if ("yes".equalsIgnoreCase(privateFlag)) {
            check();
            @SuppressWarnings("unchecked")
            Map<String, Object> infoMap = (Map<String, Object>) mediaInfoMap.get("info");
            infoMap.put("private", 1);
        } else if ("no".equalsIgnoreCase(privateFlag)) {
            check();
            @SuppressWarnings("unchecked")
            Map<String, Object> infoMap = (Map<String, Object>) mediaInfoMap.get("info");
            infoMap.remove("private");
        }
    }

    private void processNoAnnounceOption() {
        boolean noAnnounce = options.isNoAnnounce();
        if (noAnnounce) {
            check();
            mediaInfoMap.remove("announce");
            mediaInfoMap.remove("announce-list");
        }
    }

    private void processNoSourceOption() {
        boolean noSource = options.isNoSource();
        if (noSource) {
            check();
            @SuppressWarnings("unchecked")
            Map<String, Object> infoMap = (Map<String, Object>) mediaInfoMap.get("info");
            infoMap.remove("source");
        }
    }

    private void processNoPublisherOption() {
        boolean noPublisher = options.isNoPublisher();
        if (noPublisher) {
            check();
            mediaInfoMap.remove("publisher");
        }
    }

    private void processNoCreationDateOption() {
        boolean noCreationDate = options.isNoCreationDate();
        if (noCreationDate) {
            check();
            mediaInfoMap.remove("creation date");
        }
    }

    private void processNoCreatedByOption() {
        boolean noCreatedBy = options.isNoCreatedBy();
        if (noCreatedBy) {
            check();
            mediaInfoMap.remove("created by");
        }
    }

    private void processSourceOption() {
        String source = options.getSource();
        if (source != null) {
            check();
            @SuppressWarnings("unchecked")
            Map<String, Object> infoMap = (Map<String, Object>) mediaInfoMap.get("info");
            infoMap.put("source", source);
        }
    }

    private void processTorrentNameOption() {
        String torrentName = options.getTorrentName();
        if (torrentName != null) {
            check();
            @SuppressWarnings("unchecked")
            Map<String, Object> infoMap = (Map<String, Object>) mediaInfoMap.get("info");
            infoMap.put("name", torrentName);
        }
    }

    private void processCreationDateOption() {
        String creationDate = options.getCreationDate();
        if (creationDate != null) {
            check();
            LocalDateTime localDateTime = LocalDateTime.parse(creationDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            mediaInfoMap.put("creation date", localDateTime.toEpochSecond(ZoneOffset.ofHours(8)));
        }

    }

    private void processCreateByOption() {
        String createBy = options.getCreateBy();
        if (createBy != null) {
            check();
            mediaInfoMap.put("created by", createBy);
        }
    }

    private void processPublisherOption() {
        String publisher = options.getPublisher();
        if (publisher != null) {
            check();
            mediaInfoMap.put("publisher", publisher);
        }
    }

    private void processAnnounceOption() {
        String announce = options.getAnnounceUrl();
        if (announce != null) {
            check();

            List<String> announceUrlList = new ArrayList<>();
            if (announce.contains(",")) {
                String[] announceList = announce.split(",");
                announce = announceList[0];
                Collections.addAll(announceUrlList, announceList);
            } else {
                announceUrlList.add(announce);
            }
            mediaInfoMap.put("announce", announce);

            if (mediaInfoMap.get("announce-list") != null) {
                mediaInfoMap.put("announce-list", announceUrlList);
            }
        }
    }


    private void processCommentOption() {
        String comment = options.getComment();
        if (comment != null) {
            check();
            mediaInfoMap.put("comment", comment);
        }
    }


    private void save() {
        File inputFile = options.getInputFile();
        String outputPath = options.getOutputPath();
        File outputFile;
        if (outputPath != null) {
            if (!outputPath.endsWith(".torrent")) {
                outputFile = new File(outputPath + File.separator + inputFile.getName() + ".torrent");
            } else {
                outputFile = new File(outputPath);
            }
        } else {
            outputFile = options.getInputFile();
        }
        bEncode.save(outputFile, mediaInfoMap);
    }

    private void check() {
        if (mediaInfoMap == null) {
            decode(options.getInputFile());
        }
    }

    @SneakyThrows
    private void decode(File file) {
        bEncode = new BEncode();
        byte[] bytes = Files.readAllBytes(Path.of(file.getAbsolutePath()));
        mediaInfoMap = bEncode.decode(bytes, BType.DICTIONARY);
    }
}
