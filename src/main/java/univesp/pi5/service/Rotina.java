package univesp.pi5.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import univesp.pi5.controller.RequisicaoDTO;
import univesp.pi5.repository.ArduinoStatus;
import univesp.pi5.repository.RequestsJpaRepository;
import univesp.pi5.repository.RequisicaoEntity;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

@Slf4j
@Component
public class Rotina {

    @Autowired
    private RequestsJpaRepository requestsJpaRepository;

    @Autowired
    private EntityManager entityManager;

    public RequisicaoEntity executa(RequisicaoDTO requisicaoDTO) {
        RequisicaoEntity entity = new RequisicaoEntity();
        entity.setDateTime(LocalDateTime.now());
        entity.setCommand(requisicaoDTO.getCommand());
        entity.setArduinoStatus(ArduinoStatus.WAITING);
        requestsJpaRepository.save(entity);

        if (requisicaoDTO.getCommand() == null || requisicaoDTO.getCommand().equals("0000")) {
            entity.setArduinoStatus(ArduinoStatus.FINISHED);
            entity.setMedidas("Nenhuma medida realizada!");
            requestsJpaRepository.save(entity);
        } else {
            int count = 0;
            while ((entity.getArduinoStatus().compareTo(ArduinoStatus.FINISHED) != 0 &&
                    entity.getArduinoStatus().compareTo(ArduinoStatus.ERROR) != 0) && count <= 200) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.info(e.getMessage());
                }
                entityManager.clear();
                entity = requestsJpaRepository.findById(entity.getId()).orElseThrow(RuntimeException::new);
                count++;
            }
        }
        return entity;
    }
}
