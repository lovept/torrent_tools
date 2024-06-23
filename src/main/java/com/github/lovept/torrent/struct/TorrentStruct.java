package com.github.lovept.torrent.struct;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lovept :)
 * @date 2024/6/13 19:08
 * @description 种子文件结构体
 */
@Getter
@Setter
public class TorrentStruct {
    private byte[] info;

    private Integer index;

    private Integer infoStart;
    private Integer infoEnd;

    public TorrentStruct(byte[] info, Integer index) {
        this.info = info;
        this.index = index;
    }

    public void advanceIndex(int offset) {
        this.index = index + offset;
    }

    public char getChar() {
        return (char) info[index];
    }

    public Integer getLength() {
        return info.length;
    }

}
