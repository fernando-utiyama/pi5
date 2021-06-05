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
import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "REQUISICOES")
public class RequisicaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private LocalDateTime dateTime;

    @Column
    private String command;

    @Column
    private String medidas;

    @Enumerated(EnumType.ORDINAL)
    private ArduinoStatus arduinoStatus;

}
