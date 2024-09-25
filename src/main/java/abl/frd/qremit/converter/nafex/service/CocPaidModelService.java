package abl.frd.qremit.converter.nafex.service;

import abl.frd.qremit.converter.nafex.model.CocPaidModel;
import abl.frd.qremit.converter.nafex.model.FileInfoModel;
import abl.frd.qremit.converter.nafex.model.User;
import abl.frd.qremit.converter.nafex.repository.CocPaidModelRepository;
import abl.frd.qremit.converter.nafex.repository.FileInfoModelRepository;
import abl.frd.qremit.converter.nafex.repository.UserModelRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CocPaidModelService {
    @Autowired
    CocPaidModelRepository cocPaidModelRepository;
    @Autowired
    MyUserDetailsService myUserDetailsService;
    @Autowired
    UserModelRepository userModelRepository;
    @Autowired
    FileInfoModelRepository fileInfoModelRepository;
    LocalDateTime currentDateTime = LocalDateTime.now();
    public FileInfoModel save(MultipartFile file, int userId, String exchangeCode){
        try{
            FileInfoModel fileInfoModel = new FileInfoModel();
            fileInfoModel.setUserModel(userModelRepository.findByUserId(userId));
            User user = userModelRepository.findByUserId(userId);
            List<CocPaidModel> cocPaidModelList = csvToCocPaidModels(file.getInputStream());
            if(cocPaidModelList.size()!=0) {
                int ind = 0;
                for (CocPaidModel cocPaidModel : cocPaidModelList) {
                    cocPaidModel.setFileInfoModel(fileInfoModel);
                    cocPaidModel.setUserModel(user);
                    if (ind == 0) {
                        fileInfoModel.setExchangeCode(exchangeCode);
                        ind++;
                    }
                }
            }
            else {
                return null;
            }
            fileInfoModel.setCocPaidModelList(cocPaidModelList);
            fileInfoModel.setFileName(file.getOriginalFilename());
            fileInfoModel.setUploadDateTime(currentDateTime);
            fileInfoModelRepository.save(fileInfoModel);
            return fileInfoModel;
        }
        catch(Exception e){
            throw new RuntimeException("fail to store csv data: " + e.getMessage());
        }
    }
    public List<CocPaidModel> csvToCocPaidModels(InputStream is) {
        Optional<CocPaidModel> duplicateData;
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withDelimiter(',').withQuote('"').withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            List<CocPaidModel> cocPaidModelList = new ArrayList<>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            for (CSVRecord csvRecord : csvRecords) {
                duplicateData = cocPaidModelRepository.findByTransactionNoEqualsIgnoreCase(csvRecord.get(1));
                if (duplicateData.isPresent()) {  // Checking Duplicate Transaction No in this block
                    continue;
                }
                CocPaidModel cocPaidModel = new CocPaidModel(
                        csvRecord.get(0), //exCode
                        csvRecord.get(1), //Tranno
                        Double.parseDouble(csvRecord.get(4)), //Amount
                        csvRecord.get(3), //Entered Date
                        csvRecord.get(11), //Paid Date
                        csvRecord.get(5), //remitter Name
                        csvRecord.get(6), // beneficiary Name
                        csvRecord.get(7), //beneficiaryAccount
                        csvRecord.get(10), //beneficiary Mobile
                        "",// branch code have to put here
                        csvRecord.get(12), //tr mode
                        currentDateTime);  //uploadDateTime
                cocPaidModelList.add(cocPaidModel);
            }
            return cocPaidModelList;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }
}
