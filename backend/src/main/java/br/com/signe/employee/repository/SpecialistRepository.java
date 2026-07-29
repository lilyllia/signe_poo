package br.com.signe.employee.repository;

import br.com.signe.employee.domain.Specialist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpecialistRepository extends JpaRepository<Specialist, UUID> {
}