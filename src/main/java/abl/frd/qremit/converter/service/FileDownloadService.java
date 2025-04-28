package abl.frd.qremit.converter.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import abl.frd.qremit.converter.model.FileDownloadModel;
import abl.frd.qremit.converter.repository.FileDownlaodRepository;

@Service
public class FileDownloadService {
    @Autowired
    FileDownlaodRepository fileDownlaodRepository;
    public void add(String type, String fileName, String url){
        LocalDateTime currenDateTime = CommonService.getCurrentDateTime();
        FileDownloadModel fileDownloadModel = new FileDownloadModel(currenDateTime, type, fileName, url);
        fileDownlaodRepository.save(fileDownloadModel);
    }
}
