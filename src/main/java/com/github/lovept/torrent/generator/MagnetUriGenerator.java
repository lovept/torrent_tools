package com.github.lovept.torrent.generator;

import com.github.lovept.torrent.entity.Torrent;
import lombok.Getter;
import lombok.Setter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author lovept :)
 * @date 2024/6/14 11:07
 * @description 磁力链接生成器
 */
@Getter
@Setter
public class MagnetUriGenerator {
    private static final String PREFIX = "magnet:?xt=urn:btih:";
    private static final String DN = "&dn=";
    private static final String TR = "&tr=";
    private static final String CHARSET = "UTF-8";

    public static void generator(Torrent torrent) throws UnsupportedEncodingException {
        if (torrent == null) {
            throw new IllegalArgumentException("初始化的种子文件无效");
        }

        String hash = torrent.getInfoHash();
        Map<String, Object> metaInfo = torrent.getMetaInfo();
        @SuppressWarnings("unchecked")
        Map<String, Object> infoMap = (Map<String, Object>) metaInfo.get("info");
        String name = infoMap.get("name").toString();
        String primaryTracker = metaInfo.get("announce").toString();

        StringBuilder builder = new StringBuilder(PREFIX)
                .append(hash)
                .append(DN).append(URLEncoder.encode(name, CHARSET))
                .append(TR).append(URLEncoder.encode(primaryTracker, CHARSET));

        List<String> trackers = new ArrayList<>();
        Object announceListObj = metaInfo.get("announce-list");
        if (announceListObj instanceof List) {
            try {
                @SuppressWarnings("unchecked")
                List<List<String>> announceList = (List<List<String>>) announceListObj;
                announceList.forEach(trackers::addAll);
            } catch (ClassCastException ignored) {
            }
        }

        Set<String> uniqueTrackers = new HashSet<>(trackers);
        uniqueTrackers.remove(primaryTracker);

        for (String trackerElement : uniqueTrackers) {
            builder.append(TR).append(URLEncoder.encode(trackerElement, CHARSET));
        }

        torrent.setMagnetUri(builder.toString());
    }

}

