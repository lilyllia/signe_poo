package br.com.signe.payroll;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PayStubRepository extends JpaRepository<PayStub, Long> {

    List<PayStub> findBySpecialist_Id(UUID specialistId);

    Optional<PayStub> findBySpecialist_IdAndReferenceMonthAndReferenceYear(
            UUID specialistId, int referenceMonth, int referenceYear);
}