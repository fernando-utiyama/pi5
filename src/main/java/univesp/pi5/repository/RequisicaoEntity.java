package univesp.pi5.repository;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Data
@Entity
@Table(name = "REQUISICOES")
public class RequisicaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String command;

    @Column
    private String medidas;

    @Column
    private double volume1;

    @Column
    private double peso1;

    @Column
    private double volume2;

    @Column
    private double peso2;

    @Enumerated(EnumType.ORDINAL)
    private ArduinoStatus arduinoStatus;

}
