package com.naoido.discordbot.util;

import net.dv8tion.jda.internal.utils.tuple.Pair;

import java.util.stream.IntStream;

public class Util {

    /**
     * 素数を判定する
     * @param i 判定する数
     * @return 結果と割り切れた数
     */
    public static Pair<Boolean, String> isPrime(int i) {
        if ((i % 2) == 0) return Pair.of(false, "2");
        Pair<Boolean, String> result = Pair.of(true, "");
        for (int j :IntStream.rangeClosed(2, (int) Math.sqrt(i)).toArray()){
            if ((j % 2) != 0) {
                if ((i % j) == 0) {
                    result = Pair.of(false, String.valueOf(j));
                    break;
                }
            }
        }
        return result;
    }
}
