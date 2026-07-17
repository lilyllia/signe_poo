package br.com.signe.schedule;

import br.com.signe.client.Client;
import br.com.signe.service.Procedure;
import br.com.signe.employee.Specialist;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

public class Scheduling {

    private Client client;
    private Specialist specialist;
    private Procedure procedure;
    private LocalTime start;
    private LocalTime finish;
    private schedule.StatusScheduling status;

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
        this.status = schedule.StatusScheduling.SCHEDULED;
    }
    public Client getClient() {
        return client;
    }

    public Specialist getSpecialist() {
        return specialist;
    }

    public Procedure getProcedure() {
        return procedure;
    }

    public LocalTime getFinish() {
        return finish;
    }

    public LocalTime getStart() {
        return start;
    }

    public schedule.StatusScheduling getStatus() {
        return status;
    }

    public void confirmScheduling(){
        if(status != schedule.StatusScheduling.SCHEDULED){
            throw new IllegalStateException("Só é possivel confirmar horários AGENDADOS.");
        }
        status = schedule.StatusScheduling.CONFIRMED;
    }

    public void cancelScheduling(){
        if(status != schedule.StatusScheduling.SCHEDULED && status != schedule.StatusScheduling.CONFIRMED){
            throw new IllegalStateException("Só é possivel cancelar horários AGENDADOS ou CONFIRMADOS.");
        }
        status = schedule.StatusScheduling.CANCELLED;
    }

    public void completedService(){
        if(status != schedule.StatusScheduling.CONFIRMED){
            throw new IllegalStateException("Só é possivel completar serviços de horários CONFIRMADOS.");
        }
        status = schedule.StatusScheduling.COMPLETED;
    }

    public void missScheduling(){

        if(status != schedule.StatusScheduling.CONFIRMED){
            throw new IllegalStateException(
                    "Somente horários confirmados podem ser marcados como falta.");
        }

        status = schedule.StatusScheduling.MISSED;
    }
}
