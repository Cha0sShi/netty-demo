package com.asm.server.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserServiceMemoryImpl implements UserService {

    @Override
    public boolean login(String username, String password) {

        final String pass = allUserMap.get(username);
        if (pass == null)
        {
            return false;
        }

        return pass.equals(password);
    }


    private Map<String, String> allUserMap = new ConcurrentHashMap<>();
    {
        allUserMap.put("admin", "123456");
        allUserMap.put("scs", "123456");
        allUserMap.put("lll", "123456");
        allUserMap.put("cha0s", "123456");
    }
}
