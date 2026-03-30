package com.example.demo.assign;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/assignments")
@AllArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;

    @GetMapping("/technician/{technicianId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TECHNICIAN')")
    public ResponseEntity<List<Assignment>> getAssignmentsByTechnician(@PathVariable Long technicianId) {
        return ResponseEntity.ok(assignmentService.getAssignmentsByTechnician(technicianId));
    }

    @GetMapping("/demande/{demandeId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    public ResponseEntity<List<Assignment>> getAssignmentsByDemande(@PathVariable Long demandeId) {
        return ResponseEntity.ok(assignmentService.getAssignmentsByDemande(demandeId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Assignment> getAssignmentById(@PathVariable Long id) {
        return assignmentService.getAssignmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/start")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<Assignment> startAssignment(@PathVariable Long id) {
        Assignment assignment = assignmentService.startAssignment(id);
        return ResponseEntity.ok(assignment);
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<Assignment> completeAssignment(
            @PathVariable Long id,
            @RequestParam(required = false) String completionPhoto) {
        Assignment assignment = assignmentService.completeAssignment(id, completionPhoto);
        return ResponseEntity.ok(assignment);
    }

    @PostMapping("/{id}/fail")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<Assignment> failAssignment(
            @PathVariable Long id,
            @RequestParam String reason) {
        Assignment assignment = assignmentService.failAssignment(id, reason);
        return ResponseEntity.ok(assignment);
    }

    @DeleteMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TECHNICIAN')")
    public ResponseEntity<Assignment> cancelAssignment(@PathVariable Long id) {
        Assignment assignment = assignmentService.cancelAssignment(id);
        return ResponseEntity.ok(assignment);
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("assigned", assignmentService.countByStatus(AssignmentStatus.ASSIGNED));
        stats.put("inProgress", assignmentService.countByStatus(AssignmentStatus.IN_PROGRESS));
        stats.put("completed", assignmentService.countByStatus(AssignmentStatus.COMPLETED));
        stats.put("failed", assignmentService.countByStatus(AssignmentStatus.FAILED));
        stats.put("averageCompletionTimeMinutes", assignmentService.getAverageCompletionTime());
        return ResponseEntity.ok(stats);
    }
}