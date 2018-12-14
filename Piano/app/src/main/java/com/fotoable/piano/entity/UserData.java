package com.fotoable.piano.entity;

/**
 * Created by damon on 28/06/2017.
 * 用户信息实体类
 */

public class UserData {

    /**
     * 用于标示用户唯一身份的id(如果是默认用户的话,这个id意义不大,既然是默认的我完全不需要id来识别身份啊)
     * 1.不能修改
     * 2.不能为空
     */
    public String id;

    /**
     * 用户的 昵称 (如果是默认用户的话,这个NickName 意义也不大,因为nick定义个啥呢? )
     *
     */
    public String nickName;

    /**
     * 该用户的游戏等级
     * 范围: [0,+∞)
     */
    public int level;
    /**
     * 在当前等级中获取的经验
     * 比如: level==3   xpLevel=50; 则表示在第三级中获取的经验
     * 范围: [0,+∞)
     */
    public int xpLevel;
    /**
     * 所有等级一共获取的经验
     * 范围: [0,+∞)
     */
    public int xpTotal;
    /**
     * 该用户的金币数
     * 范围: [0,+∞)
     */
    public int coin;

    @Override
    public String toString() {
        return "UserData{" +
                "id='" + id + '\'' +
                ", nickName='" + nickName + '\'' +
                ", level=" + level +
                ", xpLevel=" + xpLevel +
                ", xpTotal=" + xpTotal +
                ", coin=" + coin +
                '}';
    }
}
