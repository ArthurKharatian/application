package ru.app.user.registration.services;

import ru.app.user.registration.dto.StatisticsDto;
import ru.app.user.registration.dto.StatusInfoDto;
import ru.app.user.registration.dto.UserDto;
import ru.app.user.registration.enums.Status;

import java.time.LocalDate;

public interface RegistrationService {

    UserDto createUser(UserDto userDto);

    void validateEmail(String email);

    LocalDate validateAndGetDate(String date);

    int getAge(String date);

    UserDto getUser(Long id);

    StatusInfoDto changeStatus(Long id, Status status);

    StatisticsDto getStatistics(String status, Boolean isAdult);
}
