package univesp.pi5.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional(timeout = 60000)
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/command")
    public ResponseEntity<RequisicaoEntity> receiveCommand(@RequestParam(name = "command") String command,
                                                           UriComponentsBuilder uriBuilder) {
        RequisicaoDTO requisicaoDTO = new RequisicaoDTO();
        requisicaoDTO.setCommand(command);

        RequisicaoEntity entity = rotina.executa(requisicaoDTO);
        URI uri = uriBuilder.path("/command/{id}").buildAndExpand(entity.getId()).toUri();
        return ResponseEntity.created(uri).body(entity);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/request")
    public ResponseEntity<RequisicaoEntity> receiveRequest(@RequestBody RequisicaoDTO requisicaoDTO,
                                                           UriComponentsBuilder uriBuilder) {
        RequisicaoEntity entity = rotina.executa(requisicaoDTO);
        URI uri = uriBuilder.path("/command/{id}").buildAndExpand(entity.getId()).toUri();
        return ResponseEntity.created(uri).body(entity);
    }

    @PostMapping("/response")
    public ResponseEntity<RequisicaoEntity> postResponse(@RequestParam(name = "id") Long id,
                                                         @RequestBody RequisicaoDTO requisicaoDTO,
                                                         UriComponentsBuilder uriBuilder) {
        entityManager.clear();
        RequisicaoEntity entity = requestsJpaRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        entity.setMedidas(requisicaoDTO.getMedidas());
        entity.setArduinoStatus(Enum.valueOf(ArduinoStatus.class, requisicaoDTO.getStatus()));

        requestsJpaRepository.save(entity);

        URI uri = uriBuilder.path("/response/{id}").buildAndExpand(entity.getId()).toUri();
        return ResponseEntity.created(uri).body(entity);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/requests")
    public List<RequisicaoEntity> getRequests() {
        List<RequisicaoEntity> requests = requestsJpaRepository.findAllByArduinoStatus(ArduinoStatus.WAITING);
        return Stream.concat(requests.stream(), requestsJpaRepository.findAllByArduinoStatus(ArduinoStatus.ERROR).stream()).collect(Collectors.toList());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/allrequests")
    public List<RequisicaoEntity> getAllRequests() {
        return requestsJpaRepository.findAll();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/clear")
    public void clear() {
        requestsJpaRepository.deleteAll();
    }
}
