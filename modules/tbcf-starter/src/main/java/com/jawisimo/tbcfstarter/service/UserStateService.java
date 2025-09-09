package com.jawisimo.tbcfstarter.service;

public interface UserStateService {
    String getUserStateOrDefault(String chatId, String defaultState);
    void saveUserState(String chatId, String nextState);
}
