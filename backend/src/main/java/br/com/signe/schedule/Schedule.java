package br.com.signe.schedule;

import br.com.signe.employee.Specialist;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

    @ManyToOne
    @JoinColumn(name = "specialist_id", nullable = false)
    private Specialist specialist;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Scheduling> schedulings;

    public Schedule() {
        this.schedulings = new ArrayList<>();
    }
    public Schedule(Specialist specialist) {
        this.specialist = specialist;
        this.schedulings = new ArrayList<>();
    }

    public void addScheduling(br.com.signe.schedule.Scheduling scheduling) {
        if (isAvailable(scheduling.getStart(), scheduling.getFinish())) {
            schedulings.add(scheduling);
        } else {
            throw new IllegalArgumentException("O horário solicitado não está disponível.");
        }
    }

    private boolean isAvailable(LocalTime start, LocalTime finish) {
        for (br.com.signe.schedule.Scheduling s : schedulings) {
            if (!(finish.isBefore(s.getStart()) || start.isAfter(s.getFinish()))) {
                return false;
            }
        }
        return true;
    }
}