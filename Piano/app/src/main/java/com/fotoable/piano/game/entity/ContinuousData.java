package com.fotoable.piano.game.entity;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by damon on 27/06/2017.
 */

public class ContinuousData {


    /**
     * 当前连续击中音节的数量, 每次断开则清零,重新计数
     */
    public AtomicInteger currentContinuousHit = new AtomicInteger(0);

    /**
     * 连续击中的最后一个node的index
     */
    public int lastHitIndex = -1;
}
