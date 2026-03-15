package ru.ssau.s_rest_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CityRoute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCityRoute;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_admin")
    private User admin;

    @Column(length = 256, nullable = false)
    private String cityRouteName;

    @Column(columnDefinition = "TEXT")
    private String cityRouteDescription;

    @Column(nullable = false)
    private Boolean published = false;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;
}
