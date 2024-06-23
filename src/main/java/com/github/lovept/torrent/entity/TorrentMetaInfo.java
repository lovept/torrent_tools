package com.github.lovept.torrent.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lovept :)
 * @date 2024/6/14 14:20
 * @description 种子元信息
 */
@Getter
@Setter
public class TorrentMetaInfo {
    private String announce;
    private List<String> announceList = new ArrayList<>();
    private Long creationDate;
    private String comment;
    private String createdBy;
    private Info info;

    @Getter
    @Setter
    public static class Info {
        private Long pieceLength;
        private String pieces;
        private Long privateFlag;
        private String source;
        // 单文件结构
        private String name;
        private Long length = 0L;
        // 多文件结构
        List<File> files  = new ArrayList<>();

        @Getter
        @Setter
        public static class File {
            private Long length;
            private List<String> path;
        }
    }
}

