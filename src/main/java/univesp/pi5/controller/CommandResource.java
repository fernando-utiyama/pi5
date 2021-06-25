package univesp.pi5.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import univesp.pi5.repository.ArduinoStatus;
import univesp.pi5.repository.RequestsJpaRepository;
import univesp.pi5.repository.RequisicaoEntity;
import univesp.pi5.service.Rotina;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@CrossOrigin
@RestController
@RequestMapping("/api")
@Slf4j
public class CommandResource {

    @Autowired
    private RequestsJpaRepository requestsJpaRepository;

    @Autowired
    private Rotina rotina;

    @Autowired
    private EntityManager entityManager;

    //Receber o comando String
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/command")
    public ResponseEntity<RequisicaoEntity> receiveCommand(@RequestParam(name = "command") String command,
                                                           UriComponentsBuilder uriBuilder) {
        RequisicaoEntity entity = new RequisicaoEntity();
        entity.setDateTime(LocalDateTime.now());
        entity.setCommand(command);
        entity.setArduinoStatus(ArduinoStatus.WAITING);

        requestsJpaRepository.save(entity);
        log.info("Comando recebido: " + command);

        URI uri = uriBuilder.path("/request/{id}").buildAndExpand(entity.getId()).toUri();
        return ResponseEntity.created(uri).body(entity);
    }

    //Receber o comando JSON
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/request")
    public ResponseEntity<RequisicaoEntity> receiveRequest(@RequestBody RequisicaoDTO requisicaoDTO,
                                                           UriComponentsBuilder uriBuilder) {
        RequisicaoEntity entity = new RequisicaoEntity();
        entity.setDateTime(LocalDateTime.now());
        entity.setCommand(requisicaoDTO.getCommand());
        entity.setArduinoStatus(ArduinoStatus.WAITING);

        if (requisicaoDTO.getCommand() == null || requisicaoDTO.getCommand().equals("0000")) {
            entity.setArduinoStatus(ArduinoStatus.FINISHED);
            entity.setMedidas("Nenhuma medida realizada!");
        }

        requestsJpaRepository.save(entity);
        log.info("Comando recebido: " + requisicaoDTO.getCommand());

        URI uri = uriBuilder.path("/command/{id}").buildAndExpand(entity.getId()).toUri();
        return ResponseEntity.created(uri).body(entity);
    }

    //Atualiza a requisição
    @PostMapping("/response")
    public ResponseEntity<RequisicaoEntity> postResponse(@RequestParam(name = "id") Long id,
                                                         @RequestBody RequisicaoDTO requisicaoDTO,
                                                         UriComponentsBuilder uriBuilder) {
        entityManager.clear();
        RequisicaoEntity entity = requestsJpaRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        entity.setMedidas(requisicaoDTO.getMedidas());
        entity.setArduinoStatus(Enum.valueOf(ArduinoStatus.class, requisicaoDTO.getStatus()));

        requestsJpaRepository.save(entity);
        if (requisicaoDTO.getMedidas() != null) {
            log.info("Medida recebida: " + entity);
        }

        URI uri = uriBuilder.path("/response/{id}").buildAndExpand(entity.getId()).toUri();
        return ResponseEntity.created(uri).body(entity);
    }

    //Envio de List de requisições com status WAITING e ERROR
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/requests")
    public List<RequisicaoEntity> getRequests() {
        List<RequisicaoEntity> requests = requestsJpaRepository.findAllByArduinoStatus(ArduinoStatus.WAITING);
        return Stream.concat(requests.stream(), requestsJpaRepository.findAllByArduinoStatus(ArduinoStatus.ERROR).stream()).collect(Collectors.toList());
    }

    //Envio da requisição pelo id
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/request")
    public RequisicaoEntity getRequest(@RequestParam(name = "id") Long id) {
        entityManager.clear();
        return requestsJpaRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    //Envia a lista de todas as requisições
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/allrequests")
    public List<RequisicaoEntity> getAllRequests() {
        return requestsJpaRepository.findAll();
    }


    //Deleta todas as requisições
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/clear")
    public void clear() {
        requestsJpaRepository.deleteAll();
    }
}
