package com.is4tech.base.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Users {
    static final String SEQ_NAME = "users_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQ_NAME)
    @SequenceGenerator(name = SEQ_NAME, sequenceName = SEQ_NAME, allocationSize = 1)

    private Integer userId;
    private String name;
    private String surname;
    private String email;
    @JsonIgnore
    private String password;
    private Boolean status;
    private Integer profileId;


    public static String generatePassword() {
        var upperCase = "ABCDEFGHIJKLMNÑOPQRSTUVWXYZ";
        var lowerCase = "abcdefghijklmnñopqrstuvwxyz";
        var special = "#$%&'()*+,-./:;<=>?@[]^_`{|}~";
        var nums = "0123456789";
        var allCharacters = upperCase + lowerCase + special + nums;

        var length = 8;
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(allCharacters.length());
            char randomChar = allCharacters.charAt(randomIndex);
            password.append(randomChar);
        }

        return password.toString();
    }

    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
