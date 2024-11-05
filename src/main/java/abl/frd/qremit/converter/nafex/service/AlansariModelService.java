package abl.frd.qremit.converter.nafex.service;

import java.io.*;
import java.time.LocalDateTime;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import abl.frd.qremit.converter.nafex.model.ErrorDataModel;
import abl.frd.qremit.converter.nafex.model.FileInfoModel;
import abl.frd.qremit.converter.nafex.model.AlansariModel;
import abl.frd.qremit.converter.nafex.model.User;
import abl.frd.qremit.converter.nafex.repository.AccountPayeeModelRepository;
import abl.frd.qremit.converter.nafex.repository.BeftnModelRepository;
import abl.frd.qremit.converter.nafex.repository.CocModelRepository;
import abl.frd.qremit.converter.nafex.repository.ExchangeHouseModelRepository;
import abl.frd.qremit.converter.nafex.repository.FileInfoModelRepository;
import abl.frd.qremit.converter.nafex.repository.AlansariModelRepository;
import abl.frd.qremit.converter.nafex.repository.OnlineModelRepository;
import abl.frd.qremit.converter.nafex.repository.UserModelRepository;
import java.util.*;
import java.util.stream.Collectors;
@SuppressWarnings("unchecked")
@Service
public class AlansariModelService {
    @Autowired
    AlansariModelRepository alansariModelRepository;
    @Autowired
    OnlineModelRepository onlineModelRepository;
    @Autowired
    CocModelRepository cocModelRepository;
    @Autowired
    AccountPayeeModelRepository accountPayeeModelRepository;
    @Autowired
    BeftnModelRepository beftnModelRepository;
    @Autowired
    FileInfoModelRepository fileInfoModelRepository;
    @Autowired
    UserModelRepository userModelRepository;
    @Autowired
    ExchangeHouseModelRepository exchangeHouseModelRepository;
    @Autowired
    ErrorDataModelService errorDataModelService;
    @Autowired
    FileInfoModelService fileInfoModelService;
    
    public Map<String, Object> save(MultipartFile file, int userId, String exchangeCode, String nrtaCode) {
        Map<String, Object> resp = new HashMap<>();
        LocalDateTime currentDateTime = CommonService.getCurrentDateTime();
        try
        {
            FileInfoModel fileInfoModel = new FileInfoModel();
            fileInfoModel.setUserModel(userModelRepository.findByUserId(userId));
            User user = userModelRepository.findByUserId(userId);
            fileInfoModel.setExchangeCode(exchangeCode);
            fileInfoModel.setFileName(file.getOriginalFilename());
            fileInfoModel.setUploadDateTime(currentDateTime);
            fileInfoModelRepository.save(fileInfoModel);

            Map<String, Object> alansariData = csvToAlansariModels(file.getInputStream(), user, fileInfoModel, exchangeCode, nrtaCode, currentDateTime);
            List<AlansariModel> alansariModels = (List<AlansariModel>) alansariData.get("alansariDataModelList");

            if(alansariData.containsKey("errorMessage")){
                resp.put("errorMessage", alansariData.get("errorMessage"));
            }
            if(alansariData.containsKey("errorCount") && ((Integer) alansariData.get("errorCount") >= 1)){
                int errorCount = (Integer) alansariData.get("errorCount");
                fileInfoModel.setErrorCount(errorCount);
                resp.put("fileInfoModel", fileInfoModel);
                fileInfoModelRepository.save(fileInfoModel);
            }

            if(alansariModels.size()!=0) {
                for(AlansariModel alansariModel : alansariModels){
                    alansariModel.setFileInfoModel(fileInfoModel);
                    alansariModel.setUserModel(user);
                }
                // 4 DIFFERENTS DATA TABLE GENERATION GOING ON HERE
                Map<String, Object> convertedDataModels = CommonService.generateFourConvertedDataModel(alansariModels, fileInfoModel, user, currentDateTime, 0);
                fileInfoModel = CommonService.countFourConvertedDataModel(convertedDataModels);
                fileInfoModel.setTotalCount(String.valueOf(alansariModels.size()));
                fileInfoModel.setIsSettlement(0);
                fileInfoModel.setAlansariModel(alansariModels);
   
                // SAVING TO MySql Data Table
                try{
                    fileInfoModelRepository.save(fileInfoModel);                
                    resp.put("fileInfoModel", fileInfoModel);
                }catch(Exception e){
                    resp.put("errorMessage", e.getMessage());
                }
            }
        } catch (IOException e) {
            String message = "fail to store csv data: " + e.getMessage();
            resp.put("errorMessage", message);
            throw new RuntimeException(message);
        }
        return resp;
    }

    public Map<String, Object> csvToAlansariModels(InputStream is, User user, FileInfoModel fileInfoModel, String exchangeCode, String nrtaCode, LocalDateTime currentDateTime) {
        Map<String, Object> resp = new HashMap<>();
        Optional<AlansariModel> duplicateData;
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"))){
            String uniqueHeaderLine = getUniqueHeader(fileReader);
            String fileContentWithUniqueHeader = uniqueHeaderLine + "\n" + fileReader.lines().collect(Collectors.joining("\n"));
            try(CSVParser csvParser = new CSVParser(new StringReader(fileContentWithUniqueHeader),CSVFormat.newFormat('|').withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())){
            //CSVParser csvParser = new CSVParser(fileReader, CSVFormat.newFormat('|').withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())){
                Iterable<CSVRecord> csvRecords = csvParser.getRecords();
                List<AlansariModel> alansariDataModelList = new ArrayList<>();
                List<ErrorDataModel> errorDataModelList = new ArrayList<>();
                List<String> transactionList = new ArrayList<>();
                String duplicateMessage = "";
                int i = 0;
                int duplicateCount = 0;
                for (CSVRecord csvRecord : csvRecords) {
                    i++;
                    String transactionNo = csvRecord.get(1).trim();
                    String amount = csvRecord.get(3).trim();
                    duplicateData = alansariModelRepository.findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(transactionNo, CommonService.convertStringToDouble(amount), exchangeCode);
                    String beneficiaryAccount = csvRecord.get(7).trim();
                    String bankName = csvRecord.get(8).trim();
                    String branchCode = CommonService.fixRoutingNo(csvRecord.get(11).trim());
                    Map<String, Object> data = getCsvData(csvRecord, exchangeCode, transactionNo, beneficiaryAccount, bankName, branchCode);

                    Map<String, Object> errResp = CommonService.checkError(data, errorDataModelList, nrtaCode, fileInfoModel, user, currentDateTime, csvRecord.get(0).trim(), duplicateData, transactionList);
                    if((Integer) errResp.get("err") == 1){
                        errorDataModelList = (List<ErrorDataModel>) errResp.get("errorDataModelList");
                        continue;
                    }
                    if((Integer) errResp.get("err") == 2){
                        resp.put("errorMessage", errResp.get("msg"));
                        break;
                    }
                    if((Integer) errResp.get("err") == 3){
                        duplicateMessage += errResp.get("msg");
                        duplicateCount++;
                        continue;
                    }
                    if((Integer) errResp.get("err") == 4){
                        duplicateMessage += errResp.get("msg");
                        continue;
                    }
                    if(errResp.containsKey("transactionList"))  transactionList = (List<String>) errResp.get("transactionList");

                    AlansariModel alansariDataModel = new AlansariModel();
                    alansariDataModel = CommonService.createDataModel(alansariDataModel, data);
                    alansariDataModel.setTypeFlag(CommonService.setTypeFlag(beneficiaryAccount, bankName, branchCode));
                    alansariDataModel.setUploadDateTime(currentDateTime);
                    alansariDataModelList.add(alansariDataModel);
                }

                //save error data
                Map<String, Object> saveError = errorDataModelService.saveErrorModelList(errorDataModelList);
                if(saveError.containsKey("errorCount")) resp.put("errorCount", saveError.get("errorCount"));
                if(saveError.containsKey("errorMessage")){
                    resp.put("errorMessage", saveError.get("errorMessage"));
                    return resp;
                }
                //if both model is empty then delete fileInfoModel
                if(errorDataModelList.isEmpty() && alansariDataModelList.isEmpty()){
                    fileInfoModelService.deleteFileInfoModelById(fileInfoModel.getId());
                }
                resp.put("alansariDataModelList", alansariDataModelList);
                if(!resp.containsKey("errorMessage")){
                    resp.put("errorMessage", CommonService.setErrorMessage(duplicateMessage, duplicateCount, i));
                }
            }catch (IOException e) {
                resp.put("errorMessage", "fail to store csv data: " + e.getMessage());
            }
        } catch (IOException e) {
            String message = "fail to store csv data: " + e.getMessage();
            resp.put("errorMessage", message);
            throw new RuntimeException(message);
        }
        return resp;
    }
    
    public Map<String, Object> getCsvData(CSVRecord csvRecord, String exchangeCode, String transactionNo, String beneficiaryAccount, String bankName, String branchCode){
        Map<String, Object> data = new HashMap<>();
        data.put("exchangeCode", exchangeCode);
        data.put("transactionNo", transactionNo);
        data.put("currency", csvRecord.get(2));
        data.put("amount", csvRecord.get(3));
        data.put("enteredDate", csvRecord.get(4));
        data.put("remitterName", csvRecord.get(5));
        data.put("remitterMobile", "");
        data.put("beneficiaryName", csvRecord.get(6));
        data.put("beneficiaryAccount", beneficiaryAccount);
        data.put("beneficiaryMobile", csvRecord.get(12));
        data.put("bankName", bankName);
        data.put("bankCode", csvRecord.get(9));
        data.put("branchName", csvRecord.get(10));
        data.put("branchCode", branchCode);
        data.put("draweeBranchName", "");
        data.put("draweeBranchCode", "");
        data.put("purposeOfRemittance", "");
        data.put("sourceOfIncome", "");
        data.put("processFlag", "");
        data.put("processedBy", "");
        data.put("processedDate", "");
        return data;
    }

    public String getUniqueHeader(BufferedReader fileReader) throws IOException{
        String uniqueHeaderLine = "";
        String headerLine = fileReader.readLine();
        // Split header and handle duplicates by appending a counter
        String[] headers = headerLine.split("\\|");
        Map<String, Integer> headerCountMap = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            String header = headers[i].trim();
            // If header is "0", or any other duplicate header, rename it
            if (headerCountMap.containsKey(header)) {
                int count = headerCountMap.get(header) + 1;
                headerCountMap.put(header, count);
                headers[i] = header + "_" + count;  // Rename duplicate header
            } else {
                headerCountMap.put(header, 1);
            }
        }
        // Reconstruct the header line
        uniqueHeaderLine = String.join("|", headers);
        return uniqueHeaderLine;
    }


}
