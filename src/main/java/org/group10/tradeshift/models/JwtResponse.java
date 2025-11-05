package org.group10.tradeshift.models;

import lombok.*;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
@Builder
public class JwtResponse {
    private String jwtToken;
    private String username;
}
