package br.com.signe.schedule;

import br.com.signe.client.domain.Client;
import br.com.signe.client.repository.ClientRepository;
import br.com.signe.employee.domain.Specialist;
import br.com.signe.employee.repository.EmployeeRepository;
import br.com.signe.service.domain.Procedure;
import br.com.signe.service.repository.ProcedureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
public class SchedulingService {

    private final ScheduleRepository scheduleRepository;
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final ProcedureRepository procedureRepository;

    public SchedulingService(ScheduleRepository scheduleRepository, ClientRepository clientRepository, EmployeeRepository employeeRepository, ProcedureRepository procedureRepository) {
        this.scheduleRepository = scheduleRepository;
        this.clientRepository = clientRepository;
        this.employeeRepository = employeeRepository;
        this.procedureRepository = procedureRepository;
    }

    // --- NEW: FETCH DAILY SCHEDULE ---
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getDailySchedule(UUID specialistId, LocalDate date) {
        // If there's a schedule, map its appointments to our safe DTO!
        return scheduleRepository.findBySpecialistIdAndDate(specialistId, date)
                .map(schedule -> schedule.getSchedulings().stream()
                        .map(this::toResponse)
                        .toList()
                )
                .orElse(List.of());
    }

    @Transactional
    public AppointmentResponse bookAppointment(UUID clientId, UUID specialistId, UUID procedureId, LocalDate date, LocalTime start) {

        // 1. Fetch the Client, Specialist, and Procedure
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));

        Specialist specialist = (Specialist) employeeRepository.findById(specialistId)
                .orElseThrow(() -> new IllegalArgumentException("Especialista não encontrado."));

        Procedure procedure = procedureRepository.findById(procedureId)
                .orElseThrow(() -> new IllegalArgumentException("Procedimento não encontrado."));

        // 2. Calculate the exact finish time based on the procedure's duration
        LocalTime finish = start.plusMinutes(procedure.getAverageDuration());

        // 3. Verify if the specialist actually works during these hours
        if (!specialist.isWithinWorkingHours(start, finish)) {
            throw new IllegalArgumentException("O horário solicitado (" + start + " às " + finish + ") está fora do expediente do especialista.");
        }

        // 4. Find the Specialist's schedule for this specific day, OR create a new one if it's their first appointment of the day!
        Schedule dailySchedule = scheduleRepository.findBySpecialistIdAndDate(specialist.getId(), date)
                .orElseGet(() -> new Schedule(specialist, date));

        // 5. Create the appointment
        Scheduling newAppointment = new Scheduling(client, specialist, procedure, start, finish);

        // 6. Attempt to add it to the daily schedule.
        dailySchedule.addScheduling(newAppointment);

        // 7. Save the daily schedule
        scheduleRepository.save(dailySchedule);

        return toResponse(newAppointment);
    }

    // --- HELPER METHOD TO MAP ENTITY TO DTO ---
    private AppointmentResponse toResponse(Scheduling s) {
        return new AppointmentResponse(
                s.getId(),
                s.getStart().toString(),
                s.getFinish().toString(),
                s.getStatus().name(),
                new AppointmentResponse.ClientSummary(s.getClient().getId(), s.getClient().getFirstName(), s.getClient().getLastName()),
                new AppointmentResponse.ProcedureSummary(s.getProcedure().getId(), s.getProcedure().getName())
        );
    }
}