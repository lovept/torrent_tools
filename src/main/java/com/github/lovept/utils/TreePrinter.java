package com.github.lovept.utils;

import java.io.File;
import java.text.DecimalFormat;

/**
 * @author lovept :)
 * @date 2024/5/26 17:17
 * @description 打印目录树
 */
public class TreePrinter {

    private static File rootDir;
    private static boolean useColor;

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_PURPLE = "\u001B[35m";

    public static void printer(String directoryPath, boolean isUseColor) {
        rootDir = new File(directoryPath);
        useColor = isUseColor;
        printTree();
    }

    private static void printTree() {
        if (!rootDir.exists()) {
            System.out.println("The directory does not exist.");
            return;
        }
        // 根目录不显示大小，并使用颜色高亮显示名称
        System.out.println((useColor ? ANSI_PURPLE : "") + rootDir.getName() + (useColor ? ANSI_RESET : ""));
        printEntries(rootDir, "");
    }

    private static long calculateDirectorySize(File directory) {
        long size = 0;
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    size += file.length();
                } else {
                    size += calculateDirectorySize(file);
                }
            }
        }
        return size;
    }

    private static void printEntries(File directory, String prefix) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            boolean last = i == files.length - 1;
            String linePrefix = prefix + (last ? "└── " : "├── ");
            if (file.isDirectory()) {
                long dirSize = calculateDirectorySize(file);
                System.out.println(linePrefix + formatName(file.getName(), " [" + readableFileSize(dirSize) + "]", true));
                printEntries(file, prefix + (last ? "    " : "│   "));
            } else {
                System.out.println(linePrefix + formatName(file.getName(), " [" + readableFileSize(file.length()) + "]", false));
            }
        }
    }

    private static String formatName(String name, String size, boolean isDirectory) {
        return (isDirectory ? ANSI_BLUE : ANSI_GREEN) + name + ANSI_RESET + size;
    }

    private static String readableFileSize(long size) {
        if (size <= 0) {
            return "0 B";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB", "PB", "EB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

}


