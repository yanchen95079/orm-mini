package com.yc.orm;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.Table;
import java.util.Date;
/**
 * @author Yanchen
 * @ClassName User
 * @Date 2019/4/25 15:46
 */
@Table(name="t_user")
@Setter
@Getter
public class User {
    private Integer id;

    private String name;

    private Integer age;

    private Long createDate;
}
