package com.github.lovept.bencode;

import java.util.function.IntPredicate;

/**
 * @author lovept
 * @date 2024/5/26 17:17
 * @description bEncoded data type
 */
public class BType {

    public static final BType STRING = new BType(Character::isDigit);

    public static final BType NUMBER = new BType(token -> token == BEncode.NUMBER);

    public static final BType LIST = new BType(token -> token == BEncode.LIST);

    public static final BType DICTIONARY = new BType(token -> token == BEncode.DICTIONARY);

    public static final BType UNKNOWN = new BType(token -> false);

    private final IntPredicate predicate;

    private BType(IntPredicate predicate) {
        this.predicate = predicate;
    }

    boolean validate(final int token) {
        return predicate.test(token);
    }

    public static BType[] values() {
        return new BType[]{STRING, NUMBER, LIST, DICTIONARY, UNKNOWN};
    }
}
