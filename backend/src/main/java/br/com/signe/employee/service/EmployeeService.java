package br.com.signe.employee.service;

import br.com.signe.employee.domain.Administrator;
import br.com.signe.employee.domain.Employee;
import br.com.signe.employee.domain.Specialist;
import br.com.signe.employee.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> getAllActiveEmployees() {
        return employeeRepository.findByActiveTrue();
    }

    @Transactional
    public Administrator registerAdministrator(Administrator admin) {
        checkDuplicates(admin.getCpf(), admin.getEmail());
        return employeeRepository.save(admin);
    }

    @Transactional
    public Specialist registerSpecialist(Specialist specialist) {
        checkDuplicates(specialist.getCpf(), specialist.getEmail());
        return employeeRepository.save(specialist);
    }

    @Transactional
    public Employee updateEmployeeProfile(UUID id, String name, String phone, String address) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado."));

        employee.updateProfile(name, phone, address);
        return employeeRepository.save(employee);
    }

    @Transactional
    public Administrator updateAdminContract(UUID id, double baseSalary) {
        Administrator admin = (Administrator) employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Admin não encontrado."));

        admin.updateSalary(baseSalary);
        return employeeRepository.save(admin);
    }

    @Transactional
    public Specialist updateSpecialistContract(UUID id, double baseSalary, double commission, LocalTime start, LocalTime finish, java.util.Set<br.com.signe.employee.domain.Specialization> specializations) {
        Specialist spec = (Specialist) employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Especialista não encontrado."));

        spec.updateSalary(baseSalary);
        spec.updateCommission(commission);
        spec.updateSchedule(start, finish);
        spec.updateSpecializations(specializations);

        return employeeRepository.save(spec);
    }

    @Transactional
    public void deactivateEmployee(UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado."));

        employee.deactivate();
        employeeRepository.save(employee);
    }

    private void checkDuplicates(String cpf, String email) {
        if (employeeRepository.findByCpf(cpf).isPresent()) {
            throw new IllegalArgumentException("Já existe um funcionário cadastrado com este CPF.");
        }
        if (employeeRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Já existe um funcionário cadastrado com este Email.");
        }
    }
}