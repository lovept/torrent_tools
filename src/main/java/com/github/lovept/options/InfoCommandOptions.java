package com.github.lovept.options;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine;

import java.io.File;

/**
 * @author lovept :)
 * @date 2024/6/14 09:49
 * @description info options
 */
@Getter
@Setter
public class InfoCommandOptions {

    @CommandLine.Parameters(index = "0", description = ".torrent file path.")
    private File inputFile;

    @CommandLine.Option(names = {"--raw"}, description = "Print the metafile data formatted as JSON.")
    private boolean raw;
}
