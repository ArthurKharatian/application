package ru.app.user.registration.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.app.user.registration.db.entities.User;
import ru.app.user.registration.enums.Status;

import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    User findByEmail(String email);

    List<User> findAllByNewStatus(Status status);

    @Query(value = "select * from users where users.age >= :age order by users.age asc", nativeQuery = true)
    List<User> findAdults(@Param("age") Integer ageLimiter);

    @Query(value = "select * from users where users.age < :age order by users.age asc", nativeQuery = true)
    List<User> findTeens(@Param("age") Integer ageLimiter);

}
