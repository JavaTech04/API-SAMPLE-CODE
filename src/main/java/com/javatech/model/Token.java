package com.javatech.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "token")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Token extends AbstractEntity<Integer> {

    @NotNull
    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "refresh_token")
    private String refreshToken;
}