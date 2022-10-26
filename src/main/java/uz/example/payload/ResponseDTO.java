package uz.example.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Me: muhammadqodir
 * Project: app-doctor-report/IntelliJ IDEA
 * Date:Wed 26/10/22 14:57
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResponseDTO {

    private List<ReportDTO> list = new ArrayList<>();
    private List<ReportCountDTO> counts = new ArrayList<>();

}
