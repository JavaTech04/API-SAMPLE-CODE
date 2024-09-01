package com.javatech.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "tbl_address")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address extends AbstractEntity<Long> {
    @Column(name = "apartment_number")
    private String apartmentNumber;

    @Column(name = "floor")
    private String floor;

    @Column(name = "building")
    private String building;

    @Column(name = "street_number")
    private String streetNumber;

    @Column(name = "street")
    private String street;

    @Column(name = "city")
    private String city;

    @Column(name = "country")
    private String country;

    @Column(name = "address_type")
    private Integer addressType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;
}