package ru.app.user.registration.controllers;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.app.user.registration.dto.StatisticsDto;
import ru.app.user.registration.dto.StatusInfoDto;
import ru.app.user.registration.dto.UserDto;
import ru.app.user.registration.enums.Status;
import ru.app.user.registration.services.RegistrationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/registration")
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping("/create")
    @Operation(summary = "Добавление нового пользователя")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        return ResponseEntity.ok(registrationService.createUser(userDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение информации о пользователе по id")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(registrationService.getUser(id));
    }

    @GetMapping("/status/{id}")
    @Operation(summary = "Изменение статуса пользователя по id")
    public ResponseEntity<StatusInfoDto> changeStatus(@PathVariable Long id, @RequestParam Status status) {
        return ResponseEntity.ok(registrationService.changeStatus(id, status));
    }

    @GetMapping("/statistics")
    @Operation(summary = "Статистика сервера")
    public ResponseEntity<StatisticsDto> getStatistics(@RequestParam(required = false) String status,
                                                       @RequestParam(required = false) Boolean isAdult) {
        return ResponseEntity.ok(registrationService.getStatistics(status, isAdult));
    }

}
