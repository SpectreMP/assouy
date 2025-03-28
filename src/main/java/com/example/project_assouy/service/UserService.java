package com.example.project_assouy.service;

import com.example.project_assouy.dto.UserRegistrationDto;

import com.example.project_assouy.entity.User;
import com.example.project_assouy.entity.UserPersonal;
import com.example.project_assouy.repo.UserPersonalRepository;
import com.example.project_assouy.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserPersonalRepository userPersonalRepository;

    public UserPersonal registerUser(UserRegistrationDto dto) {
        // Пример проверки: можно добавить проверку на существование пользователя по логину, email или номеру телефона
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        User user = new User();
        // Присваиваем поля из DTO в сущность User
        user.setCreateTs(LocalDateTime.now());
        user.setCreatedBy("admin");
        user.setIsActive(true);
        user.setPhoneNumber(dto.getUserIdPhoneNumber());
        user.setLogin(dto.getUserIdLogin());
        // Шифруем пароль перед сохранением
        user.setPasswordHash(passwordEncoder.encode(dto.getUserIdPasswordHash()));
        user.setRole(dto.getUserIdRole());

        UserPersonal userPersonal = new UserPersonal();
        userPersonal.setUserId(user);
        userPersonal.setFullName(dto.getFullName());
        userPersonal.setBirthDate(dto.getBirthDate());
        userPersonal.setEmail(dto.getEmail());
        userPersonal.setRegion(dto.getRegion());
        userPersonal.setCreateTs(LocalDateTime.now());
        userPersonal.setCreatedBy("admin");
        userRepository.save(user);

        // Сохраняем пользователя в репозитории
        return userPersonalRepository.save(userPersonal);
    }
    public UserPersonal loginUser(String login, String password) {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        Optional<User> userOptional = userRepository.findByLogin(login);
        if (userOptional.isEmpty()) {
            throw new BadCredentialsException("Неверный логин или пароль");
        }

        User user = userOptional.get();

        // Проверяем активность пользователя
        if (!user.getIsActive()) {
            throw new IllegalStateException("Пользователь неактивен");
        }

        // Проверяем пароль
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new BadCredentialsException("Неверный логин или пароль");
        }

        // Возвращаем персональные данные пользователя
        return userPersonalRepository.findByUserId(user)
                .orElseThrow(() -> new IllegalStateException("Персональные данные пользователя не найдены"));
    }
}
