package com.github.lovept.byteTest;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;

/**
 * @author lovept :)
 * @date 2024/6/18 17:01
 * @description TODO
 */
public class ByteTest01 {
    @Test
    public void t1() {
        byte[] data = "Hello, World!".getBytes();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        PushbackInputStream pushbackInputStream = new PushbackInputStream(byteArrayInputStream);

        try {
            int firstByte = pushbackInputStream.read();
            System.out.println((char) firstByte); // 输出 'H'

            pushbackInputStream.unread(firstByte); // 回推 'H'

            int secondByte = pushbackInputStream.read();
            System.out.println((char) secondByte); // 再次输出 'H'
        } catch (IOException ignored) {
        }
    }
}
