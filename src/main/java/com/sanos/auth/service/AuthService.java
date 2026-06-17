package com.sanos.auth.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sanos.auth.model.User;
import com.sanos.auth.dto.UserDTO;
import com.sanos.auth.factory.IUserFactory;
import com.sanos.auth.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final IUserFactory userFactory;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, IUserFactory userFactory) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userFactory = userFactory;
    }

    public boolean authenticate(String username, String password) {
        Optional<User> userOpt = Optional.ofNullable(userRepository.findByUsername(username));
        return userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword());
    }

    public User register(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("The username '" + user.getUsername() + "' is already taken.");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("The email '" + user.getEmail() + "' is already registered.");
        }

        Set<String> processedRoles = new HashSet<>();

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            processedRoles.add("ROLE_USER");
        } else {
            for (String role : user.getRoles()) {
                String cleanRole = role.trim().toUpperCase().replace("ROLE_", "");

                if (cleanRole.equals("ORGANIZACION")) {
                    processedRoles.add("ROLE_ORGANIZACION");
                } else {
                    processedRoles.add("ROLE_USER");
                }
            }
        }
        user.setRoles(processedRoles);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userFactory::toDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        return userFactory.toDTO(user);
    }

    public UserDTO updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());

        if (userDetails.getRoles() != null && !userDetails.getRoles().isEmpty()) {
            Set<String> updatedRoles = new HashSet<>();
            for (String r : userDetails.getRoles()) {
                String cleanR = r.trim().toUpperCase().replace("ROLE_", "");
                if (cleanR.equals("ORGANIZACION")) {
                    updatedRoles.add("ROLE_ORGANIZACION");
                } else {
                    updatedRoles.add("ROLE_USER");
                }
            }
            user.setRoles(updatedRoles);
        }

        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }

        User usuarioActualizado = userRepository.save(user);
        return userFactory.toDTO(usuarioActualizado);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        userRepository.deleteById(id);
    }
}