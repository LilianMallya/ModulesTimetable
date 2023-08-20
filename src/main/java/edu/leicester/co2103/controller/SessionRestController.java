package edu.leicester.co2103.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.util.ArrayBuilders;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import edu.leicester.co2103.domain.Session;
import edu.leicester.co2103.repo.SessionRepository;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestController
@RequestMapping("/sessions")
public class SessionRestController {

    @Autowired
    private SessionRepository sessionRepository;

    // Endpoint #18: Delete all sessions
    @DeleteMapping
    public ResponseEntity<Void> deleteAllSessions() {
        sessionRepository.deleteAll();
        return ResponseEntity.noContent().build();
    }

    // List all sessions, allowing filtering by convenor (convenor ID) and by module (module code)
    @GetMapping("")
    public List<Session> getAllSessions(@RequestParam(value = "convenor", required = false) Long convenorId,
                                        @RequestParam(value = "module", required = false) String moduleCode) {
        if (convenorId != null && moduleCode != null) {
            // Both filters are specified
            return new ArrayList<>();
        } else if (convenorId != null) {
            // Filter by convenor only
            return new ArrayList<>();
        } else if (moduleCode != null) {
            // Filter by module only
            return new ArrayList<>();
        } else {
            // No filters specified, return all sessions
            return (List<Session>) sessionRepository.findAll();
        }
    }

    // Create a new session
    @PostMapping
    public ResponseEntity<Session> createSession(@RequestBody Session session) {
        Session savedSession = sessionRepository.save(session);
        return new ResponseEntity<>(savedSession, HttpStatus.CREATED);
    }

    // Retrieve a specific session
    @GetMapping("/{id}")
    public ResponseEntity<Session> getSession(@PathVariable long id) {
        return sessionRepository.findById(id)
                .map(session -> new ResponseEntity<>(session, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Update a specific session
    @PutMapping("/{id}")
    public ResponseEntity<Session> updateSession(@PathVariable("id") Long id, @RequestBody Session session) {
        Session existingSession = sessionRepository.findById(id).orElse(null);

        if (existingSession == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            existingSession.setTopic(session.getTopic());
            existingSession.setDatetime(session.getDatetime());
            existingSession.setDuration(session.getDuration());
            sessionRepository.save(existingSession);
            return new ResponseEntity<>(existingSession, HttpStatus.OK);
        }
    }

    // Delete a specific session
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable long id) {
        if (!sessionRepository.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        sessionRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound() {
        return "404 Not Found";
    }
}
