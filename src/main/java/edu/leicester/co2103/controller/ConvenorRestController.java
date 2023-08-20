package edu.leicester.co2103.controller;

import edu.leicester.co2103.domain.Convenor;
import edu.leicester.co2103.domain.Module;
import edu.leicester.co2103.domain.Position;
import edu.leicester.co2103.repo.ConvenorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/convenors")
public class ConvenorRestController {

    private final ConvenorRepository convenorRepository;

    @Autowired
    public ConvenorRestController(ConvenorRepository convenorRepository) {
        this.convenorRepository = convenorRepository;
    }

    // Endpoint 1: List all convenors
    @GetMapping
    public ResponseEntity<List<Convenor>> getAllConvenors() {
        List<Convenor> convenors = new ArrayList<>();
        convenorRepository.findAll().forEach(convenors::add);

        return ResponseEntity.ok(convenors);
    }

    // Endpoint 2: Retrieve a specific convenor by id
    @GetMapping("/{id}")
    public ResponseEntity<Convenor> getConvenorById(@PathVariable("id") long id) {
        Convenor convenor = convenorRepository.findById(id).orElse(null);
        if (convenor == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convenor);
    }

    // Endpoint 3: Create a new convenor
    @PostMapping
    public ResponseEntity<Convenor> createConvenor(@RequestBody Convenor convenor) {
        Convenor savedConvenor = convenorRepository.save(convenor);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedConvenor);
    }

    // Endpoint 4: Update a specific convenor by id
    @PutMapping("/{id}")
    public ResponseEntity<Convenor> updateConvenor(@PathVariable("id") long id, @RequestBody Convenor convenor) {
        Convenor existingConvenor = convenorRepository.findById(id).orElse(null);
        if (existingConvenor == null) {
            return ResponseEntity.notFound().build();
        }
        existingConvenor.setName(convenor.getName());
        existingConvenor.setPosition(convenor.getPosition());
        existingConvenor.setModules(convenor.getModules());
        Convenor updatedConvenor = convenorRepository.save(existingConvenor);
        return ResponseEntity.ok(updatedConvenor);
    }

    // Endpoint 5: Delete a specific convenor by id
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteConvenor(@PathVariable("id") long id) {
        Convenor existingConvenor = convenorRepository.findById(id).orElse(null);
        if (existingConvenor == null) {
            return ResponseEntity.notFound().build();
        }
        convenorRepository.delete(existingConvenor);
        return ResponseEntity.noContent().build();
    }

    // Endpoint 6: List all modules taught by a convenor
    @GetMapping("/{id}/modules")
    public ResponseEntity<List<Module>> getAllModulesTaughtByConvenor(@PathVariable("id") long id) {
        Convenor convenor = convenorRepository.findById(id).orElse(null);
        if (convenor == null) {
            return ResponseEntity.notFound().build();
        }
        List<Module> modules = convenor.getModules();
        return ResponseEntity.ok(modules);
    }
}
