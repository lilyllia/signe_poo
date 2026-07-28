package br.com.signe.schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SchedulingRepository extends JpaRepository<Scheduling, UUID> {

    @Query("SELECT s FROM Scheduling s WHERE s.specialist.id = :specialistId " +
            "AND s.status = :status " +
            "AND s.date BETWEEN :start AND :end")
    List<Scheduling> findBySpecialistAndStatusAndPeriod(
            @Param("specialistId") String specialistId,
            @Param("status") StatusScheduling status,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);
}