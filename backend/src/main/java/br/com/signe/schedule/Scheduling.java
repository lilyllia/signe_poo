package br.com.signe.schedule;

import br.com.signe.client.domain.Client;
import br.com.signe.service.domain.Procedure;
import br.com.signe.employee.domain.Specialist;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tb_scheduling")
@Getter
public class Scheduling {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialist_id", nullable = false)
    private Specialist specialist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "procedure_id", nullable = false)
    private Procedure procedure;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @Column(name = "start_time", nullable = false)
    private LocalTime start;

    @Column(name = "finish_time", nullable = false)
    private LocalTime finish;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusScheduling status;

    protected Scheduling() {}

    public Scheduling(Client client, Specialist specialist, Procedure procedure, LocalTime start, LocalTime finish) {
        this.client = Objects.requireNonNull(client, "Cliente é obrigatório.");
        this.specialist = Objects.requireNonNull(specialist, "Especialista é obrigatório.");
        this.procedure = Objects.requireNonNull(procedure, "Procedimento é obrigatório.");
        this.start = Objects.requireNonNull(start, "Data inicial é obrigatória.");
        this.finish = Objects.requireNonNull(finish, "Data final é obrigatória.");

        if (!finish.isAfter(start)) {
            throw new IllegalArgumentException("O horário final deve ser posterior ao horário inicial.");
        }
        this.status = StatusScheduling.SCHEDULED;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public void confirmScheduling() {
        if(status != StatusScheduling.SCHEDULED) {
            throw new IllegalStateException("Só é possivel confirmar horários AGENDADOS.");
        }
        this.status = StatusScheduling.CONFIRMED;
    }

    public void cancelScheduling() {
        if(status != StatusScheduling.SCHEDULED && status != StatusScheduling.CONFIRMED) {
            throw new IllegalStateException("Só é possivel cancelar horários AGENDADOS ou CONFIRMADOS.");
        }
        this.status = StatusScheduling.CANCELLED;
    }

    public void completedService() {
        if(status != StatusScheduling.CONFIRMED) {
            throw new IllegalStateException("Só é possivel completar serviços de horários CONFIRMADOS.");
        }
        this.status = StatusScheduling.COMPLETED;
    }

    public void missScheduling() {
        if(status != StatusScheduling.CONFIRMED) {
            throw new IllegalStateException("Somente horários confirmados podem ser marcados como falta.");
        }
        this.status = StatusScheduling.MISSED;
    }
}