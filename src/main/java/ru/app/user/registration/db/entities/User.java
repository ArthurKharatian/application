package ru.app.user.registration.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.app.user.registration.enums.Status;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    String bornDate;
    Integer age;
    String email;
    Status newStatus = Status.UNDEFINED;
    Status oldStatus = Status.UNDEFINED;

}
