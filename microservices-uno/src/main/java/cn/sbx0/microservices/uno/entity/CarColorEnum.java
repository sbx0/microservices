package cn.sbx0.microservices.uno.entity;

/**
 * @author sbx0
 * @since 2022/3/9
 */
public enum CarColorEnum {
    YELLOW("黄色", "yellow"),
    BLUE("蓝色", "blue"),
    GREEN("绿色", "green"),
    RED("红色", "red");
    private String name;
    private String value;

    CarColorEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
