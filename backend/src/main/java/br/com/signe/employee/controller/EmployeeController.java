package br.com.signe.employee.controller;

import br.com.signe.employee.domain.Administrator;
import br.com.signe.employee.domain.Employee;
import br.com.signe.employee.domain.EmployeeStatus;
import br.com.signe.employee.domain.Specialist;
import br.com.signe.employee.domain.Specialization;
import br.com.signe.employee.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }


    // create
    @PostMapping("/admin")
    public ResponseEntity<Administrator> createAdmin(@RequestBody CreateAdminRequest request) {
        Administrator admin = new Administrator(
                request.name(),
                request.cpf(),
                request.email(),
                request.phone(),
                request.address(),
                request.baseSalary(),
                EmployeeStatus.ACTIVE
        );
        Administrator savedAdmin = employeeService.registerAdministrator(admin);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAdmin);
    }

    @PostMapping("/specialist")
    public ResponseEntity<Specialist> createSpecialist(@RequestBody CreateSpecialistRequest request) {
        Specialist specialist = new Specialist(
                request.name(),
                request.cpf(),
                request.email(),
                request.phone(),
                request.address(),
                request.baseSalary(),
                EmployeeStatus.ACTIVE,
                request.commissionPercentage(),
                request.start(),
                request.finish(),
                request.specializations()
        );
        Specialist savedSpecialist = employeeService.registerSpecialist(specialist);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSpecialist);
    }

    // read
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllActiveEmployees());
    }


    // update (falta fazer...)
    @PutMapping("/{id}/profile")
    public ResponseEntity<Employee> updateProfile(@PathVariable UUID id, @RequestBody UpdateProfileRequest request) {
        Employee updated = employeeService.updateEmployeeProfile(id, request.name(), request.phone(), request.address());
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/admin/{id}/contract")
    public ResponseEntity<Administrator> updateAdminContract(@PathVariable UUID id, @RequestBody UpdateAdminContractRequest request) {
        Administrator updated = employeeService.updateAdminContract(id, request.baseSalary());
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/specialist/{id}/contract")
    public ResponseEntity<Specialist> updateSpecialistContract(@PathVariable UUID id, @RequestBody UpdateSpecialistContractRequest request) {
        Specialist updated = employeeService.updateSpecialistContract(
                id, request.baseSalary(), request.commissionPercentage(),
                request.start(), request.finish(), request.specializations()
        );
        return ResponseEntity.ok(updated);
    }

    // delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable UUID id) {
        employeeService.deactivateEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequests(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}

// dtos
record CreateAdminRequest(
        String name, String cpf, String email, String phone, String address, double baseSalary
) {}

record CreateSpecialistRequest(
        String name, String cpf, String email, String phone, String address, double baseSalary,
        double commissionPercentage, LocalTime start, LocalTime finish, Set<Specialization> specializations
) {}

record UpdateProfileRequest(String name, String phone, String address) {}

record UpdateAdminContractRequest(double baseSalary) {}

record UpdateSpecialistContractRequest(
        double baseSalary, double commissionPercentage, LocalTime start, LocalTime finish, Set<Specialization> specializations
) {}