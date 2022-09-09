package ru.app.user.registration.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {

    UNDEFINED("Отсутствует"),
    ONLINE("онлайн"),
    OFFLINE("офлайн");

    private final String description;

}
