package br.com.signe.schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SchedulingRepository extends JpaRepository<Scheduling, Long> {

    @Query("SELECT s FROM Scheduling s WHERE s.specialist.id = :specialistId " +
            "AND s.status = br.com.signe.schedule.StatusScheduling.COMPLETED " +
            "AND s.schedule.date BETWEEN :start AND :end")
    List<Scheduling> findCompletedBySpecialistAndPeriod(
            @Param("specialistId") java.util.UUID specialistId,
            @Param("start") java.time.LocalDate start,
            @Param("end") java.time.LocalDate end);
}
