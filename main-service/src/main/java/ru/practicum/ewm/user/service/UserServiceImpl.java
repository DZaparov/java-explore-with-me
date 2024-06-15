package ru.practicum.ewm.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.UserRepository;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.UserMapper;
import ru.practicum.ewm.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> listUsers(List<Long> ids, int from, int size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        Page<User> users;

        if (ids == null) {
            users = userRepository.findAll(page);
        } else {
            users = userRepository.findAllByIdIn(ids, page);
        }

        return users
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(NewUserRequest newUserRequest) {
        User user = UserMapper.toUser(newUserRequest);
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ConflictException("Email is not unique");
        }
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long userId) {
        applyChecks(userId);
        userRepository.deleteById(userId);
    }

    private void applyChecks(Long userId) {
        if (userId != null) {
            User user = userRepository.findById(userId).orElseThrow(() ->
                    new NotFoundException("User with id=" + userId + " was not found"));
        }
    }
}
