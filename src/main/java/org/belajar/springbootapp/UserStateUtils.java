package org.belajar.springbootapp;

import lombok.Data;

import java.util.Map;

@Data
public class UserStateUtils {

    public Map<Long, UserState> userStateMap;
    public Long userId;
}
