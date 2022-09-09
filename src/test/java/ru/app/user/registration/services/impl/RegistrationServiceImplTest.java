package ru.app.user.registration.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import ru.app.user.registration.db.entities.User;
import ru.app.user.registration.db.repositories.UserRepo;
import ru.app.user.registration.dto.StatisticsDto;
import ru.app.user.registration.dto.StatusInfoDto;
import ru.app.user.registration.dto.UserDto;
import ru.app.user.registration.enums.Status;
import ru.app.user.registration.exceptions.RegistrationException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RegistrationServiceImplTest {

    @InjectMocks
    private RegistrationServiceImpl registrationService;

    @Mock
    private UserRepo userRepo;

    @Spy
    private ObjectMapper mapper;

    private UserDto userDto;
    private User user;
    private long usersCount;

    @Before
    public void setup() {
        userDto = new UserDto();
        userDto.setName("Boris");
        userDto.setEmail("boris@mail.ru");
        userDto.setBornDate("11.12.1999");

        Long id = 1L;
        user = new User();
        user.setId(id);
        user.setEmail(userDto.getEmail());
        user.setNewStatus(Status.UNDEFINED);

        usersCount = 10L;
        when(userRepo.count()).thenReturn(usersCount);
    }

    @Test
    public void createUser_notExists() {
        when(userRepo.findByEmail(userDto.getEmail())).thenReturn(null);
        when(userRepo.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        UserDto result = registrationService.createUser(userDto);
        assertEquals(Status.UNDEFINED.name(), result.getCurrentStatus());
        assertEquals(Integer.valueOf(22), result.getAge());
    }

    @Test(expected = RegistrationException.class)
    public void createUser_Exists() {
        when(userRepo.findByEmail(userDto.getEmail())).thenReturn(user);
        registrationService.createUser(userDto);
    }

    @Test(expected = RegistrationException.class)
    public void validateEmail_badEmail() {
        registrationService.validateEmail("boris@mail@.ru");
    }

    @Test(expected = RegistrationException.class)
    public void validateAndGetDate_badDate() {
        registrationService.validateAndGetDate("2004-12-30");
    }

    @Test(expected = RegistrationException.class)
    public void validateAndGetDate_emptyDate() {
        registrationService.validateAndGetDate("");
    }

    @Test(expected = RegistrationException.class)
    public void getAge_badAge() {
        LocalDate today = LocalDate.now();
        LocalDate bornDate = today.plusDays(10);
        String date = bornDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        registrationService.getAge(date);
    }

    @Test
    public void getUser() {

        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));

        UserDto result = registrationService.getUser(user.getId());
        assertEquals(userDto.getEmail(), result.getEmail());
    }

    @Test(expected = RegistrationException.class)
    public void getUser_fail() {
        registrationService.getUser(1L);
    }

    @Test
    public void changeStatus() {
        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepo.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        StatusInfoDto result = registrationService.changeStatus(1L, Status.ONLINE);
        assertEquals(Status.UNDEFINED, result.getOldStatus());
        assertEquals(Status.ONLINE, result.getNewStatus());
    }

    @Test(expected = RegistrationException.class)
    public void changeStatus_userNotFound() {
        registrationService.changeStatus(1L, Status.ONLINE);
    }

    @Test(expected = RegistrationException.class)
    public void getStatistics_badStatus() {
        registrationService.getStatistics("Bad", null);
    }

    @Test
    public void getStatistics_noStatusNoAge() {
        StatisticsDto result = registrationService.getStatistics(null, null);
        assertTrue(result.getUsersByAge().isEmpty());
        assertTrue(result.getUsersByStatus().isEmpty());
        assertEquals(Long.valueOf(usersCount), result.getTotalUsersCount());
        assertEquals(Integer.valueOf(0), result.getAverageAge());
    }

    @Test
    public void getStatistics_statusOnly() {
        Status status = Status.ONLINE;
        user.setAge(20);
        when(userRepo.findAllByNewStatus(status)).thenReturn(Collections.singletonList(user));

        StatisticsDto result = registrationService.getStatistics(status.name(), null);
        assertTrue(result.getUsersByAge().isEmpty());
        assertFalse(result.getUsersByStatus().isEmpty());
        assertEquals(user.getAge(), result.getAverageAge());
    }

    @Test
    public void getStatistics_statusOfflineOnly() {
        Status status = Status.OFFLINE;
        user.setAge(20);
        when(userRepo.findAllByNewStatus(status)).thenReturn(Collections.singletonList(user));

        StatisticsDto result = registrationService.getStatistics(status.name(), null);
        assertTrue(result.getUsersByAge().isEmpty());
        assertFalse(result.getUsersByStatus().isEmpty());
        assertEquals(user.getAge(), result.getAverageAge());
    }

    @Test
    public void getStatistics_ageOnlyAdults() {

        user.setAge(20);
        when(userRepo.findAdults(18)).thenReturn(Collections.singletonList(user));

        StatisticsDto result = registrationService.getStatistics(null, true);
        assertFalse(result.getUsersByAge().isEmpty());
        assertTrue(result.getUsersByStatus().isEmpty());
        assertEquals(user.getAge(), result.getAverageAge());
    }

    @Test
    public void getStatistics_ageOnlyTeens() {
        user.setAge(12);
        when(userRepo.findTeens(18)).thenReturn(Collections.singletonList(user));

        StatisticsDto result = registrationService.getStatistics(null, false);
        assertFalse(result.getUsersByAge().isEmpty());
        assertTrue(result.getUsersByStatus().isEmpty());
        assertEquals(user.getAge(), result.getAverageAge());
    }

    @Test
    public void getStatistics_statusAndAge() {
        Status status = Status.OFFLINE;
        user.setAge(20);
        when(userRepo.findAllByNewStatus(status)).thenReturn(Collections.singletonList(user));

        User teen = new User();
        teen.setAge(12);
        when(userRepo.findTeens(18)).thenReturn(Collections.singletonList(teen));

        StatisticsDto result = registrationService.getStatistics(status.name(), false);
        assertFalse(result.getUsersByAge().isEmpty());
        assertFalse(result.getUsersByStatus().isEmpty());
        assertEquals(Integer.valueOf((user.getAge() + teen.getAge()) / 2), result.getAverageAge());

    }
}