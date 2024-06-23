package com.github.lovept.torrent.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lovept :)
 * @date 2024/6/13 18:56
 * @description 种子文件
 */
@Getter
@Setter
public class Torrent {
    private String path;
    private String name;
    private String protocolVersion = "v1";
    private String infoHash;
    private String magnetUri;
    private Map<String, Object> metaInfo = new HashMap<>();


    public Torrent(String path, String name) {
        this.path = path;
        this.name = name;
    }
}
