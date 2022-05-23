package com.example.kamikaze.database.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "boarding_passes")
public class BoardingPass {
    @Id
    @Column(name = "ticket_no")
    private String ticketNo;

    @Column(name = "flight_id")
    private Integer flightId;

    @Column(name = "boarding_no")
    private Integer BoardingNo;

    @Column(name = "seat_no")
    private String seatNo;

}