package univesp.pi5.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import javax.persistence.EntityNotFoundException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping
@Slf4j
public class CommandResource {

    @Autowired
    private RequestsJpaRepository requestsJpaRepository;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/command")
    public ResponseEntity<RequisicaoEntity> receiveCommand(@RequestParam(name = "command") String command,
                                                           UriComponentsBuilder uriBuilder) {
        RequisicaoEntity entity = new RequisicaoEntity();
        entity.setDateTime(LocalDateTime.now());
        entity.setCommand(command);
        entity.setArduinoStatus(ArduinoStatus.WAITING);

        requestsJpaRepository.save(entity);

        URI uri = uriBuilder.path("/command/{id}").buildAndExpand(entity.getId()).toUri();
        return ResponseEntity.created(uri).body(entity);
    }

    @PostMapping("/response")
    public ResponseEntity<RequisicaoEntity> postResponse(@RequestParam(name = "id") Long id,
                                                         @RequestBody RequisicaoDTO requisicaoDTO,
                                                         UriComponentsBuilder uriBuilder) {
        RequisicaoEntity entity = requestsJpaRepository.findById(id).orElseThrow(EntityNotFoundException::new);
//        Medidas medidas = requisicaoDTO.getMedidas();
//        entity.setVolume1(medidas.getVolume1());
//        entity.setVolume2(medidas.getVolume2());
//        entity.setPeso1(medidas.getPeso1());
//        entity.setPeso2(medidas.getPeso2());

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
