package ru.practicum.ewm.user.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/admin/users")
@Slf4j
@AllArgsConstructor
public class UserAdminController {
    public final UserService userService;

    @GetMapping
    public List<UserDto> listUsers(@RequestParam(required = false) List<Long> ids,
                                   @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                   @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Попытка получения всех пользователей ids={}, from={}, size={}", ids, from, size);
        List<UserDto> result = userService.listUsers(ids, from, size);
        log.info("Получен список пользователей: {}", result);

        return result;
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED) //201
    public UserDto createUser(@Valid @RequestBody NewUserRequest newUserRequest) {
        log.info("Попытка создания пользователя {}", newUserRequest);
        UserDto result = userService.createUser(newUserRequest);
        log.info("Создан пользователь: {}", result);

        return result;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT) //204
    public void deleteUser(@PathVariable Long userId) {
        log.info("Попытка удалить пользователя с id={}", userId);
        userService.deleteUser(userId);
        log.info("Удален пользователь с id={}", userId);
    }
}
