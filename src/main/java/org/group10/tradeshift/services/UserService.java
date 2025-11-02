package org.group10.tradeshift.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.group10.tradeshift.entities.User;
import org.springframework.stereotype.Service;

@Service
@Getter
@ToString
@Setter
@AllArgsConstructor
public class UserService {
    public int insertUser(User user)
    {
        return 0;
    }

}
