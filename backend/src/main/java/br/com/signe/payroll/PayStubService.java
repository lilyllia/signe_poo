package br.com.signe.payroll;

import br.com.signe.employee.domain.Specialist;
import br.com.signe.employee.repository.SpecialistRepository;
import br.com.signe.schedule.Scheduling;
import br.com.signe.schedule.SchedulingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    public PayStub generatePayStub(UUID specialistId, int month, int year) {

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

        // Zera a produtividade acumulada antes de recalcular o período,
        // já que Specialist não tem mais setProductivity(double).
        specialist.resetProductivity();

        for (Scheduling scheduling : completedSchedulings) {
            double cost = scheduling.getProcedure().getCost();

            payStub.addService(scheduling.getProcedure().getName(), cost);
            specialist.addProductivity(cost);
        }

        double totalSalary = specialist.calculateSalary();
        payStub.setTotalSalary(totalSalary);

        payStub.printServices();

        return payStubRepository.save(payStub);
    }

    public List<PayStub> listBySpecialist(UUID specialistId) {
        return payStubRepository.findBySpecialist_Id(specialistId);
    }

    public PayStub findByIdOrThrow(Long id) {
        return payStubRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Folha de pagamento não encontrada."));
    }
}