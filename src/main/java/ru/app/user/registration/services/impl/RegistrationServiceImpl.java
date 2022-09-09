package ru.app.user.registration.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.app.user.registration.db.entities.User;
import ru.app.user.registration.db.repositories.UserRepo;
import ru.app.user.registration.dto.StatisticsDto;
import ru.app.user.registration.dto.StatusInfoDto;
import ru.app.user.registration.dto.UserDto;
import ru.app.user.registration.enums.Status;
import ru.app.user.registration.exceptions.RegistrationException;
import ru.app.user.registration.services.RegistrationService;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final UserRepo userRepo;
    private final ObjectMapper mapper;

    @Override
    public UserDto createUser(UserDto userDto) {

        String email = userDto.getEmail();
        validateEmail(email);

        User userByEmail = userRepo.findByEmail(email);
        if (userByEmail != null) {
            String errMessage = String.format("Пользователь с почтой %s уже существует", email);
            throw new RegistrationException(errMessage, HttpStatus.CONFLICT);
        }

        int age = getAge(userDto.getBornDate());


        User user = mapper.convertValue(userDto, User.class);
        user.setAge(age);

        User save = userRepo.save(user);
        return convertToUserDto(save);
    }

    @Override
    public void validateEmail(String email) {

        if (!EmailValidator.getInstance().isValid(email)) {
            String errMessage = String.format("Неверный формат почты %s. Пример почты: name@example.com", email);
            throw new RegistrationException(errMessage, HttpStatus.CONFLICT);
        }

    }

    @Override
    public LocalDate validateAndGetDate(String date) {

        if (date == null || date.isEmpty()) {
            throw new RegistrationException("Отсутствует дата", HttpStatus.CONFLICT);
        }

        try {
            return LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        } catch (DateTimeParseException e) {
            log.error(e.getMessage());
            String errMessage = String.format("Неверный формат даты %s . Пример формата даты: 31.12.2001", date);
            throw new RegistrationException(errMessage, HttpStatus.CONFLICT);
        }

    }

    @Override
    public int getAge(String date) {

        LocalDate bornDate = validateAndGetDate(date);
        LocalDate today = LocalDate.now();
        if (bornDate.isAfter(today)) {
            String errMessage = String.format("Дата рождения не может превышать текущую дату. Введенная дата: %s", date);
            throw new RegistrationException(errMessage, HttpStatus.CONFLICT);
        }

        return Period.between(bornDate, today).getYears();
    }

    @Override
    public UserDto getUser(Long id) {

        User user = getUserFromDB(id);

        return convertToUserDto(user);

    }

    @Override
    public StatusInfoDto changeStatus(Long id, Status status) {

        User user = getUserFromDB(id);
        user.setOldStatus(user.getNewStatus());
        user.setNewStatus(status);

        User updatedUser = userRepo.save(user);

        return StatusInfoDto.builder()
                .userId(updatedUser.getId())
                .newStatus(updatedUser.getNewStatus())
                .oldStatus(updatedUser.getOldStatus())
                .build();

    }

    private User getUserFromDB(Long id) {
        return userRepo.findById(id).orElseThrow(() ->
                new RegistrationException(String.format("Пользователь с id %s не найден", id), HttpStatus.NOT_FOUND));
    }

    @Override
    public StatisticsDto getStatistics(String status, Boolean isAdult) {

        List<User> usersByStatus = new ArrayList<>();
        if (status != null && !status.isEmpty()) {

            Status incomingStatus;
            if (status.equalsIgnoreCase(Status.ONLINE.name())) {
                incomingStatus = Status.ONLINE;

            } else if (status.equalsIgnoreCase(Status.OFFLINE.name())) {
                incomingStatus = Status.OFFLINE;

            } else {
                throw new RegistrationException(String.format("Передан неверный статус %s", status), HttpStatus.BAD_REQUEST);
            }

            usersByStatus = userRepo.findAllByNewStatus(incomingStatus);
        }

        List<User> usersByAge = new ArrayList<>();
        if (isAdult != null) {
            final int ageLimiter = 18;
            if (isAdult) {
                usersByAge = userRepo.findAdults(ageLimiter);
            } else {
                usersByAge = userRepo.findTeens(ageLimiter);
            }
        }

        List<UserDto> usersByStatusDto = usersByStatus.stream()
                .map(this::convertToUserDto)
                .collect(Collectors.toList());

        List<UserDto> usersByAgeDto = usersByAge.stream()
                .map(this::convertToUserDto)
                .collect(Collectors.toList());

        Set<UserDto> extractedUsers = new HashSet<>(usersByStatusDto);
        extractedUsers.addAll(usersByAgeDto);

        long extractedUsersSize = extractedUsers.size();
        AtomicInteger ageSum = new AtomicInteger();

        extractedUsers.forEach(user -> ageSum.getAndAdd(user.getAge()));

        int averageAge = 0;
        if (extractedUsersSize != 0) {
            averageAge = (int) (ageSum.get() / extractedUsersSize);
        }

        StatisticsDto statisticsDto = new StatisticsDto();
        statisticsDto.setTotalUsersCount(userRepo.count());
        statisticsDto.setUsersByStatus(usersByStatusDto);
        statisticsDto.setUsersByAge(usersByAgeDto);
        statisticsDto.setAverageAge(averageAge);
        return statisticsDto;

    }

    private UserDto convertToUserDto(User user) {
        UserDto userDto = mapper.convertValue(user, UserDto.class);
        userDto.setCurrentStatus(user.getNewStatus().name());
        return userDto;
    }

}
