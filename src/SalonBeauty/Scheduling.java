package SalonBeauty;

import java.time.LocalDateTime;
import java.util.Objects;

public class Scheduling {

    private Client client;
    private Specialist specialist;
    private Service service;
    private LocalDateTime start;
    private LocalDateTime finish;
    private StatusScheduling status;

    public Scheduling(Client client, Specialist specialist, Service service, LocalDateTime start, LocalDateTime finish) {
        this.client = Objects.requireNonNull(client, "Cliente é obrigatório.");
        this.specialist = Objects.requireNonNull(specialist, "Especialista é obrigatório.");
        this.service = Objects.requireNonNull(service, "Serviço é obrigatório.");
        this.start = Objects.requireNonNull(start, "Data inicial é obrigatória.");
        this.finish = Objects.requireNonNull(finish, "Data final é obrigatória.");

        if (!finish.isAfter(start)) {
            throw new IllegalArgumentException(
                    "O horário final deve ser posterior ao horário inicial.");
        }
        this.status = StatusScheduling.SCHEDULED;
    }
    public Client getClient() {
        return client;
    }

    public Specialist getSpecialist() {
        return specialist;
    }

    public Service getService() {
        return service;
    }

    public LocalDateTime getFinish() {
        return finish;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public StatusScheduling getStatus() {
        return status;
    }

    public void confirmScheduling(){
        if(status != StatusScheduling.SCHEDULED){
            throw new IllegalStateException("Só é possivel confirmar horários AGENDADOS.");
        }
        status = StatusScheduling.CONFIRMED;
    }

    public void cancelScheduling(){
        if(status != StatusScheduling.SCHEDULED && status != StatusScheduling.CONFIRMED){
            throw new IllegalStateException("Só é possivel cancelar horários AGENDADOS ou CONFIRMADOS.");
        }
        status = StatusScheduling.CANCELLED;
    }

    public void completedService(){
        if(status != StatusScheduling.CONFIRMED){
            throw new IllegalStateException("Só é possivel completar serviços de horários CONFIRMADOS.");
        }
        status = StatusScheduling.COMPLETED;
    }

    public void missScheduling(){

        if(status != StatusScheduling.CONFIRMED){
            throw new IllegalStateException(
                    "Somente horários confirmados podem ser marcados como falta.");
        }

        status = StatusScheduling.MISSED;
    }
}
