package com.github.lovept.path;

import org.junit.Test;

import java.io.File;

/**
 * @author lovept
 * @date 2024/5/31 14:19
 * @description 路径测试
 */
public class PathTest {
    @Test
    public void t1() {
        String input = "/Users/username/Python-Script";
        File file = new File(input);
        String parent = file.getParent();
        System.out.println(parent);
    }

    @Test
    public void t2() {
        String input = "1.zip";
        File file = new File(input);
        String parent = file.getParent();
        System.out.println(parent);
    }

    @Test
    public void t3() {
        String lowerCase = System.getProperty("os.name").toLowerCase();
        System.out.println(lowerCase);

        String home = System.getProperty("user.home").toLowerCase();
        System.out.println(home);
    }

    @Test
    public void t4() {
        String input = "1.zip";
        if (input.endsWith(".zip")) {
            System.out.println("zip");
        } else if (input.endsWith(".rar")) {
            System.out.println("rar");
        } else {
            System.out.println("other");
        }

    }
}
