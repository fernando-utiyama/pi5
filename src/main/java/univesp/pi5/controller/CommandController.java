package univesp.pi5.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import univesp.pi5.repository.RequestsJpaRepository;
import univesp.pi5.repository.RequisicaoEntity;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping
@Slf4j
public class CommandController {

    @Autowired
    RequestsJpaRepository requestsJpaRepository;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/command")
    public ResponseEntity<RequisicaoEntity> receiveCommand(@RequestParam(name = "command") String command,
                                                           UriComponentsBuilder uriBuilder) {
        RequisicaoEntity entity = new RequisicaoEntity();
        entity.setCommand(command);

        requestsJpaRepository.save(entity);

        URI uri = uriBuilder.path("/command/{id}").buildAndExpand(entity.getId()).toUri();
        return ResponseEntity.created(uri).body(entity);
    }

    @GetMapping("/response")
    public ResponseEntity<RequisicaoEntity> postResponse(@RequestParam(name = "id") Long id,
                                                         @RequestParam(name = "peso") Float response,
                                                         UriComponentsBuilder uriBuilder) {
        RequisicaoEntity entity = requestsJpaRepository.findById(id).orElseThrow(RuntimeException::new);
        entity.setPeso(response);

        requestsJpaRepository.save(entity);

        URI uri = uriBuilder.path("/response/{id}").buildAndExpand(entity.getId()).toUri();
        return ResponseEntity.created(uri).body(entity);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/requests")
    public List<RequisicaoEntity> getRequests() {
        return requestsJpaRepository.findAll();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/clear")
    public void clear() {
        requestsJpaRepository.deleteAll();
    }
}
