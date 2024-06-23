package com.github.lovept.options;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine;

import java.io.File;

/**
 * @author LovePT
 * @date 2024/5/31 14:44
 * @description 创建种子命令选项
 */
@Getter
@Setter
public class CreateCommandOptions {
    @CommandLine.Parameters(index = "0", description = "The file to create a torrent for.")
    private File inputFile;

    @CommandLine.Option(names = {"-o", "--output"}, description = "Set the filename and/or output directory of the created file. [default: <name>.torrent]")
    private String outputPath;

    @CommandLine.Option(names = {"-a", "--announce"}, defaultValue = "https://example.com", description = "Add one or multiple announces urls.")
    private String announceUrl;

    @CommandLine.Option(names = {"-c", "--comment"}, defaultValue = "TorrentTools", description = "Add a comment.")
    private String comment;

    @CommandLine.Option(names = {"-n", "--name"}, description = "Set the name of the torrent. This changes the filename for single file torrents.")
    private String torrentName;

    @CommandLine.Option(names = {"-l", "--piece-size"}, description = "Set the piece size. When no unit is specified block size will be either 2^<n> bytes, or <n> bytes if n is larger or equal to 16384. Piece size must be a power of two in range [16K, 64M]. Leave empty or set to auto to determine by total file size. [default: auto]")
    private Integer pieceLength;

    @CommandLine.Option(names = {"-p", "--private"}, defaultValue = "false", description = "Set the private flag to disable DHT and PEX.")
    private boolean privateFlag;

    @CommandLine.Option(names = {"-s", "--source"}, defaultValue = "lovept :)", description = "Add a source tag to facilitate cross-seeding.")
    private String source;

    @CommandLine.Option(names = {"--created-by"}, defaultValue = "TorrentTools", description = " Override the value of the created by field.")
    private String createdBy;

    @CommandLine.Option(names = {"--publisher"}, defaultValue = "lovept :)", description = "Add a source tag to facilitate cross-seeding.")
    private String publisher;

    @CommandLine.Option(names = {"-d", "--creation-date"}, description = "Override the value of the creation date field as ISO-8601 time or POSIX time.")
    private String creationDate;

    @CommandLine.Option(names = {"--no-created-by"}, description = "Do not include the name and version of this program.")
    private boolean noCreatedBy;

    @CommandLine.Option(names = {"--no-creation-date"}, description = "Do not include the creation date.")
    private boolean noCreationDate;

    @CommandLine.Option(names = {"--no-publisher"}, description = "Do not include the publisher.")
    private boolean noPublisher;

    @CommandLine.Option(names = {"--no-source"}, description = "Do not include the source tag.")
    private boolean noSource;
}
