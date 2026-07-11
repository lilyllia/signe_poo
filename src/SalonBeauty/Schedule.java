package SalonBeauty;

import java.util.ArrayList;
import java.util.List;

import static jdk.jfr.FlightRecorder.isAvailable;

public class Schedule {

    private Specialist specialist;
    private List<Scheduling> schedulings;

    public Schedule(Specialist specialist) {
        this.specialist = specialist;
        this.schedulings = new ArrayList<>();
    }

    public Specialist getSpecialist() {
        return specialist;
    }

    public List<Scheduling> getSchedulings() {
        return schedulings;
    }

    public void addScheduling(Scheduling scheduling) {
        if (isAvailable(scheduling.getStart(), scheduling.getFinish())) {
            schedulings.add(scheduling);
        } else {
            throw new IllegalArgumentException("O horário solicitado não está disponível.");
        }
    }
}