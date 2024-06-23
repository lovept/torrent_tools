package com.github.lovept;

import com.github.lovept.commands.TorrentToolsCommand;
import picocli.CommandLine;

/**
 * @author lovept :)
 * @date 2024/5/26 11:15
 */
public class App {
    public static void main(String[] args) {
        new CommandLine(new TorrentToolsCommand()).execute(args);
    }
}
