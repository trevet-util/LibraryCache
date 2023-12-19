package cn.trevet.library.cache.enums;

import lombok.Getter;

@Getter
public enum ResCacheTypeEnum {

    MAVEN(1, "maven", true),

    // Alpine存在索引包需要额外判断下载的连接是否为索引包,因此不能使用通用Service
    ALPINE(2, "alpine", false),

    PYTHON(3, "python", true)
    // 换行
    ;
    private final int type;
    private final String name;
    /**
     * 是否通用
     */
    private final boolean isBase;

    ResCacheTypeEnum(int type, String name, boolean isBase) {
        this.type = type;
        this.name = name;
        this.isBase = isBase;
    }
}
