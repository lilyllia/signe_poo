package br.com.signe.schedule;

import br.com.signe.employee.domain.Specialist;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_schedule")
@Getter
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "specialist_id", nullable = false)
    private Specialist specialist;

    @Column(nullable = false)
    private LocalDate date;

    // é melhor usar set do que list pra evitar dados duplicados
    @JsonIgnore
    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Scheduling> schedulings = new ArrayList<>();

    protected Schedule() {}

    public Schedule(Specialist specialist, LocalDate date) {
        this.specialist = specialist;
        this.date = date;
    }

    // métodos de negócio

    public void addScheduling(Scheduling scheduling) {
        if (isAvailable(scheduling.getStart(), scheduling.getFinish())) {
            this.schedulings.add(scheduling);
            scheduling.setSchedule(this);
        } else {
            throw new IllegalArgumentException("O horário solicitado não está disponível.");
        }
    }

    public void removeScheduling(Scheduling scheduling) {
        this.schedulings.remove(scheduling);
        scheduling.setSchedule(null);
    }

    public boolean isAvailable(LocalTime start, LocalTime finish) {
        return schedulings.stream()
                .filter(s -> s.getStatus() != StatusScheduling.CANCELLED && s.getStatus() != StatusScheduling.MISSED)
                .noneMatch(s ->
                        !(finish.isBefore(s.getStart()) || start.isAfter(s.getFinish()))
                );
    }
}