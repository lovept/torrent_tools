package com.github.lovept.options;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine;

import java.io.File;

/**
 * @author lovopt :)
 * @date 2024/6/12 15:58
 * @description edit命令的选项
 */
@Getter
@Setter
public class EditCommandOptions {

    @CommandLine.Parameters(index = "0", description = "edit the torrent file.")
    private File inputFile;

    @CommandLine.Option(names = {"-o", "--output"}, description = "Set the filename and/or output directory of the created file. %n[default: <name>.torrent]%nThis will overwrite the existing file if the name is the same.%nUse a path with trailing slash to only set the output directory.%n")
    private String outputPath;

    @CommandLine.Option(names = {"-a", "--announce"}, description = "Add one or multiple announces urls.")
    private String announceUrl;

    @CommandLine.Option(names = {"-c", "--comment"}, description = "Add a comment.")
    private String comment;

    @CommandLine.Option(names = {"-n", "--name"}, description = "Set the name of the torrent. This changes the filename for single file torrents.")
    private String torrentName;

    @CommandLine.Option(names = {"-p", "--private"}, description = "Set the private flag to disable DHT and PEX.")
    private String privateFlag;

    @CommandLine.Option(names = {"-s", "--source"}, description = "Add a source tag to facilitate cross-seeding.")
    private String source;

    @CommandLine.Option(names = {"--create-by"}, description = "Override the value of the created by field.")
    private String createBy;

    @CommandLine.Option(names = {"--publisher"}, description = "Override the value of the publisher field.")
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

    @CommandLine.Option(names = {"--no-announce"}, description = "Do not include the announce tag.")
    private boolean noAnnounce;
}
