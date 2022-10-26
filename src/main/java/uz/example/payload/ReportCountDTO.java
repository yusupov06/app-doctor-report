package uz.example.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Me: muhammadqodir
 * Project: app-doctor-report/IntelliJ IDEA
 * Date:Wed 26/10/22 14:49
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReportCountDTO {

    private String fromDoctorName;
    private Long reportCount;

}
