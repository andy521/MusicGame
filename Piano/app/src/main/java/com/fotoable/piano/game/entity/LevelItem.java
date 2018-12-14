package com.fotoable.piano.game.entity;

/**
 * Created by damon on 30/06/2017.
 */

public class LevelItem {
    public int level;
    public int xp;
    public int userxp;
    public String title;
    public int coins;

    @Override
    public String toString() {
        return "LevelItem{" +
                "level=" + level +
                ", xp=" + xp +
                ", userxp=" + userxp +
                ", title='" + title + '\'' +
                ", coins=" + coins +
                '}';
    }
}
