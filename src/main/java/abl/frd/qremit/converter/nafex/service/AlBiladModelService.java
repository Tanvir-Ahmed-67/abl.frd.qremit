package abl.frd.qremit.converter.nafex.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import abl.frd.qremit.converter.nafex.model.AlBiladModel;
import abl.frd.qremit.converter.nafex.model.ErrorDataModel;
import abl.frd.qremit.converter.nafex.model.FileInfoModel;
import abl.frd.qremit.converter.nafex.model.User;
import abl.frd.qremit.converter.nafex.repository.AccountPayeeModelRepository;
import abl.frd.qremit.converter.nafex.repository.AlBiladModelRepository;
import abl.frd.qremit.converter.nafex.repository.BeftnModelRepository;
import abl.frd.qremit.converter.nafex.repository.CocModelRepository;
import abl.frd.qremit.converter.nafex.repository.ExchangeHouseModelRepository;
import abl.frd.qremit.converter.nafex.repository.FileInfoModelRepository;
import abl.frd.qremit.converter.nafex.repository.OnlineModelRepository;
import abl.frd.qremit.converter.nafex.repository.UserModelRepository;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import org.apache.commons.csv.*;
@SuppressWarnings("unchecked")
@Service
public class AlBiladModelService {
    @Autowired
    AlBiladModelRepository alBiladModelRepository;
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

            Map<String, Object> alBiladData = csvToAlBiladModels(file.getInputStream(), user, fileInfoModel, exchangeCode, nrtaCode, currentDateTime);
            List<AlBiladModel> alBiladModels = (List<AlBiladModel>) alBiladData.get("alBiladModelList");

            if(alBiladData.containsKey("errorMessage")){
                resp.put("errorMessage", alBiladData.get("errorMessage"));
            }
            if(alBiladData.containsKey("errorCount") && ((Integer) alBiladData.get("errorCount") >= 1)){
                int errorCount = (Integer) alBiladData.get("errorCount");
                fileInfoModel.setErrorCount(errorCount);
                resp.put("fileInfoModel", fileInfoModel);
                fileInfoModelRepository.save(fileInfoModel);
            }
            if(alBiladModels.size()!=0) {
                for (AlBiladModel alBiladModel : alBiladModels) {
                    alBiladModel.setFileInfoModel(fileInfoModel);
                    alBiladModel.setUserModel(user);
                }
                // 4 DIFFERENTS DATA TABLE GENERATION GOING ON HERE
                Map<String, Object> convertedDataModels = CommonService.generateFourConvertedDataModel(alBiladModels, fileInfoModel, user, currentDateTime, 0);
                fileInfoModel = CommonService.countFourConvertedDataModel(convertedDataModels);
                fileInfoModel.setTotalCount(String.valueOf(alBiladModels.size()));
                fileInfoModel.setIsSettlement(0);
                fileInfoModel.setAlBiladModel(alBiladModels);
   
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

    public Map<String, Object> csvToAlBiladModels(InputStream is, User user, FileInfoModel fileInfoModel, String exchangeCode, String nrtaCode, LocalDateTime currentDateTime){
        Map<String, Object> resp = new HashMap<>();
        Optional<AlBiladModel> duplicateData;
        try(BufferedReader fileReader= new BufferedReader(new InputStreamReader(is, "UTF-8"))){
            String line;
            List<AlBiladModel> alBiladModelList = new ArrayList<>();
            List<ErrorDataModel> errorDataModelList = new ArrayList<>();
            List<String> transactionList = new ArrayList<>();
            String duplicateMessage = "";
            int i = 0;
            int duplicateCount = 0;
            while ((line = fileReader.readLine()) != null) {
                if(!line.startsWith("D"))   continue;
                i++;
                Map<String, Object> data = processData(line, exchangeCode);
                //System.out.println(data);
                String transactionNo = data.get("transactionNo").toString();
                String amount = data.get("amount").toString();
                duplicateData = alBiladModelRepository.findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(transactionNo, CommonService.convertStringToDouble(amount), exchangeCode);
                String beneficiaryAccount = data.get("beneficiaryAccount").toString();
                String bankName = data.get("bankName").toString();
                String branchCode = CommonService.fixRoutingNo(data.get("branchCode").toString());
                
                Map<String, Object> errResp = CommonService.checkError(data, errorDataModelList, nrtaCode, fileInfoModel, user, currentDateTime, nrtaCode, duplicateData, transactionList);
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
                AlBiladModel alBiladModel = new AlBiladModel();
                alBiladModel = CommonService.createDataModel(alBiladModel, data);
                alBiladModel.setTypeFlag(CommonService.setTypeFlag(beneficiaryAccount, bankName, branchCode));
                alBiladModel.setUploadDateTime(currentDateTime);
                alBiladModelList.add(alBiladModel);
            }
            //save error data
            Map<String, Object> saveError = errorDataModelService.saveErrorModelList(errorDataModelList);
            if(saveError.containsKey("errorCount")) resp.put("errorCount", saveError.get("errorCount"));
            if(saveError.containsKey("errorMessage")){
                resp.put("errorMessage", saveError.get("errorMessage"));
                return resp;
            }
            //if both model is empty then delete fileInfoModel
            if(errorDataModelList.isEmpty() && alBiladModelList.isEmpty()){
                fileInfoModelService.deleteFileInfoModelById(fileInfoModel.getId());
            }
            resp.put("alBiladModelList", alBiladModelList);
            if(!resp.containsKey("errorMessage")){
                resp.put("errorMessage", CommonService.setErrorMessage(duplicateMessage, duplicateCount, i));
            }
        }catch(IOException e){
            String message = "fail to store csv data: " + e.getMessage();
            resp.put("errorMessage", message);
            throw new RuntimeException(message);
        }
        return resp;
    }

    public Map<String, Object> processData(String line, String exchangeCode){
        Map<String, Object> data = new HashMap<>();
        String transactionNo = line.substring(7,23).trim();
        String enteredDate = line.substring(23,31).trim();
        Double amount = Double.parseDouble(line.substring(31, 49).trim());
        String currency = line.substring(49, 52).trim();
        String remitterName = line.substring(55, 90).trim();
        String branchName = line.substring(230, 265).trim();
        String branchCode = line.substring(265, 300).trim();
        String bankStr = line.substring(300,335).trim();
        String branch2 = line.substring(580, 615).trim();
        Map<String, Object> bank = getBankDetails(bankStr,branchName, branchCode, branch2);
        String beneficiaryName = line.substring(440, 545).trim();
        String beneficiaryAccount = line.substring(545, 580).trim();
        String sourceOfIncome = line.substring(755, 790).trim();

        data.put("exchangeCode", exchangeCode);
        data.put("transactionNo", transactionNo);
        data.put("enteredDate", enteredDate);
        data.put("amount", String.valueOf(amount));
        data.put("currency", currency);
        data.put("remitterName", remitterName);
        data.put("branchName", bank.get("branchName").toString());
        data.put("branchCode", branchCode);
        data.put("bankName", bank.get("bankName").toString());
        data.put("bankCode", bank.get("bankCode").toString());
        data.put("beneficiaryName", beneficiaryName);
        data.put("beneficiaryAccount", beneficiaryAccount);
        data.put("remitterMobile", "");
        data.put("beneficiaryMobile","");
        data.put("draweeBranchName", "");
        data.put("draweeBranchCode", "");
        data.put("purposeOfRemittance", "");
        data.put("sourceOfIncome", sourceOfIncome);
        data.put("processFlag", "");
        data.put("processedBy", "");
        data.put("processedDate", "");
        return data;
    }

    public Map<String, Object> getBankDetails(String bankStr, String branchName, String branchCode, String branch2){
        Map<String, Object> resp = new HashMap<>();
        String bankName = "Agrani Bank";
        String bankCode = "11";
        if(!bankStr.isEmpty() && !CommonService.checkAgraniBankName(bankStr)){
            bankName = bankStr;
            bankCode = "";
        }
        branchName = getBranchName(branchName, branch2);
        resp.put("bankName", bankName);
        resp.put("bankCode", bankCode);
        resp.put("branchName", branchName);
        return resp;
    }

    public String getBranchName(String branchName, String branch2){
        String search = "remittance-to-3rd-party-bank";
        String search2 = "remittance to 3rd party bank";
        if(branchName.toLowerCase().contains(search) || branchName.toLowerCase().contains(search2)){
            branchName = branch2;
        }
        return branchName;
    }

}
