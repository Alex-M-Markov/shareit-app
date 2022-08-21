package ru.practicum.shareit.user;

import java.util.Collection;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserServiceImpl userServiceImpl;


    @PostMapping
    public UserDto create(@RequestBody UserDto user) {
        return userServiceImpl.create(user);
    }

    @PatchMapping(path = "/{id}")
    public UserDto update(@PathVariable Long id, @RequestBody UserDto user) {
        return userServiceImpl.update(id, user);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable Long id) {
        userServiceImpl.deleteUserById(id);
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        return userServiceImpl.getUserById(id);
    }

    @GetMapping
    public Collection<UserDto> getAllUsers() {
        return userServiceImpl.getAllUsers();
    }

}