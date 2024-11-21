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
import abl.frd.qremit.converter.nafex.model.SwiftModel;
import abl.frd.qremit.converter.nafex.model.User;
import abl.frd.qremit.converter.nafex.repository.AccountPayeeModelRepository;
import abl.frd.qremit.converter.nafex.repository.BeftnModelRepository;
import abl.frd.qremit.converter.nafex.repository.CocModelRepository;
import abl.frd.qremit.converter.nafex.repository.ExchangeHouseModelRepository;
import abl.frd.qremit.converter.nafex.repository.FileInfoModelRepository;
import abl.frd.qremit.converter.nafex.repository.SwiftModelRepository;
import abl.frd.qremit.converter.nafex.repository.OnlineModelRepository;
import abl.frd.qremit.converter.nafex.repository.UserModelRepository;
import java.util.*;
import java.util.stream.Collectors;
import java.util.regex.*;


@SuppressWarnings("unchecked")
@Service
public class SwiftModelService {
    @Autowired
    SwiftModelRepository swiftModelRepository;
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

            Map<String, Object> swiftData = csvToSwiftModels(file.getInputStream(), user, fileInfoModel, exchangeCode, nrtaCode, currentDateTime);
            List<SwiftModel> swiftModels = (List<SwiftModel>) swiftData.get("swiftDataModelList");

            if(swiftData.containsKey("errorMessage")){
                resp.put("errorMessage", swiftData.get("errorMessage"));
            }
            if(swiftData.containsKey("errorCount") && ((Integer) swiftData.get("errorCount") >= 1)){
                int errorCount = (Integer) swiftData.get("errorCount");
                fileInfoModel.setErrorCount(errorCount);
                resp.put("fileInfoModel", fileInfoModel);
                fileInfoModelRepository.save(fileInfoModel);
            }

            if(swiftModels.size()!=0) {
                for(SwiftModel swiftModel : swiftModels){
                    swiftModel.setFileInfoModel(fileInfoModel);
                    swiftModel.setUserModel(user);
                }
                // 4 DIFFERENTS DATA TABLE GENERATION GOING ON HERE
                Map<String, Object> convertedDataModels = CommonService.generateFourConvertedDataModel(swiftModels, fileInfoModel, user, currentDateTime, 0);
                fileInfoModel = CommonService.countFourConvertedDataModel(convertedDataModels);
                fileInfoModel.setTotalCount(String.valueOf(swiftModels.size()));
                fileInfoModel.setIsSettlement(0);
                fileInfoModel.setSwiftModel(swiftModels);
   
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

    public Map<String, Object> csvToSwiftModels(InputStream is, User user, FileInfoModel fileInfoModel, String exchangeCode, String nrtaCode, LocalDateTime currentDateTime) {
        Map<String, Object> resp = new HashMap<>();
       // Optional<SwiftModel> duplicateData; 
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"))){
        //try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(((MultipartFile) is).getInputStream(), StandardCharsets.UTF_8))) {   
           
          //  String uniqueHeaderLine = getUniqueHeader(fileReader);

            StringBuilder swiftContent = new StringBuilder();
            String line;
            while ((line = fileReader.readLine()) != null) {
                swiftContent.append(line).append("\n");
            }

            List<Map<String, String>> transactions = extractTransactions(swiftContent.toString());
            if (transactions.isEmpty()) {
                resp.put("errorMessage", "No valid SWIFT transactions found in the file.");
            } else {
                System.out.println(transactions);
                resp.put("transactions", transactions);
                resp.put("transactionCount", transactions.size());
                for(Map<String, String> transaction: transactions){
                    String transactionNo = transaction.get("transactionNo");
                    String amount = transaction.get("amount");
                    String beneficiaryAccount = transaction.get("beneficiaryAccount");
                    String bankName = transaction.get("bankName");
                }
            }
            

           //Map<String, Object> data = getCsvData(csvRecord, exchangeCode, transactionNo, beneficiaryAccount, bankName, branchCode);

                    /* 
                    List<ErrorDataModel> errorDataModelList = new ArrayList<>();
                    duplicateData = swiftModelRepository.findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(transactionNo, CommonService.convertStringToDouble(amount), exchangeCode);
                            String beneficiaryAccount = transactions.
                            String bankName = transactions.get(8).trim();
                            String branchCode = CommonService.fixRoutingNo(transactions.get(11).trim());
                            Map<String, Object> data = getCsvData(csvRecord, exchangeCode, transactionNo, beneficiaryAccount, bankName, branchCode);
                            Map<String, Object> errResp = CommonService.checkError(data, errorDataModelList, nrtaCode, fileInfoModel, user, currentDateTime, csvRecord.get(0).trim(), duplicateData, transactionList);
                            if((Integer) errResp.get("err") == 1){
                                errorDataModelList = (List<ErrorDataModel>) errResp.get("errorDataModelList");
                                continue;
                            }
                    */
                    //   String fileContentWithUniqueHeader = uniqueHeaderLine + "\n" + fileReader.lines().collect(Collectors.joining("\n"));
                    // try(CSVParser csvParser = new CSVParser(new StringReader(fileContentWithUniqueHeader),CSVFormat.newFormat('|').withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())){
                    // CSVParser csvParser = new CSVParser(fileReader, CSVFormat.newFormat('|').withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim()){
                    //     Iterable<CSVRecord> csvRecords = csvParser.getRecords();
                    //     List<SwiftModel> swiftDataModelList = new ArrayList<>();
                    //    
                    //     List<String> transactionList = new ArrayList<>();
                    //     String duplicateMessage = "";
                    //     int i = 0;
                    //     int duplicateCount = 0;
                    //     for (CSVRecord csvRecord : csvRecords) {
                    //         i++;
                    //         String transactionNo = csvRecord.get(1).trim();
                    //         String amount = csvRecord.get(3).trim();
                            
                    //         if((Integer) errResp.get("err") == 2){
                    //             resp.put("errorMessage", errResp.get("msg"));
                    //             break;
                    //         }
                    //         if((Integer) errResp.get("err") == 3){
                    //             duplicateMessage += errResp.get("msg");
                    //             duplicateCount++;
                    //             continue;
                    //         }
                    //         if((Integer) errResp.get("err") == 4){
                    //             duplicateMessage += errResp.get("msg");
                    //             continue;
                    //         }
                    //         if(errResp.containsKey("transactionList"))  transactionList = (List<String>) errResp.get("transactionList");

                    //         SwiftModel swiftDataModel = new SwiftModel();
                    //         swiftDataModel = CommonService.createDataModel(swiftDataModel, data);
                    //         swiftDataModel.setTypeFlag(CommonService.setTypeFlag(beneficiaryAccount, bankName, branchCode));
                    //         swiftDataModel.setUploadDateTime(currentDateTime);
                    //         swiftDataModelList.add(swiftDataModel);
                    //     }
              /*  int i = 0;
                int duplicateCount = 0;
                String duplicateMessage = "";
                List<SwiftModel> swiftDataModelList = new ArrayList<>();
                List<ErrorDataModel> errorDataModelList = new ArrayList<>();
                //save error data
                Map<String, Object> saveError = errorDataModelService.saveErrorModelList(errorDataModelList);
                if(saveError.containsKey("errorCount")) resp.put("errorCount", saveError.get("errorCount"));
                if(saveError.containsKey("errorMessage")){
                    resp.put("errorMessage", saveError.get("errorMessage"));
                    return resp;
                }
                //if both model is empty then delete fileInfoModel
                if(errorDataModelList.isEmpty() && swiftDataModelList.isEmpty()){
                    fileInfoModelService.deleteFileInfoModelById(fileInfoModel.getId());
                }
                resp.put("swiftDataModelList", swiftDataModelList);
                if(!resp.containsKey("errorMessage")){
                    resp.put("errorMessage", CommonService.setErrorMessage(duplicateMessage, duplicateCount, i));
                }
                    */
            }catch (IOException e) {
                resp.put("errorMessage", "fail to store csv data: " + e.getMessage());
            }
        // } catch (IOException e) {
        //     String message = "fail to store csv data: " + e.getMessage();
        //     resp.put("errorMessage", message);
        //     throw new RuntimeException(message);
        // }
        return resp;
    }
    


    private List<Map<String, String>> extractTransactions(String fileContent) {
        List<Map<String, String>> transactions = new ArrayList<>();
       //Define the regex pattern to find valid transactions
        String transactionPattern = "(:20:)\\S*\\n(:23B:)\\S*\\n*(:32A:([\\s\\S]*?):70:)\\S*\\D*";
        Pattern pattern = Pattern.compile(transactionPattern, Pattern.DOTALL); 
        Matcher matcher = pattern.matcher(fileContent);
        System.out.println(fileContent);

        while (matcher.find()) {
            String transactionBlock = matcher.group(0); // Entire transaction block
            Map<String, String> transactionData = parseTransactionBlock(transactionBlock);

            if (!transactionData.isEmpty()) {
                transactions.add(transactionData);
            }
        }

        return transactions;
    }

    public Map<String, String> parseTransactionBlock(String rawData) {
        Map<String, String> data = new HashMap<>();

        rawData = rawData.replace("\r\n", "\n").replace("\r", "\n");
        // Extract transaction number from `:20:`
        String transactionNo = extractField(rawData, ":20:",  "(.*?)(?=\\n:|$)");
        data.put("transactionNo", transactionNo);
        String exchangeCode = "";
        if(transactionNo.toLowerCase().startsWith("ft"))   exchangeCode = "7010204";
        else exchangeCode = "7119";
        data.put("exchangeCode", exchangeCode);
         // Extract amount and currency from `:32A:`
        String amountCurrency = extractField(rawData, ":32A:", "\\d{6}BDT[0-9,]+");
        if (amountCurrency != null) {
            String[] parts = amountCurrency.split("BDT");
            data.put("enteredDate", parts[0]); // First 6 digits are the date
            data.put("amount", parts[1].replace(",", ".")); // Remove commas for numeric amount
            data.put("currency", "BDT");
        }

        // Extract remitter name from `:50K:`
        String remitterBlock = extractField(rawData, ":50K:", "(?s).*?(?=:\\d{2}|\\z)");
        if (remitterBlock != null) {
            String[] remitterDetails = remitterBlock.split("\\n");
            data.put("remitterName", remitterDetails.length > 1 ? remitterDetails[1].trim() : "");
        }

        // Extract branch code from `:57A:`
        String branchCode = extractField(rawData, ":57A:", "(?s).*?(?=:\\d{2}|\\z)");
        data.put("branchCode", branchCode != null ? branchCode.replaceAll("\\s+", " ").trim() : "");
        // Extract bank name and branch name from `:57D:`
        String bankDetails = extractField(rawData, ":57D:", "(?s).*?(?=:\\d{2}|$)");
        if (bankDetails != null) {
            String[] bankLines = bankDetails.trim().split("\\n", 2);
            if (bankLines.length > 0 && bankLines[0].contains("-")) {
                data.put("bankName", bankLines[0].split("-", 2)[1].trim());
            }
            data.put("branchName", bankLines.length > 1 ? bankLines[1].trim() : "");
        }
    
        String beneficiaryBlock = extractField(rawData, ":59:", "(?s).*?(?=:\\d{2}|$\\\\z)");
                
        if (beneficiaryBlock != null) {
            String[] beneficiaryDetails = beneficiaryBlock.replace("/", "").trim().split("\\n", 2);
            data.put("beneficiaryAccount", beneficiaryDetails[0].replaceAll("[^0-9]", "").trim());
            data.put("beneficiaryName", beneficiaryDetails.length > 1 ? beneficiaryDetails[1].trim() : "");
        }
 
        // Extract purpose of remittance from `:70:`
        String purpose = extractField(rawData, ":70:", "(?s).*?(?=:\\d{2}|$)");
        data.put("purposeOfRemittance", purpose != null ? purpose.trim() : "");
        data.put("branchName","");
        String[] fields = {"remitterMobile","beneficiaryMobile","bankCode","draweeBranchName","draweeBranchCode","sourceOfIncome","processFlag","processedBy","processedDate"};
        for(String field: fields)   data.put(field, "");
        System.out.println(data); // Debugging output
        return data;
    }

    // Helper function to extract field values based on prefix and regex
     private String extractField(String rawData, String fieldTag, String regex) {
        String pattern = fieldTag + regex;
        Pattern compiledPattern = Pattern.compile(pattern, Pattern.MULTILINE);
        Matcher matcher = compiledPattern.matcher(rawData);
         return matcher.find() ? matcher.group().replace(fieldTag, "").trim() : null;
    } 

}
