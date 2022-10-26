package uz.example.controller;


import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.ServletContextAware;
import uz.example.payload.*;
import uz.example.service.contract.DoctorService;
import uz.example.service.contract.ReportService;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@Slf4j
@RequiredArgsConstructor
public class FileDownloadController implements ServletContextAware {

    private ServletContext servletContext;
    private final DoctorService doctorService;
    private final ReportService reportService;
    private static final int BUFFER_SIZE = 4096;

    private final String filePath = "/downloads/report.pdf";


    @RequestMapping(method = RequestMethod.POST, value = "/home/{from}")
    public String homeSorted(Model model, @Valid ReportGetDTO reportGetDTO, @PathVariable Long from) {
        DoctorDTO doctor = doctorService.getDoctorById(from);
        reportGetDTO.setDoctorId(from);
        model.addAttribute("doctor", doctor);
        model.addAttribute("reportAddDTO", new ReportAddDTO());
        List<ReportDTO> list = reportService.getAllByDoctorAndDates(reportGetDTO);
        List<ReportCountDTO> countsByFrom = reportService.getCountsByFrom(reportGetDTO);
        createPDFAndDownload(doctor, reportGetDTO);
        ResponseDTO responseDTO = new ResponseDTO(list, countsByFrom);
        model.addAttribute("response", responseDTO);
        model.addAttribute("reportGet", new ReportGetDTO());
        return "home";
    }

    private void createPDFAndDownload(DoctorDTO doctor, ReportGetDTO reportGetDTO) {

        List<ReportDTO> list = reportService.getAllByDoctorAndDates(reportGetDTO);
        List<ReportCountDTO> countsByFrom = reportService.getCountsByFrom(reportGetDTO);
        if (list.isEmpty()) {
            throw new NoSuchElementException("Ushbu kunlarda Hisobotlar yo'q");
        }
        try {
            generatePDF(list, countsByFrom, doctor.getFullname());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void generatePDF(List<ReportDTO> list, List<ReportCountDTO> countsByFrom, String fullname) throws IOException {

        Document document = new Document();
        try {

            String appPath = servletContext.getRealPath("");
            System.out.println("appPath = " + appPath);


            String fullPath = appPath + filePath;

            File file = new File(fullPath);

            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            document.addAuthor("Muhammadqodir");
            document.addCreationDate();

            document.addTitle(" Hisobot ");
            document.addHeader("Name: ", fullname);

            Paragraph header = new Paragraph(" Hisobot ");
            header.setAlignment(Element.ALIGN_CENTER);
            header.setAlignment(Element.ALIGN_MIDDLE);

            document.add(header);


            PdfPTable tableForCounts = new PdfPTable(3);
            tableForCounts.setWidthPercentage(100);
            tableForCounts.setSpacingBefore(10f);
            tableForCounts.setSpacingAfter(10f);
            float[] columnWidths1 = {1f, 1f, 1f};
            tableForCounts.setWidths(columnWidths1);

            PdfPCell cell11 = new PdfPCell(new Paragraph("Tartib "));
            cell11.setPaddingLeft(10);
            cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell11.setVerticalAlignment(Element.ALIGN_MIDDLE);

            PdfPCell cell22 = new PdfPCell(new Paragraph("Doctor ism va familiyasi "));
            cell22.setPaddingLeft(10);
            cell22.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell22.setVerticalAlignment(Element.ALIGN_MIDDLE);

            PdfPCell cell33 = new PdfPCell(new Paragraph(" Yuborilganlar soni "));
            cell33.setPaddingLeft(10);
            cell33.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell33.setVerticalAlignment(Element.ALIGN_MIDDLE);

            tableForCounts.addCell(cell11);
            tableForCounts.addCell(cell22);
            tableForCounts.addCell(cell33);


            int k = 1;
            for (ReportCountDTO reportCountDTO : countsByFrom) {

                cell11 = new PdfPCell(new Paragraph(String.valueOf(k++)));
                cell22 = new PdfPCell(new Paragraph(reportCountDTO.getFromDoctorName()));
                cell33 = new PdfPCell(new Paragraph(String.valueOf(reportCountDTO.getReportCount())));

                tableForCounts.addCell(cell11);
                tableForCounts.addCell(cell22);
                tableForCounts.addCell(cell33);

            }

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            document.add(tableForCounts);

            float[] columnWidths = {1f, 1f, 1f, 1f, 1f};
            table.setWidths(columnWidths);

            PdfPCell cell1 = new PdfPCell(new Paragraph("Tartib "));
            cell1.setPaddingLeft(10);
            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);

            PdfPCell cell2 = new PdfPCell(new Paragraph("Hisobot yuborilgan doktor "));
            cell2.setPaddingLeft(10);
            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);

            PdfPCell cell3 = new PdfPCell(new Paragraph(" Bemor to'liq ism va sharifi"));
            cell3.setPaddingLeft(10);
            cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);

            PdfPCell cell4 = new PdfPCell(new Paragraph(" Bemor manzili"));
            cell4.setPaddingLeft(10);
            cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);

            PdfPCell cell5 = new PdfPCell(new Paragraph(" Hisobot yuborilgan vaqt "));
            cell5.setPaddingLeft(10);
            cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell5.setVerticalAlignment(Element.ALIGN_MIDDLE);

            table.addCell(cell1);
            table.addCell(cell2);
            table.addCell(cell3);
            table.addCell(cell4);
            table.addCell(cell5);
            k = 1;
            for (ReportDTO reportDTO : list) {

                cell1 = new PdfPCell(new Paragraph(String.valueOf(k++)));
                cell2 = new PdfPCell(new Paragraph(reportDTO.getFromDoctorName()));
                cell3 = new PdfPCell(new Paragraph(reportDTO.getPatient().getFullname()));
                cell4 = new PdfPCell(new Paragraph(reportDTO.getPatient().getAddress()));
                cell5 = new PdfPCell(new Paragraph(reportDTO.getSentTime()));
                table.addCell(cell1);
                table.addCell(cell2);
                table.addCell(cell3);
                table.addCell(cell4);
                table.addCell(cell5);
            }

            document.add(table);


            document.close();
            writer.close();
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }


    @RequestMapping(value = "/downloadFile", method = RequestMethod.GET)
    public void doDownload(HttpServletResponse response) throws IOException {


        String appPath = servletContext.getRealPath("");
        System.out.println("appPath = " + appPath);


        String fullPath = appPath + filePath;
        File downloadFile = new File(fullPath);
        FileInputStream inputStream = new FileInputStream(downloadFile);


        String mimeType = servletContext.getMimeType(fullPath);
        if (mimeType == null) {

            mimeType = "application/octet-stream";
        }
        System.out.println("MIME type: " + mimeType);


        response.setContentType(mimeType);
        response.setContentLength((int) downloadFile.length());


        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"",
                downloadFile.getName());
        response.setHeader(headerKey, headerValue);


        OutputStream outStream = response.getOutputStream();

        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = -1;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }

        inputStream.close();
        outStream.close();
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;

    }
}