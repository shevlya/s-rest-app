package ru.ssau.s_rest_app.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserInterestId implements Serializable {
    private Long idUser;
    private Long idEventCategory;
}
