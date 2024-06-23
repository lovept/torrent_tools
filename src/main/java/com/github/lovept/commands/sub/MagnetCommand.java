package com.github.lovept.commands.sub;

import com.github.lovept.torrent.reader.TorrentReader;
import com.github.lovept.torrent.entity.Torrent;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;

/**
 * @author lovept :)
 * @date 2024/6/14 16:47
 * @description magnet uri
 */
@CommandLine.Command(name = "magnet",
        mixinStandardHelpOptions = true,
        version = "0.0.1",
        header = "Getting a magnetic link to a seed file",
        optionListHeading = "%nOptions: %n")
public class MagnetCommand  implements Runnable {
    @CommandLine.Parameters(index = "0", description = ".torrent file path.")
    private File inputFile;

    @Override
    public void run() {
        String absolutePath = inputFile.getAbsolutePath();
        try {
            Torrent torrent = TorrentReader.build(absolutePath);
            System.out.print(torrent.getMagnetUri());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
