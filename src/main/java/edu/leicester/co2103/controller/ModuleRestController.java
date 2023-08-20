package edu.leicester.co2103.controller;

import edu.leicester.co2103.domain.Module;
import edu.leicester.co2103.domain.Session;
import edu.leicester.co2103.repo.ModuleRepository;
import edu.leicester.co2103.repo.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.util.*;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@RestController
@RequestMapping("/modules")
public class ModuleRestController {

    @Autowired
    private ModuleRepository moduleRepository;

    @GetMapping
    public ResponseEntity<List<Module>> listAllModules() {
        List<Module> modules = new ArrayList<>();
        moduleRepository.findAll().forEach(modules::add);

        return ResponseEntity.ok(modules);
    }

    @PostMapping
    public Module createModule(@RequestBody Module module) {
        return moduleRepository.save(module);
    }

    @GetMapping("/{code}")
    public Module getModule(@PathVariable String code) {
        return moduleRepository.findById(code).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Module not found"));
    }

    @PatchMapping("/{code}")
    public Module updateModule(@PathVariable String code, @RequestBody Module moduleUpdates) {
        Module module = moduleRepository.findById(code).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Module not found"));
        module.setTitle(moduleUpdates.getTitle());
        module.setLevel(moduleUpdates.getLevel());
        module.setOptional(moduleUpdates.isOptional());
        return moduleRepository.save(module);
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<?> deleteModule(@PathVariable String code) {
        Module module = moduleRepository.findById(code).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Module not found"));
        moduleRepository.delete(module);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{code}/sessions")
    public List<Session> listSessions(@PathVariable String code) {
        Module module = moduleRepository.findById(code).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Module not found"));
        return module.getSessions();
    }

    @PostMapping("/{code}/sessions")
    public Module createSession(@PathVariable String code, @RequestBody Session session) {
        Module module = moduleRepository.findById(code).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Module not found"));
        module.getSessions().add(session);
        return moduleRepository.save(module);
    }

    @GetMapping("/{code}/sessions/{sessionId}")
    public Session getSession(@PathVariable String code, @PathVariable Long sessionId) {
        Module module = moduleRepository.findById(code).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Module not found"));
        return module.getSessions().stream()
                .filter(session -> Long.valueOf(session.getId()).equals(sessionId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found"));
    }

    @PutMapping("/{code}/sessions/{sessionId}")
    public ResponseEntity<?> updateSession(@PathVariable String code, @PathVariable Long sessionId, @RequestBody Session updatedSession) {
        Optional<Module> optionalModule = moduleRepository.findById(code);
        if (!optionalModule.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Module module = optionalModule.get();
        Optional<Session> optionalSession = module.getSessions().stream().filter(s -> s.getId() == sessionId).findFirst();
        if (!optionalSession.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Session session = optionalSession.get();
        session.setTopic(updatedSession.getTopic());
        session.setDatetime(updatedSession.getDatetime());
        session.setDuration(updatedSession.getDuration());

        moduleRepository.save(module);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{code}/sessions/{sessionId}")
    public ResponseEntity<?> updateSessionPartial(@PathVariable String code, @PathVariable Long sessionId, @RequestBody Map<String, Object> updatedFields) {
        Optional<Module> optionalModule = moduleRepository.findById(code);
        if (!optionalModule.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Module module = optionalModule.get();
        Optional<Session> optionalSession = module.getSessions().stream().filter(s -> s.getId() == sessionId).findFirst();
        if (!optionalSession.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Session session = optionalSession.get();
        updatedFields.forEach((key, value) -> {
            switch (key) {
                case "topic":
                    session.setTopic((String) value);
                    break;
                case "datetime":
                    session.setDatetime(Timestamp.valueOf((String) value));
                    break;
                case "duration":
                    session.setDuration((Integer) value);
                    break;
            }
        });

        moduleRepository.save(module);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{code}/sessions/{sessionId}")
    public ResponseEntity<?> deleteSession(@PathVariable String code, @PathVariable Long sessionId) {
        Optional<Module> optionalModule = moduleRepository.findById(code);
        if (!optionalModule.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Module module = optionalModule.get();
        Optional<Session> optionalSession = module.getSessions().stream().filter(s -> s.getId() == sessionId).findFirst();
        if (!optionalSession.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Session session = optionalSession.get();
        module.getSessions().remove(session);
        moduleRepository.save(module);

        return ResponseEntity.noContent().build();
    }
}

