package br.com.signe.schedule;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/schedulings")
public class SchedulingController {

    private final SchedulingService schedulingService;

    public SchedulingController(SchedulingService schedulingService) {
        this.schedulingService = schedulingService;
    }


    // 1. create - novo agendamento
    @PostMapping
    public ResponseEntity<AppointmentResponse> bookAppointment(@RequestBody BookAppointmentRequest request) {
        AppointmentResponse newAppointment = schedulingService.bookAppointment(
                request.clientId(),
                request.specialistId(),
                request.procedureId(),
                request.date(),
                request.start()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(newAppointment);
    }

    // 2. read - listar os agendamentos do especialista
    @GetMapping("/daily")
    public ResponseEntity<List<AppointmentResponse>> getDailySchedule(
            @RequestParam UUID specialistId,
            @RequestParam LocalDate date) {

        List<AppointmentResponse> schedulings = schedulingService.getDailySchedule(specialistId, date);
        return ResponseEntity.ok(schedulings);
    }

    // 3. update - atualizar status
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable UUID id, @RequestParam String action) {
        schedulingService.updateSchedulingStatus(id, action);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequests(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}

record BookAppointmentRequest(
        UUID clientId,
        UUID specialistId,
        UUID procedureId,
        LocalDate date,
        LocalTime start
) {}