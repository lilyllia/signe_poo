package br.com.signe.payroll;

import br.com.signe.employee.domain.Specialist;
import br.com.signe.employee.SpecialistRepository;
import br.com.signe.schedule.Scheduling;
import br.com.signe.schedule.SchedulingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PayStubService {

    private final PayStubRepository payStubRepository;
    private final SpecialistRepository specialistRepository;
    private final SchedulingRepository schedulingRepository;

    public PayStubService(PayStubRepository payStubRepository,
                          SpecialistRepository specialistRepository,
                          SchedulingRepository schedulingRepository) {
        this.payStubRepository = payStubRepository;
        this.specialistRepository = specialistRepository;
        this.schedulingRepository = schedulingRepository;
    }

    @Transactional
    public PayStub generatePayStub(String specialistId, int month, int year) {

        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new IllegalArgumentException("Especialista não encontrado."));

        Optional<PayStub> existing = payStubRepository
                .findBySpecialist_IdAndReferenceMonthAndReferenceYear(specialistId, month, year);
        if (existing.isPresent()) {
            throw new IllegalStateException("Já existe uma folha de pagamento gerada para esse período.");
        }

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Scheduling> completedSchedulings =
                schedulingRepository.findCompletedBySpecialistAndPeriod(specialistId, start, end);

        PayStub payStub = new PayStub(specialist, month, year);

        for (Scheduling scheduling : completedSchedulings) {
            payStub.addService(
                    scheduling.getProcedure().getName(),
                    scheduling.getProcedure().getCost()
            );
        }

        // aplica a produtividade calculada na regra de comissão do especialista
        specialist.setProductivity(payStub.getProductivity());
        double totalSalary = specialist.calculateSalary();
        payStub.setTotalSalary(totalSalary);

        payStub.printServices();

        return payStubRepository.save(payStub);
    }

    public List<PayStub> listBySpecialist(String specialistId) {
        return payStubRepository.findBySpecialist_Id(specialistId);
    }

    public PayStub findByIdOrThrow(Long id) {
        return payStubRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Folha de pagamento não encontrada."));
    }
}