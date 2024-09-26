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

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

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
    //@Autowired
    //CommonService commonService;
    @Autowired
    ReportService reportService;
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
            int totalCount = cocPaidModelList.size();
            fileInfoModel.setCocPaidModelList(cocPaidModelList);
            fileInfoModel.setFileName(file.getOriginalFilename());
            fileInfoModel.setUploadDateTime(currentDateTime);
            fileInfoModel.setIsSettlement(1);
            fileInfoModel.setCocCount(String.valueOf(totalCount));
            fileInfoModel.setAccountPayeeCount("0");
            fileInfoModel.setOnlineCount("0");
            fileInfoModel.setBeftnCount("0");
            fileInfoModel.setTotalCount(String.valueOf(totalCount));
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
                String routingNo = CommonService.fixABLRoutingNo(csvRecord.get(8));
                String branchCode = reportService.getABLBranchFromRouting(routingNo);
                
                CocPaidModel cocPaidModel = new CocPaidModel(
                        csvRecord.get(0), //exCode
                        csvRecord.get(1), //Tranno
                        Double.parseDouble(csvRecord.get(4)), //Amount
                        CommonService.convertStringToDate(csvRecord.get(3)), //Entered Date
                        CommonService.convertStringToDate(csvRecord.get(11)), //Paid Date
                        csvRecord.get(5), //remitter Name
                        csvRecord.get(6), // beneficiary Name
                        csvRecord.get(7), //beneficiaryAccount
                        routingNo, //routingNo
                        csvRecord.get(10), //beneficiary Mobile
                        branchCode,// branch code have to put here
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
