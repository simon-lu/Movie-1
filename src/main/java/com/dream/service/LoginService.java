package com.dream.service;

import com.dream.common.E3Result;

/**
 * Created by ZXL on 2018/3/2.
 */
public interface LoginService {
    E3Result userLogin(String username, String password);
}
