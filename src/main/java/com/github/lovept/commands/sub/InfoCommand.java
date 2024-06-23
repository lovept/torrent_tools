package com.github.lovept.commands.sub;

import cn.hutool.json.JSONUtil;
import com.github.lovept.torrent.generator.TorrentMetaInfoGenerator;
import com.github.lovept.torrent.reader.TorrentReader;
import com.github.lovept.torrent.entity.Torrent;
import com.github.lovept.torrent.entity.TorrentMetaInfo;
import com.github.lovept.options.InfoCommandOptions;
import picocli.CommandLine;

import java.io.IOException;
import java.util.List;

/**
 * @author lovept :)
 * @date 2024/6/14 09:46
 * @description General information about bittorrent metafiles.
 */
@CommandLine.Command(name = "info",
        mixinStandardHelpOptions = true,
        version = "0.0.1",
        header = "General information about bittorrent metafiles.",
        optionListHeading = "%nOptions: %n")
public class InfoCommand implements Runnable {

    @CommandLine.Mixin
    private InfoCommandOptions options;

    @Override
    public void run() {
        try {
            String absolutePath = options.getInputFile().getAbsolutePath();
            Torrent torrent = TorrentReader.build(absolutePath);
            TorrentMetaInfoGenerator metaInfoGenerator = new TorrentMetaInfoGenerator(torrent);
            TorrentMetaInfo metaInfo = metaInfoGenerator.generate();
            TorrentMetaInfo.Info info = metaInfo.getInfo();

            if (options.isRaw()) {
                System.out.print(JSONUtil.toJsonPrettyStr(torrent.getMetaInfo()));
                return;
            }

            printMetaInfo(torrent, metaInfo, info);
            printAnnounceList(metaInfo.getAnnounceList());
            printFilesInfo(info);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void printMetaInfo(Torrent torrent, TorrentMetaInfo metaInfo, TorrentMetaInfo.Info info) {
        System.out.println("Metafile:         " + torrent.getName());
        System.out.println("Protocol version: " + torrent.getProtocolVersion());
        System.out.println("InfoHash:         v1: " + torrent.getInfoHash());
        System.out.println("Piece size:       " + info.getPieceLength());
        System.out.println("Created by:       " + metaInfo.getCreatedBy());
        System.out.println("Created on:       " + metaInfo.getCreationDate());
        System.out.println("Private:          " + (info.getPrivateFlag().equals(0L)));
        System.out.println("Name:             " + info.getName());
        System.out.println("Source:           " + info.getSource());
        System.out.println("Comment:          " + metaInfo.getComment());
        System.out.println();
    }

    private void printAnnounceList(List<String> announceList) {
        System.out.println("Announces:        ");
        announceList.forEach(announce -> System.out.println("  " + announce));
        System.out.println();
    }

    private void printFilesInfo(TorrentMetaInfo.Info info) {
        List<TorrentMetaInfo.Info.File> files = info.getFiles();
        System.out.println("Files:            ");
        if (!files.isEmpty()) {
            files.forEach(file -> System.out.println("  [" + file.getLength() + "] " + file.getPath().get(0)));
        } else {
            System.out.println("[" + info.getLength() + "] " + info.getName());
        }
    }

}
