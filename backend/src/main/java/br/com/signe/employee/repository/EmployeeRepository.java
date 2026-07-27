package br.com.signe.employee.repository;

import br.com.signe.employee.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    // busca por aquele q n estao demitidos ou inativos
    List<Employee> findByActiveTrue();

    // pra checar se o cpf e email dos funcionarios nao sao o mesmo (nao cadastrar a msm pessoa 2 vezes)
    Optional<Employee> findByCpf(String cpf);
    Optional<Employee> findByEmail(String email);
}