package uz.example.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.example.entity.Report;

import java.time.LocalDateTime;

/**
 * Me: muhammadqodir
 * Project: SpringMyProject/IntelliJ IDEA
 * Date:Mon 24/10/22 00:41
 */


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReportDTO {

    private DoctorDTO owner;
    private String fromDoctorName;
    private PatientDTO patient;

    private String sentTime;

    public ReportDTO(Report report) {
        this.owner = new DoctorDTO(report.getOwner());
        this.fromDoctorName = report.getDoctorFrom();
        this.patient = new PatientDTO(report.getPatient());

        LocalDateTime createdAt = report.getCreatedAt();

        this.sentTime = createdAt.getDayOfMonth() + "-" + (createdAt.getMonth().getValue() + 1) + "-" + createdAt.getYear()
                + "   " + createdAt.getHour() + ":" + createdAt.getMinute();

    }
}
