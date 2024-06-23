package com.github.lovept.bencode;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

/**
 * @author lovept :)
 * @date 2024/6/17 18:25
 * @description TODO
 */
public class Test01 {
    @Test
    public void t1() throws IOException {
        BEncode bEncode = new BEncode();

        File torrentFile = new File("/Users/username/Downloads/1.torrent");
        byte[] torrentData = Files.readAllBytes(torrentFile.toPath());
        Map<String, Object> dict = bEncode.decode(torrentData, BType.DICTIONARY);

        String infoHash = bEncode.sha1(torrentData);
        System.out.println(infoHash);
        //
        //dict.remove("announce-list");
        //String announce = "https://tracker.example.com:80/announce";
        //dict.put("announce", announce);
        //
        //bEncode.save(torrentFile, dict);
        //System.out.println(1);
    }

    @Test
    public void t2() throws IOException {
        BEncode bEncode1 = new BEncode();
        File torrentFile1 = new File("/Users/username/Downloads/1.torrent");
        byte[] torrentData1 = Files.readAllBytes(torrentFile1.toPath());

        Map<String, Object> dict1 = bEncode1.decode(torrentData1, BType.DICTIONARY);


        String infoHash1 = bEncode1.sha1(torrentData1);
        System.out.println(infoHash1);



        BEncode bEncode2 = new BEncode();
        File torrentFile2 = new File("/Users/username/Downloads/1.torrent");
        byte[] torrentData2 = Files.readAllBytes(torrentFile2.toPath());

        Map<String, Object> dict2 = bEncode2.decode(torrentData2, BType.DICTIONARY);

        String infoHash2 = bEncode2.sha1(torrentData2);
        System.out.println(infoHash2);
    }

}
