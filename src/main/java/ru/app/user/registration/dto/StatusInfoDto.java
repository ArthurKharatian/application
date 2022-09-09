package ru.app.user.registration.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.app.user.registration.enums.Status;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatusInfoDto {

    Long userId;
    Status newStatus;
    Status oldStatus;

}
