package abl.frd.qremit.converter.nafex.service;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import abl.frd.qremit.converter.nafex.model.CityModel;
import abl.frd.qremit.converter.nafex.model.ErrorDataModel;
import abl.frd.qremit.converter.nafex.model.FileInfoModel;
import abl.frd.qremit.converter.nafex.model.User;
import abl.frd.qremit.converter.nafex.repository.AccountPayeeModelRepository;
import abl.frd.qremit.converter.nafex.repository.BeftnModelRepository;
import abl.frd.qremit.converter.nafex.repository.CityModelRepository;
import abl.frd.qremit.converter.nafex.repository.CocModelRepository;
import abl.frd.qremit.converter.nafex.repository.ExchangeHouseModelRepository;
import abl.frd.qremit.converter.nafex.repository.FileInfoModelRepository;
import abl.frd.qremit.converter.nafex.repository.OnlineModelRepository;
import abl.frd.qremit.converter.nafex.repository.UserModelRepository;
@SuppressWarnings("unchecked")
@Service
public class CityModelService {
    @Autowired
    CityModelRepository cityModelRepository;
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
        try{
            FileInfoModel fileInfoModel = new FileInfoModel();
            fileInfoModel.setUserModel(userModelRepository.findByUserId(userId));
            User user = userModelRepository.findByUserId(userId);
            fileInfoModel.setExchangeCode(exchangeCode);
            fileInfoModel.setFileName(file.getOriginalFilename());
            fileInfoModel.setUploadDateTime(currentDateTime);
            fileInfoModelRepository.save(fileInfoModel);
            Map<String, Object> cityData = new HashMap<>();
            cityData = xlsToCityModels(file.getInputStream(), user, fileInfoModel, exchangeCode, nrtaCode, currentDateTime);

            List<CityModel> cityModels = (List<CityModel>) cityData.get("cityDataModelList");

            if(cityData.containsKey("errorMessage")){
                resp.put("errorMessage", cityData.get("errorMessage"));
            }
            if(cityData.containsKey("errorCount") && ((Integer) cityData.get("errorCount") >= 1)){
                int errorCount = (Integer) cityData.get("errorCount");
                fileInfoModel.setErrorCount(errorCount);
                resp.put("fileInfoModel", fileInfoModel);
                fileInfoModelRepository.save(fileInfoModel);
            }

            if(cityModels.size()!=0) {
                for(CityModel cityModel : cityModels){
                    cityModel.setFileInfoModel(fileInfoModel);
                    cityModel.setUserModel(user);
                }
                // 4 DIFFERENTS DATA TABLE GENERATION GOING ON HERE
                Map<String, Object> convertedDataModels = CommonService.generateFourConvertedDataModel(cityModels, fileInfoModel, user, currentDateTime, 0);
                fileInfoModel = CommonService.countFourConvertedDataModel(convertedDataModels);
                fileInfoModel.setTotalCount(String.valueOf(cityModels.size()));
                fileInfoModel.setIsSettlement(0);
                fileInfoModel.setCityModel(cityModels);
   
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

    public Map<String, Object> xlsToCityModels(InputStream is, User user, FileInfoModel fileInfoModel, String exchangeCode, String nrtaCode, LocalDateTime currentDateTime){
        Map<String, Object> resp = new HashMap<>();
        Optional<CityModel> duplicateData;
        try{
            Workbook records = CommonService.getWorkbook(is);
            Row row;
            Sheet worksheet = records.getSheetAt(0);
            List<CityModel> cityDataModelList = new ArrayList<>();
            List<ErrorDataModel> errorDataModelList = new ArrayList<>();
            List<String> transactionList = new ArrayList<>();
            String duplicateMessage = "";
            int i = 0;
            int duplicateCount = 0;
            row = worksheet.getRow(3);
            String enteredDate = "";
            enteredDate = CommonService.getCellValueAsString(row.getCell(0));
            enteredDate = enteredDate.replace("DATE:", "").trim();
            

            for (int rowIndex = 9; rowIndex <= worksheet.getLastRowNum(); rowIndex++) {
                row = worksheet.getRow(rowIndex);
                if(row == null) continue;
                if (row.getCell(0) == null || row.getCell(0).getCellType() == CellType.BLANK) continue;
                int columnCount = getNonEmptyCellCount(row);
                if(columnCount != 9)    continue;
                i++;
                Map<String, Object> data = getBeftnData(row, exchangeCode, enteredDate);
                String transactionNo = data.get("transactionNo").toString();
                String amount = data.get("amount").toString();
                String beneficiaryAccount = data.get("beneficiaryAccount").toString();
                String bankName = data.get("bankName").toString();
                String branchCode = data.get("branchCode").toString();
                duplicateData = cityModelRepository.findByTransactionNoIgnoreCaseAndAmountAndExchangeCode(transactionNo, CommonService.convertStringToDouble(amount), exchangeCode);
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

                CityModel cityDataModel = new CityModel();
                cityDataModel = CommonService.createDataModel(cityDataModel, data);
                cityDataModel.setTypeFlag(CommonService.setTypeFlag(beneficiaryAccount, bankName, branchCode));
                cityDataModel.setUploadDateTime(currentDateTime);
                cityDataModelList.add(cityDataModel);
            }
            //save error data
            Map<String, Object> saveError = errorDataModelService.saveErrorModelList(errorDataModelList);
            if(saveError.containsKey("errorCount")) resp.put("errorCount", saveError.get("errorCount"));
            if(saveError.containsKey("errorMessage")){
                resp.put("errorMessage", saveError.get("errorMessage"));
                return resp;
            }
            //if both model is empty then delete fileInfoModel
            if(errorDataModelList.isEmpty() && cityDataModelList.isEmpty()){
                fileInfoModelService.deleteFileInfoModelById(fileInfoModel.getId());
            }
            resp.put("cityDataModelList", cityDataModelList);
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
    
    public Map<String, Object> getBeftnData(Row row, String exchangeCode, String enteredDate){
        Map<String, Object> data = new HashMap<>();
        String[] fields = {"draweeBranchName","draweeBranchCode","sourceOfIncome","processFlag","processedBy","processedDate","remitterMobile","beneficiaryMobile","purposeOfRemittance"};
        String branchCode = CommonService.fixRoutingNo(CommonService.getCellValueAsString(row.getCell(8)));
        String bankName = CommonService.getCellValueAsString(row.getCell(4));
        String bankCode = "";
        String branchName = CommonService.getCellValueAsString(row.getCell(5));
        String amount = CommonService.getCellValueAsString(row.getCell(6));

        data.put("exchangeCode", exchangeCode);
        data.put("transactionNo", CommonService.getCellValueAsString(row.getCell(1)));
        data.put("currency", "BDT");
        data.put("amount", amount);
        data.put("enteredDate", enteredDate);
        data.put("remitterName", CommonService.getCellValueAsString(row.getCell(7)));
        data.put("beneficiaryName", CommonService.getCellValueAsString(row.getCell(2)));
        data.put("beneficiaryAccount", CommonService.getCellValueAsString(row.getCell(3)));
        data.put("bankName", bankName);
        data.put("bankCode", bankCode);
        data.put("branchName", branchName);
        data.put("branchCode", branchCode);
        for(String field: fields)   data.put(field, "");

        return data;
    }
    private static int getNonEmptyCellCount(Row row) {
        int count = 0;
        for (Cell cell : row) {
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                count++;
            }
        }
        return count;
    }
}
