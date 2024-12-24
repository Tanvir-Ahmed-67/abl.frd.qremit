package abl.frd.qremit.converter.service;
import java.io.*;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import abl.frd.qremit.converter.helper.RepositoryModelWrapper;
import abl.frd.qremit.converter.model.ErrorDataModel;
import abl.frd.qremit.converter.model.FileInfoModel;
import abl.frd.qremit.converter.model.SwiftModel;
import abl.frd.qremit.converter.model.User;
import abl.frd.qremit.converter.repository.AccountPayeeModelRepository;
import abl.frd.qremit.converter.repository.AlBiladModelRepository;
import abl.frd.qremit.converter.repository.AlRajiModelRepository;
import abl.frd.qremit.converter.repository.BeftnModelRepository;
import abl.frd.qremit.converter.repository.CocModelRepository;
import abl.frd.qremit.converter.repository.ExchangeHouseModelRepository;
import abl.frd.qremit.converter.repository.FileInfoModelRepository;
import abl.frd.qremit.converter.repository.SwiftModelRepository;
import abl.frd.qremit.converter.repository.OnlineModelRepository;
import abl.frd.qremit.converter.repository.UserModelRepository;
import java.util.*;
import java.util.regex.*;

@SuppressWarnings("unchecked")
@Service
public class SwiftModelService {
    @Autowired
    AlBiladModelRepository alBiladModelRepository;
    @Autowired
    AlRajiModelRepository alRajiModelRepository;
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
    @Autowired
    DynamicOperationService dynamicOperationService;
    @Autowired
    SwiftModelRepository swiftModelRepository;
    @Autowired
    CustomQueryService customQueryService;
    @Autowired
    CommonService commonService;
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
                Map<String, Object> convertedDataModels = commonService.generateFourConvertedDataModel(swiftModels, fileInfoModel, user, currentDateTime, 0);
                fileInfoModel = CommonService.countFourConvertedDataModel(convertedDataModels);
                fileInfoModel.setTotalCount(String.valueOf(swiftModels.size()));
                fileInfoModel.setIsSettlement(0);
                fileInfoModel.setSwiftModel(swiftModels);
   
                // SAVING TO MySql Data Table
                try{
                    FileInfoModel savedFileInfoModel = fileInfoModelRepository.save(fileInfoModel);
                    if(savedFileInfoModel != null){
                        transferToIndividualExchange(swiftModels);
                    }
                              
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
        Optional<SwiftModel> duplicateData;

        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"))){
            StringBuilder swiftContent = new StringBuilder();
            String line;
            while ((line = fileReader.readLine()) != null) {
                swiftContent.append(line).append("\n");
            }
            List<Map<String, Object>> transactions = extractTransactions(swiftContent.toString());
            if (transactions.isEmpty()) {
                resp.put("errorMessage", "No valid SWIFT transactions found in the file.");
            } else {
                // Create lists to store error models and swift data models
                List<SwiftModel> swiftDataModelList = new ArrayList<>();
                List<ErrorDataModel> errorDataModelList = new ArrayList<>();
                String duplicateMessage = "";
                int duplicateCount = 0;
                List<String> transactionList = new ArrayList<>();
                for(Map<String, Object> data: transactions){
                    //System.out.println(data);
                    String transactionNo = data.get("transactionNo").toString();
                    String amount = data.get("amount").toString();
                    String beneficiaryAccount = data.get("beneficiaryAccount").toString();
                    String bankName = data.get("bankName").toString();
                    String branchCode = data.get("branchCode").toString();
                    exchangeCode = data.get("exchangeCode").toString();
                    duplicateData = swiftModelRepository.findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(transactionNo, CommonService.convertStringToDouble(amount), exchangeCode);                 
                 
                    if (duplicateData.isPresent()) {
                        duplicateMessage += "Duplicate transaction detected for: " + transactionNo + "\n";
                        duplicateCount++;
                        continue; // Skip adding this duplicate transaction
                    }
                    Map<String, Object> errResp = CommonService.checkError(data, errorDataModelList, nrtaCode, fileInfoModel, user, currentDateTime, duplicateMessage, duplicateData, transactionList);
                    //System.out.println(errResp);
                    if ((Integer) errResp.get("err") == 1) {
                        errorDataModelList = (List<ErrorDataModel>) errResp.get("errorDataModelList");
                        continue; // Skip this transaction due to errors
                    }

                    if ((Integer) errResp.get("err") == 2) {
                        resp.put("errorMessage", errResp.get("msg"));
                        break; // Critical error, stop processing
                    }

                    SwiftModel swiftDataModel = new SwiftModel();
                    swiftDataModel = CommonService.createDataModel(swiftDataModel, data);
                    swiftDataModel.setTypeFlag(CommonService.setTypeFlag(beneficiaryAccount, bankName, branchCode));
                    swiftDataModel.setUploadDateTime(currentDateTime);
                    swiftDataModelList.add(swiftDataModel);
                }

                // Save the error data models if any
                if (!errorDataModelList.isEmpty()) {
                    Map<String, Object> saveError = errorDataModelService.saveErrorModelList(errorDataModelList);
                    if (saveError.containsKey("errorCount")) resp.put("errorCount", saveError.get("errorCount"));
                    if (saveError.containsKey("errorMessage")) {
                        resp.put("errorMessage", saveError.get("errorMessage"));
                        return resp;
                    }
                }

                // If no valid data and no errors, delete the file info model
                if (swiftDataModelList.isEmpty() && errorDataModelList.isEmpty()) {
                    fileInfoModelService.deleteFileInfoModelById(fileInfoModel.getId());
                }

                // Put the valid swift data models in the response
                resp.put("swiftDataModelList", swiftDataModelList);

                // If there are any duplicates or errors, add them to the response
                if (!duplicateMessage.isEmpty()) {
                    resp.put("duplicateMessage", duplicateMessage);
                }
                resp.put("duplicateCount", duplicateCount);

            }

        }catch (IOException e) {
            resp.put("errorMessage", "fail to store csv data: " + e.getMessage());
        }
        return resp;
    }

    public void transferToIndividualExchange(List<SwiftModel> swiftModels){
        JpaRepository repository = null;
        for(SwiftModel swiftDataModel: swiftModels){
            String exchangeCode = swiftDataModel.getExchangeCode();
            Map<String, RepositoryModelWrapper<?>> modelRepositoryWrapper;
            try{
                modelRepositoryWrapper = dynamicOperationService.repositoryModelMapByExchangeCode(exchangeCode);
            }catch(ClassCastException e){
                throw new IllegalStateException("Unexpected map type from repositoryModelMapByExchangeCode", e);
            }
            RepositoryModelWrapper<?> wrapper = modelRepositoryWrapper.get(exchangeCode);
            if (wrapper != null) {
                repository = wrapper.getRepository();
                Class<?> modelClass = wrapper.getModelClass();
                // Assuming a factory method or builder is available to replace reflection
                Object modelInstance = dynamicOperationService.createModelInstanceForSwift(modelClass, swiftDataModel);
                // Example of checking for duplicates (you can modify this logic based on your needs)
                
                //repository.findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(swiftDataModel.getTransactionNo(),swiftDataModel.getAmount(),exchangeCode);
                repository.save(modelInstance);
            }
        }
    }

    private List<Map<String, Object>> extractTransactions(String fileContent) {
        List<Map<String, Object>> transactions = new ArrayList<>();
       //Define the regex pattern to find valid transactions
        String transactionPattern = "(:20:)\\S*\\n(:23B:)\\S*\\n*(:32A:([\\s\\S]*?):70:)\\S*\\D*";
        Pattern pattern = Pattern.compile(transactionPattern, Pattern.DOTALL); 
        Matcher matcher = pattern.matcher(fileContent);
        //System.out.println(fileContent);

        while (matcher.find()) {
            String transactionBlock = matcher.group(0); // Entire transaction block
            Map<String, Object> transactionData = parseTransactionBlock(transactionBlock);

            if (!transactionData.isEmpty()) {
                transactions.add(transactionData);
            }
        }

        return transactions;
    }

    public Map<String, Object> parseTransactionBlock(String rawData) {
        Map<String, Object> data = new HashMap<>();

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
        String branchCodeStr = extractField(rawData, ":57A:", "(?s).*?(?=:\\d{2}|\\z)");
        String branchCode = branchCodeStr != null ? branchCodeStr.replaceAll("\\s+", " ").trim() : "";
        
        // Extract bank name and branch name from `:57D:`
        String bankDetails = extractField(rawData, ":57D:", "(?s).*?(?=:\\d{2}|$)");
        String bankName = "";
        String branchName = "";
        String bankCode = "";
        if (bankDetails != null) {
            String[] bankLines = bankDetails.trim().split("\\n", 2);
            if (bankLines.length > 0 && bankLines[0].contains("-")) {
                bankName =  bankLines[0].split("-", 2)[1].trim();
            }
            branchName = bankLines.length > 1 ? bankLines[1].trim() : "";
        }
        if(bankName.isEmpty()){
            if(branchCode.toUpperCase().startsWith("AGBKBD")){
                bankName = "Agrani Bank";
                Map<String, Object> branchDetails = customQueryService.getBranchDetailsFromSwiftCode(branchCode);
                if(!branchDetails.isEmpty()){
                    branchCode = branchDetails.get("branch_code").toString();
                    branchName = branchDetails.get("branch_name").toString();
                }
                bankCode = "11";
            }   
        }
        data.put("bankName", bankName);
        data.put("branchName", branchName);
        data.put("branchCode", branchCode);
        data.put("bankCode", bankCode);
    
        String beneficiaryBlock = extractField(rawData, ":59:", "(?s).*?(?=:\\d{2}|$\\\\z)");
                
        if (beneficiaryBlock != null) {
            String[] beneficiaryDetails = beneficiaryBlock.replace("/", "").trim().split("\\n", 2);
            data.put("beneficiaryAccount", beneficiaryDetails[0].replaceAll("[^0-9]", "").trim());
            data.put("beneficiaryName", beneficiaryDetails.length > 1 ? beneficiaryDetails[1].trim() : "");
        }
 
        // Extract purpose of remittance from `:70:`
        String purpose = extractField(rawData, ":70:", "(?s).*?(?=:\\d{2}|$)");
        data.put("purposeOfRemittance", purpose != null ? purpose.trim() : "");
       // data.put("branchName","");
        String[] fields = {"remitterMobile","beneficiaryMobile","draweeBranchName","draweeBranchCode","sourceOfIncome","processFlag","processedBy","processedDate"};
        for(String field: fields)   data.put(field, "");
        //System.out.println(data); // Debugging output
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
