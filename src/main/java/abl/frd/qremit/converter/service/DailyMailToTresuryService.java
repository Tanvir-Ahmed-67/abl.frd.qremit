package abl.frd.qremit.converter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class DailyMailToTresuryService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JavaMailSender mailSender;

    @Scheduled(cron = "0 0 15 * * ?") // Every day at 4:30 PM
    public void sendDailyAmountReport() {
        String sql = "SELECT SUM(amount + incentive) FROM converted_data_beftn WHERE DATE(download_date_time) = CURRENT_DATE";
        Double amount = jdbcTemplate.queryForObject(sql, Double.class);
        if(amount != null) {
            sendFormattedEmail(amount);
        }
    }
    public void sendFormattedEmail(double amount) {
        String[] recipients = {
                "tanvir.ahmed@agranibank.org",
                "agranidealers@gmail.com",
                "treasury@agranibank.org"
        };
        // Skipping sending mail if date is a Friday or Saturday
        DayOfWeek todayCheck = LocalDate.now().getDayOfWeek();
        if (todayCheck == DayOfWeek.FRIDAY || todayCheck == DayOfWeek.SATURDAY) {
            return; // Don't send mail.
        }
        String body = buildEmailBody(amount);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipients);
        // Format the current date for email subject
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDate = today.format(formatter);
        // Set email subject and body
        message.setSubject("Approximate total BEFTN amount from FRD. Date-"+formattedDate );
        message.setText(body);
        // sent the mail
        mailSender.send(message);
    }
    public String buildEmailBody(double totalAmount) {
        return "Dear Sir,\n\n" +
                "From FRD, approximate total BEFTN amount disbursed today is: " +
                String.format("%,.2f", totalAmount)+ " BDT\n\n" +
                "Best Regards,\n\n" +
                "Tanvir Ahmed\n" +
                "Principal Officer\n" +
                "Foreign Remittance Division.\n" +
                "Agrani Bank PLC (Head Office)\n" +
                "Contact: 01714785866";
    }
}
