package abl.frd.qremit.converter.service;
import java.io.*;
import java.time.*;
import java.util.*;
import abl.frd.qremit.converter.model.KandHModel;
import abl.frd.qremit.converter.repository.ExchangeHouseModelRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import abl.frd.qremit.converter.model.ErrorDataModel;
import abl.frd.qremit.converter.model.FileInfoModel;
import abl.frd.qremit.converter.model.User;
import abl.frd.qremit.converter.repository.AccountPayeeModelRepository;
import abl.frd.qremit.converter.repository.BeftnModelRepository;
import abl.frd.qremit.converter.repository.KandHModelRepository;
import abl.frd.qremit.converter.repository.CocModelRepository;
import abl.frd.qremit.converter.repository.FileInfoModelRepository;
import abl.frd.qremit.converter.repository.OnlineModelRepository;
import abl.frd.qremit.converter.repository.UserModelRepository;
@SuppressWarnings("unchecked")
@Service
public class KandHModelService {
    @Autowired
    KandHModelRepository kandhModelRepository;
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
    @Autowired
    CustomQueryService customQueryService;
    public Map<String, Object> save(MultipartFile file, int userId, String exchangeCode, String nrtaCode, String tbl) {
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
            Map<String, Object> kandhData = new HashMap<>();
            kandhData = xlsToKandHModels(file.getInputStream(), user, fileInfoModel, exchangeCode, nrtaCode, currentDateTime, tbl);

            List<KandHModel> kandhModels = (List<KandHModel>) kandhData.get("kandhDataModelList");

            if(kandhData.containsKey("errorMessage")){
                resp.put("errorMessage", kandhData.get("errorMessage"));
            }
            if(kandhData.containsKey("errorCount") && ((Integer) kandhData.get("errorCount") >= 1)){
                int errorCount = (Integer) kandhData.get("errorCount");
                fileInfoModel.setErrorCount(errorCount);
                resp.put("fileInfoModel", fileInfoModel);
                fileInfoModelRepository.save(fileInfoModel);
            }

            if(kandhModels.size()!=0) {
                for(KandHModel kandhModel : kandhModels){
                    kandhModel.setFileInfoModel(fileInfoModel);
                    kandhModel.setUserModel(user);
                }
                // 4 DIFFERENTS DATA TABLE GENERATION GOING ON HERE
                Map<String, Object> convertedDataModels = commonService.generateFourConvertedDataModel(kandhModels, fileInfoModel, user, currentDateTime, 0);
                fileInfoModel = CommonService.countFourConvertedDataModel(convertedDataModels);
                fileInfoModel.setTotalCount(String.valueOf(kandhModels.size()));
                fileInfoModel.setIsSettlement(0);
                fileInfoModel.setKandhModel(kandhModels);
                
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

    public Map<String, Object> xlsToKandHModels(InputStream is, User user, FileInfoModel fileInfoModel, String exchangeCode, String nrtaCode, LocalDateTime currentDateTime, String tbl){
        Map<String, Object> resp = new HashMap<>();
        Optional<KandHModel> duplicateData = Optional.empty();
        try{
            Workbook records = CommonService.getWorkbook(is);
            Row row;
            Sheet worksheet = records.getSheetAt(0);
            List<KandHModel> kandhDataModelList = new ArrayList<>();
            List<ErrorDataModel> errorDataModelList = new ArrayList<>();
            String duplicateMessage = "";
            int duplicateCount = 0;
            int i = 0;
            List<String[]> uniqueKeys = new ArrayList<>();
            List<Map<String, Object>> dataList = new ArrayList<>();
            Map<String, Object> modelResp = new HashMap<>();
            String fileExchangeCode = "";
            int isValidFile = 1;
            for (int rowIndex = 1; rowIndex <= worksheet.getLastRowNum(); rowIndex++) {
                row = worksheet.getRow(rowIndex);
                if(row == null) continue;
                if (row.getCell(0) == null || row.getCell(0).getCellType() == CellType.BLANK) continue;
                i++;
                Map<String, Object> data = getBeftnData(row, exchangeCode);
                String transactionNo = data.get("transactionNo").toString();
                String amount = data.get("amount").toString();
                data.put("nrtaCode", nrtaCode);
                if(!CommonService.getCellValueAsString(row.getCell(0)).trim().replace(".0", "").equals(exchangeCode)){
                    resp.put("errorMessage", "Please Upload Correct File");
                    isValidFile = 0;
                    break;
                }
                fileExchangeCode = nrtaCode;
                dataList.add(data);
                uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
            }
            if(isValidFile == 1){
                Map<String, Object> uniqueDataList = customQueryService.getUniqueList(uniqueKeys, tbl);
                Map<String, Object> archiveDataList = customQueryService.processArchiveUniqueList(uniqueKeys);
                modelResp = CommonService.processDataToModel(dataList, fileInfoModel, user, uniqueDataList, archiveDataList, currentDateTime, duplicateData, KandHModel.class, resp, errorDataModelList, fileExchangeCode, 0, 0);
                kandhDataModelList = (List<KandHModel>) modelResp.get("modelList");
                errorDataModelList = (List<ErrorDataModel>) modelResp.get("errorDataModelList");
                duplicateMessage = modelResp.get("duplicateMessage").toString();
                duplicateCount = (int) modelResp.get("duplicateCount");
            }
            //save error data
            Map<String, Object> saveError = errorDataModelService.saveErrorModelList(errorDataModelList);
            if(saveError.containsKey("errorCount")) resp.put("errorCount", saveError.get("errorCount"));
            if(saveError.containsKey("errorMessage")){
                resp.put("errorMessage", saveError.get("errorMessage"));
                return resp;
            }
            //if both model is empty then delete fileInfoModel
            if(errorDataModelList.isEmpty() && kandhDataModelList.isEmpty()){
                fileInfoModelService.deleteFileInfoModelById(fileInfoModel.getId());
            }
            resp.put("kandhDataModelList", kandhDataModelList);
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
        DataFormatter dataFormatter = new DataFormatter();
        String[] fields = {"draweeBranchName","draweeBranchCode","sourceOfIncome","processFlag","processedBy","processedDate","remitterMobile","beneficiaryMobile","purposeOfRemittance"};
       
        String bankName = CommonService.getCellValueAsString(row.getCell(8));
        String bankCode = CommonService.getCellValueAsString(row.getCell(9));
        String branchName = CommonService.getCellValueAsString(row.getCell(10));
        String branchCode = CommonService.fixRoutingNo(dataFormatter.formatCellValue(row.getCell(11)));
     
        String transactionNo = dataFormatter.formatCellValue(row.getCell(1));
        String beneficiaryAccount = dataFormatter.formatCellValue(row.getCell(7));
        LocalDate date = CommonService.convertStringToLocalDate(CommonService.getCellValueAsString(row.getCell(4)), "dd/MM/yyyy");

        data.put("exchangeCode", exchangeCode);
        data.put("transactionNo", transactionNo);
        data.put("currency", "BDT");
        data.put("amount",  CommonService.getCellValueAsString(row.getCell(3)));
        data.put("enteredDate", CommonService.convertLocalDateToString(date));
        data.put("remitterName", CommonService.getCellValueAsString(row.getCell(5)));
        data.put("beneficiaryName", CommonService.getCellValueAsString(row.getCell(6)));
        data.put("beneficiaryAccount", beneficiaryAccount);
        data.put("bankName", bankName);
        data.put("bankCode", bankCode);
        data.put("branchName", branchName);
        data.put("branchCode", branchCode);
        for(String field: fields)   data.put(field, "");

        return data;
    }
}
