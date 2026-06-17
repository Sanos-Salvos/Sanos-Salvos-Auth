package com.sanos.auth.factory;

import com.sanos.auth.dto.UserDTO;
import com.sanos.auth.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserFactoryImpl implements IUserFactory {

    @Override
    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRoles()
        );
    }
}