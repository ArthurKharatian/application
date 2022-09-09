package ru.app.user.registration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {

    @Schema(description = "Имя пользователя")
    String name;

    @Schema(description = "Дата рождения пользователя")
    String bornDate;

    @Schema(description = "Дата рождения пользователя")
    Integer age;

    @Schema(description = "Электронная почта пользователя")
    String email;

    @Schema(description = "Текущий статус пользователя")
    String currentStatus;

}
