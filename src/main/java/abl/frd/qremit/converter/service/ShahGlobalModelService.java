package abl.frd.qremit.converter.service;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;

import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import abl.frd.qremit.converter.model.ErrorDataModel;
import abl.frd.qremit.converter.model.FileInfoModel;
import abl.frd.qremit.converter.model.ShahGlobalModel;
import abl.frd.qremit.converter.model.User;
import abl.frd.qremit.converter.repository.AccountPayeeModelRepository;
import abl.frd.qremit.converter.repository.BeftnModelRepository;
import abl.frd.qremit.converter.repository.CocModelRepository;
import abl.frd.qremit.converter.repository.ExchangeHouseModelRepository;
import abl.frd.qremit.converter.repository.FileInfoModelRepository;
import abl.frd.qremit.converter.repository.OnlineModelRepository;
import abl.frd.qremit.converter.repository.ShahGlobalModelRepository;
import abl.frd.qremit.converter.repository.UserModelRepository;
@SuppressWarnings("unchecked")
@Service
public class ShahGlobalModelService {
    @Autowired
    ShahGlobalModelRepository shahGlobalModelRepository;
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
    CommonService commonService;
    public Map<String, Object> save(MultipartFile file, int userId, String exchangeCode, String nrtaCode) {
        Map<String, Object> resp = new HashMap<>();
        LocalDateTime currentDateTime = CommonService.getCurrentDateTime();
        try{
            FileInfoModel fileInfoModel = new FileInfoModel();
            fileInfoModel.setUserModel(userModelRepository.findByUserId(userId));
            User user = userModelRepository.findByUserId(userId);
            fileInfoModel.setExchangeCode(exchangeCode);
            fileInfoModel.setFileName(file.getOriginalFilename());
            fileInfoModel.setUploadDateTime(currentDateTime);
            fileInfoModelRepository.save(fileInfoModel);
            Map<String, Object> shahGlobalData = new HashMap<>();
            shahGlobalData = xlsToShahGlobalModels(file.getInputStream(), user, fileInfoModel, exchangeCode, nrtaCode, currentDateTime);

            List<ShahGlobalModel> shahGlobalModels = (List<ShahGlobalModel>) shahGlobalData.get("shahGlobalDataModelList");

            if(shahGlobalData.containsKey("errorMessage")){
                resp.put("errorMessage", shahGlobalData.get("errorMessage"));
            }
            if(shahGlobalData.containsKey("errorCount") && ((Integer) shahGlobalData.get("errorCount") >= 1)){
                int errorCount = (Integer) shahGlobalData.get("errorCount");
                fileInfoModel.setErrorCount(errorCount);
                resp.put("fileInfoModel", fileInfoModel);
                fileInfoModelRepository.save(fileInfoModel);
            }

            if(shahGlobalModels.size()!=0) {
                for(ShahGlobalModel shahGlobalModel : shahGlobalModels){
                    shahGlobalModel.setFileInfoModel(fileInfoModel);
                    shahGlobalModel.setUserModel(user);
                }
                // 4 DIFFERENTS DATA TABLE GENERATION GOING ON HERE
                Map<String, Object> convertedDataModels = commonService.generateFourConvertedDataModel(shahGlobalModels, fileInfoModel, user, currentDateTime, 0);
                fileInfoModel = CommonService.countFourConvertedDataModel(convertedDataModels);
                fileInfoModel.setTotalCount(String.valueOf(shahGlobalModels.size()));
                fileInfoModel.setIsSettlement(0);
                fileInfoModel.setShahGlobalModel(shahGlobalModels);
   
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

    public Map<String, Object> xlsToShahGlobalModels(InputStream is, User user, FileInfoModel fileInfoModel, String exchangeCode, String nrtaCode, LocalDateTime currentDateTime){
        Map<String, Object> resp = new HashMap<>();
        Optional<ShahGlobalModel> duplicateData;
        try{
            Workbook records = CommonService.getWorkbook(is);
            Row row;
            Sheet worksheet = records.getSheetAt(0);
            List<ShahGlobalModel> shahGlobalDataModelList = new ArrayList<>();
            List<ErrorDataModel> errorDataModelList = new ArrayList<>();
            List<String> transactionList = new ArrayList<>();
            String duplicateMessage = "";
            int i = 0;
            int duplicateCount = 0;
            for (int rowIndex = 1; rowIndex <= worksheet.getLastRowNum(); rowIndex++) {
                row = worksheet.getRow(rowIndex);
                if(row == null) continue;
                if (row.getCell(0) == null || row.getCell(0).getCellType() == CellType.BLANK) continue;
                i++;
                Map<String, Object> data = getBeftnData(row, exchangeCode);
                String transactionNo = data.get("transactionNo").toString();
                String amount = data.get("amount").toString();
                String beneficiaryAccount = data.get("beneficiaryAccount").toString();
                String bankName = data.get("bankName").toString();
                String branchCode = data.get("branchCode").toString();
                duplicateData = shahGlobalModelRepository.findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(transactionNo, CommonService.convertStringToDouble(amount), exchangeCode);
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

                ShahGlobalModel shahGlobalDataModel = new ShahGlobalModel();
                shahGlobalDataModel = CommonService.createDataModel(shahGlobalDataModel, data);
                shahGlobalDataModel.setTypeFlag(CommonService.setTypeFlag(beneficiaryAccount, bankName, branchCode));
                shahGlobalDataModel.setUploadDateTime(currentDateTime);
                shahGlobalDataModelList.add(shahGlobalDataModel);
            }
            //save error data
            Map<String, Object> saveError = errorDataModelService.saveErrorModelList(errorDataModelList);
            if(saveError.containsKey("errorCount")) resp.put("errorCount", saveError.get("errorCount"));
            if(saveError.containsKey("errorMessage")){
                resp.put("errorMessage", saveError.get("errorMessage"));
                return resp;
            }
            //if both model is empty then delete fileInfoModel
            if(errorDataModelList.isEmpty() && shahGlobalDataModelList.isEmpty()){
                fileInfoModelService.deleteFileInfoModelById(fileInfoModel.getId());
            }
            resp.put("shahGlobalDataModelList", shahGlobalDataModelList);
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
    
    public Map<String, Object> getBeftnData(Row row, String exchangeCode){
        Map<String, Object> data = new HashMap<>();
        String[] fields = {"draweeBranchName","draweeBranchCode","sourceOfIncome","processFlag","processedBy","processedDate","remitterMobile"};
        String branchCode = CommonService.fixRoutingNo(CommonService.getCellValueAsString(row.getCell(9)));
        String bankName = CommonService.getCellValueAsString(row.getCell(6));
        String bankCode = "";
        String branchName = CommonService.getCellValueAsString(row.getCell(8));
        String amount = CommonService.getCellValueAsString(row.getCell(3));

        data.put("exchangeCode", exchangeCode);
        data.put("transactionNo", CommonService.getCellValueAsString(row.getCell(1)));
        data.put("currency", "BDT");
        data.put("amount", amount);
        data.put("enteredDate", CommonService.getCellValueAsString(row.getCell(2)));
        data.put("remitterName", CommonService.getCellValueAsString(row.getCell(10)));
        data.put("beneficiaryName", CommonService.getCellValueAsString(row.getCell(4)));
        data.put("beneficiaryAccount", CommonService.getCellValueAsString(row.getCell(5)));
        data.put("bankName", bankName);
        data.put("bankCode", bankCode);
        data.put("branchName", branchName);
        data.put("branchCode", branchCode);
        data.put("beneficiaryMobile", CommonService.getCellValueAsString(row.getCell(12)));
        data.put("purposeOfRemittance", CommonService.getCellValueAsString(row.getCell(15)));
        for(String field: fields)   data.put(field, "");

        return data;
    }
}
