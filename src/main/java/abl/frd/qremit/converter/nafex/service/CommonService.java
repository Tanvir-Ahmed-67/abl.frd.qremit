package abl.frd.qremit.converter.nafex.service;

import abl.frd.qremit.converter.nafex.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommonService {
    @Autowired
    OnlineModelRepository onlineModelRepository;
    @Autowired
    CocModelRepository cocModelRepository;
    @Autowired
    AccountPayeeModelRepository accountPayeeModelRepository;
    @Autowired
    BeftnModelRepository beftnModelRepository;
    @Autowired
    NafexModelRepository nafexModelRepository;
    @Autowired
    NafexModelRepository BecModelRepository;
    @Autowired
    NafexModelRepository MuzainiModelRepository;
    @Autowired
    FileInfoModelRepository fileInfoModelRepository;
    @Autowired
    UserModelRepository userModelRepository;
    public static String TYPE = "text/csv";
    public String uploadSuccesPage = "/pages/user/userUploadSuccessPage";
    public static boolean hasCSVFormat(MultipartFile file) {
        if (TYPE.equals(file.getContentType())
                || file.getContentType().equals("text/plain")) {
            return true;
        }
        return false;
    }
    public boolean ifFileExist(String fileName){
        if (fileInfoModelRepository.findByFileName(fileName) != null) {
            return true;
        }
        return false;
    }
    public List<Integer> CountAllFourTypesOfData(){
        List<Integer> count = new ArrayList<Integer>(5);
        count.add(onlineModelRepository.countByIsProcessed("0"));
        count.add(cocModelRepository.countByIsProcessed("0"));
        count.add(accountPayeeModelRepository.countByIsProcessed("0"));
        count.add(beftnModelRepository.countByIsProcessedMain("0"));
        count.add(beftnModelRepository.countByIsProcessedIncentive("0"));
        return count;
    }
}
