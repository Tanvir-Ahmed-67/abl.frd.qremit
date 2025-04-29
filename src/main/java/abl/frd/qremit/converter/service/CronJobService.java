package abl.frd.qremit.converter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CronJobService {
    protected static String urlPrefix = "http://203.188.255.210:8085/";
    @Autowired
    RestTemplate restTemplate;
    public void triggerGenerateTemporaryReport() {
        try {
            String url = urlPrefix + "/generateTemporaryReport";
            String response = restTemplate.getForObject(url, String.class);
            System.out.println("Successfully triggered Temporary Report: " + response);
        } catch (Exception ex) {
            System.err.println("Failed to trigger Temporary Report: " + ex.getMessage());
        }
    }

    @Scheduled(cron = "0 0 20 * * *", zone = "Asia/Dhaka")
    public void triggerTempEightPm(){
        triggerGenerateTemporaryReport();
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Dhaka")
    public void triggerTempTwelveAm(){
        triggerGenerateTemporaryReport();
    }
}
