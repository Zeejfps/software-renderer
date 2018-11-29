package com.zeejfps.sr;

public interface ApplicationListener {
    void init();
    void update(double dt);
    void fixedUpdate();
}
