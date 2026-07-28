package br.com.signe.payroll;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayStubRepository extends JpaRepository<PayStub, Long> {

    List<PayStub> findBySpecialist_Id(String specialistId);

    Optional<PayStub> findBySpecialist_IdAndReferenceMonthAndReferenceYear(
            String specialistId, int referenceMonth, int referenceYear);
}