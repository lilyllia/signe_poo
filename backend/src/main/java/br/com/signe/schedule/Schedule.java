package br.com.signe.schedule;

import br.com.signe.employee.Specialist;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jdk.jfr.FlightRecorder.isAvailable;

public class Schedule {

    private br.com.signe.employee.Specialist specialist;
    private List<br.com.signe.schedule.Scheduling> schedulings;

    public Schedule(Specialist specialist) {
        this.specialist = specialist;
        this.schedulings = new ArrayList<>();
    }

    public Specialist getSpecialist() {
        return specialist;
    }

    public List<br.com.signe.schedule.Scheduling> getSchedulings() {
        return schedulings;
    }

    public void addScheduling(br.com.signe.schedule.Scheduling scheduling) {
        if (isAvailable(scheduling.getStart(), scheduling.getFinish())) {
            schedulings.add(scheduling);
        } else {
            throw new IllegalArgumentException("O horário solicitado não está disponível.");
        }
    }

    private boolean isAvailable(LocalDateTime start, LocalDateTime finish) {
        for (br.com.signe.schedule.Scheduling s : schedulings) {
            if (!(finish.isBefore(s.getStart()) || start.isAfter(s.getFinish()))) {
                return false;
            }
        }
        return true;
    }
}