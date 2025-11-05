package org.group10.tradeshift.models;

import lombok.*;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Data
public class JwtRequest {
    private String email;
    private String password;
}
