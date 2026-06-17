package com.sanos.auth.factory;

import com.sanos.auth.dto.UserDTO;
import com.sanos.auth.model.User;

public interface IUserFactory {
    UserDTO toDTO(User user);
}