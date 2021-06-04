package univesp.pi5.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestsJpaRepository extends JpaRepository<RequisicaoEntity, Long> {

    List<RequisicaoEntity> findAllByArduinoStatus(ArduinoStatus arduinoStatus);

}
