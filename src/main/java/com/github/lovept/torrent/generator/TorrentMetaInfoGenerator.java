package com.github.lovept.torrent.generator;

import com.github.lovept.torrent.entity.Torrent;
import com.github.lovept.torrent.entity.TorrentMetaInfo;

import java.util.*;

/**
 * @author lovept :)
 * @date 2024/6/14 14:38
 * @description 种子元信息生成器
 */

public class TorrentMetaInfoGenerator {
    private final Torrent torrent;
    private final TorrentMetaInfo torrentMetaInfo;

    public TorrentMetaInfoGenerator(Torrent torrent) {
        this.torrent = torrent;
        this.torrentMetaInfo = new TorrentMetaInfo();
    }

    public TorrentMetaInfo generate() {
        Map<String, Object> metaInfo = torrent.getMetaInfo();


        torrentMetaInfo.setAnnounce(getValue(metaInfo.get("announce"), String.class, ""));
        torrentMetaInfo.setCreationDate(getValue(metaInfo.get("creation date"), Long.class, 0L));
        torrentMetaInfo.setComment(getValue(metaInfo.get("comment"), String.class, ""));
        torrentMetaInfo.setCreatedBy(getValue(metaInfo.get("created by"), String.class, ""));

        TorrentMetaInfo.Info info = new TorrentMetaInfo.Info();
        @SuppressWarnings("unchecked")
        Map<String, Object> infoMap = (Map<String, Object>) metaInfo.get("info");

        info.setName(getValue(infoMap.get("name"), String.class, ""));
        info.setPieceLength(getValue(infoMap.get("piece length"), Long.class, 0L));
        info.setPieces(getValue(infoMap.get("pieces"), String.class, ""));
        info.setPrivateFlag(getValue(infoMap.get("private"), Long.class, 0L));
        info.setSource(getValue(infoMap.get("source"), String.class, ""));

        Object files = infoMap.get("files");
        if (files instanceof List) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> fileList = (List<Map<String, Object>>) files;
            for (Map<String, Object> fileMap : fileList) {
                TorrentMetaInfo.Info.File file = new TorrentMetaInfo.Info.File();
                file.setLength(getValue(fileMap.get("length"), Long.class, 0L));
                file.setPath(convertPath(fileMap.get("path")));
                info.getFiles().add(file);
            }
        } else {
            info.setLength(getValue(infoMap.get("length"), Long.class, 0L));
        }

        Object announces = metaInfo.get("announce-list");
        if (announces instanceof List<?> announceList) {
            if (!announceList.isEmpty() && announceList.get(0) instanceof List) {
                @SuppressWarnings("unchecked")
                List<List<String>> announceListList = (List<List<String>>) announces;
                for (List<String> announce : announceListList) {
                    torrentMetaInfo.getAnnounceList().addAll(announce);
                }
            } else {
                for (Object tracker : announceList) {
                    if (tracker instanceof String) {
                        torrentMetaInfo.getAnnounceList().add((String) tracker);
                    }
                }
            }
        }

        torrentMetaInfo.setInfo(info);
        return torrentMetaInfo;
    }

    // 泛型方法获取值，如果为空则返回指定默认值
    private <T> T getValue(Object obj, Class<T> type, T defaultValue) {
        return Optional.ofNullable(obj)
                .map(type::cast)
                .orElse(defaultValue);
    }

    // 处理路径的转换逻辑
    private List<String> convertPath(Object path) {
        if (path instanceof List) {
            @SuppressWarnings("unchecked")
            List<String> pathList = (List<String>) path;
            return pathList;
        } else {
            return Collections.singletonList(path.toString());
        }
    }
}


