package br.com.signe.schedule;

import br.com.signe.client.domain.Client;
import br.com.signe.client.repository.ClientRepository;
import br.com.signe.employee.domain.Specialist;
import br.com.signe.employee.repository.EmployeeRepository;
import br.com.signe.finance.PaymentRepository;
import br.com.signe.finance.PaymentStatus;
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
    private final SchedulingRepository schedulingRepository;
    private final PaymentRepository paymentRepository;

    public SchedulingService(ScheduleRepository scheduleRepository, ClientRepository clientRepository, EmployeeRepository employeeRepository, ProcedureRepository procedureRepository, SchedulingRepository schedulingRepository, PaymentRepository paymentRepository) {
        this.scheduleRepository = scheduleRepository;
        this.clientRepository = clientRepository;
        this.employeeRepository = employeeRepository;
        this.procedureRepository = procedureRepository;
        this.schedulingRepository = schedulingRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getDailySchedule(UUID specialistId, LocalDate date) {
        return scheduleRepository.findBySpecialistIdAndDate(specialistId, date)
                .map(schedule -> schedule.getSchedulings().stream()
                        .map(this::toResponse)
                        .toList()
                )
                .orElse(List.of());
    }

    @Transactional
    public AppointmentResponse bookAppointment(UUID clientId, UUID specialistId, UUID procedureId, LocalDate date, LocalTime start) {

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));

        Specialist specialist = (Specialist) employeeRepository.findById(specialistId)
                .orElseThrow(() -> new IllegalArgumentException("Especialista não encontrado."));

        Procedure procedure = procedureRepository.findById(procedureId)
                .orElseThrow(() -> new IllegalArgumentException("Procedimento não encontrado."));

        LocalTime finish = start.plusMinutes(procedure.getAverageDuration());

        if (!specialist.isWithinWorkingHours(start, finish)) {
            throw new IllegalArgumentException("O horário solicitado (" + start + " às " + finish + ") está fora do expediente do especialista.");
        }

        Schedule dailySchedule = scheduleRepository.findBySpecialistIdAndDate(specialist.getId(), date)
                .orElseGet(() -> new Schedule(specialist, date));

        Scheduling newAppointment = new Scheduling(client, specialist, procedure, start, finish);

        dailySchedule.addScheduling(newAppointment);

        scheduleRepository.save(dailySchedule);

        return toResponse(newAppointment);
    }

    @Transactional
    public void updateSchedulingStatus(UUID schedulingId, String action) {
        Scheduling scheduling = schedulingRepository.findById(schedulingId)
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado."));

        switch (action.toLowerCase()) {
            case "confirm":
                scheduling.confirmScheduling();
                break;
            case "cancel":
                scheduling.cancelScheduling();
                break;
            case "complete":
                scheduling.completedService();
                break;
            case "miss":
                scheduling.missScheduling();
                break;
            default:
                throw new IllegalArgumentException("Ação de status inválida.");
        }

        schedulingRepository.save(scheduling);
    }

    private AppointmentResponse toResponse(Scheduling s) {
        boolean isPaid = paymentRepository.findBySchedulingId(s.getId())
                .map(payment -> payment.getStatus() == PaymentStatus.COMPLETED)
                .orElse(false);

        return new AppointmentResponse(
                s.getId(),
                s.getStart().toString(),
                s.getFinish().toString(),
                s.getStatus().name(),
                isPaid, // <-- INJECTED HERE
                new AppointmentResponse.ClientSummary(s.getClient().getId(), s.getClient().getFirstName(), s.getClient().getLastName()),
                new AppointmentResponse.ProcedureSummary(s.getProcedure().getId(), s.getProcedure().getName())
        );
    }
}