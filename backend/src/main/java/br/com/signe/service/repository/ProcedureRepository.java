package br.com.signe.service.repository;

import br.com.signe.service.domain.Procedure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProcedureRepository extends JpaRepository<Procedure, UUID> {
    //nao precisa de mais nada.
}