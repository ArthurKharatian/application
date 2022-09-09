package ru.app.user.registration.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level =  AccessLevel.PRIVATE)
public class StatisticsDto {

    Long totalUsersCount;
    List<UserDto> usersByStatus;
    List<UserDto> usersByAge;
    Integer averageAge;

}
