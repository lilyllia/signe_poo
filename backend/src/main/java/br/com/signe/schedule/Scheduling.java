package br.com.signe.schedule;

import br.com.signe.client.domain.Client;
import br.com.signe.service.Procedure;
import br.com.signe.employee.Specialist;
import jakarta.persistence.*;


import java.time.LocalTime;
import java.util.Objects;

@Entity
public class Scheduling {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long schedulingId;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "specialist_id", nullable = false)
    private Specialist specialist;

    @ManyToOne
    @JoinColumn(name = "procedure_id", nullable = false)
    private Procedure procedure;

    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @Column(nullable = false)
    private LocalTime start;

    @Column(nullable = false)
    private LocalTime finish;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusScheduling status;

    public Scheduling() {}

        public Scheduling(Client client, Specialist specialist, Procedure procedure, LocalTime start, LocalTime finish) {
        this.client = Objects.requireNonNull(client, "Cliente é obrigatório.");
        this.specialist = Objects.requireNonNull(specialist, "Especialista é obrigatório.");
        this.procedure = Objects.requireNonNull(procedure, "Procedimento é obrigatório.");
        this.start = Objects.requireNonNull(start, "Data inicial é obrigatória.");
        this.finish = Objects.requireNonNull(finish, "Data final é obrigatória.");

        if (!finish.isAfter(start)) {
            throw new IllegalArgumentException(
                    "O horário final deve ser posterior ao horário inicial.");
        }
        this.status = br.com.signe.schedule.StatusScheduling.SCHEDULED;
    }


    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Specialist getSpecialist() {
        return specialist;
    }

    public void setSpecialist(Specialist specialist) {
        this.specialist = specialist;
    }

    public Procedure getProcedure() {
        return procedure;
    }

    public void setProcedure(Procedure procedure) {
        this.procedure = procedure;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public LocalTime getStart() {
        return start;
    }

    public void setStart(LocalTime start) {
        this.start = start;
    }

    public LocalTime getFinish() {
        return finish;
    }

    public void setFinish(LocalTime finish) {
        this.finish = finish;
    }

    public StatusScheduling getStatus() {
        return status;
    }

    public void setStatus(StatusScheduling status) {
        this.status = status;
    }

    public void confirmScheduling(){
        if(status != br.com.signe.schedule.StatusScheduling.SCHEDULED){
            throw new IllegalStateException("Só é possivel confirmar horários AGENDADOS.");
        }
        status = br.com.signe.schedule.StatusScheduling.CONFIRMED;
    }

    public void cancelScheduling(){
        if(status != br.com.signe.schedule.StatusScheduling.SCHEDULED && status != br.com.signe.schedule.StatusScheduling.CONFIRMED){
            throw new IllegalStateException("Só é possivel cancelar horários AGENDADOS ou CONFIRMADOS.");
        }
        status = br.com.signe.schedule.StatusScheduling.CANCELLED;
    }

    public void completedService(){
        if(status != br.com.signe.schedule.StatusScheduling.CONFIRMED){
            throw new IllegalStateException("Só é possivel completar serviços de horários CONFIRMADOS.");
        }
        status = br.com.signe.schedule.StatusScheduling.COMPLETED;
    }

    public void missScheduling(){

        if(status != br.com.signe.schedule.StatusScheduling.CONFIRMED){
            throw new IllegalStateException(
                    "Somente horários confirmados podem ser marcados como falta.");
        }

        status = br.com.signe.schedule.StatusScheduling.MISSED;
    }
    @Override
    public String toString() {
        return "Scheduling{" +
                "client=" + client +
                ", specialist=" + specialist +
                ", procedure=" + procedure +
                ", start=" + start +
                ", finish=" + finish +
                ", status=" + status +
                '}';
    }
}
