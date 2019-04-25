package com.yc.orm;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrmApplicationTests {

    @Test
    public void contextLoads() {
        User user = new User();
        user.setId(2);
        List<User> users = Dao.select(user);
        System.out.println(users);
    }
    }

}
