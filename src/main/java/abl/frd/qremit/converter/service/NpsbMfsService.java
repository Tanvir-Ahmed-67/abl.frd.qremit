package abl.frd.qremit.converter.service;
import org.apache.commons.csv.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import abl.frd.qremit.converter.model.ErrorDataModel;
import abl.frd.qremit.converter.model.ExchangeHouseModel;
import abl.frd.qremit.converter.model.FileInfoModel;
import abl.frd.qremit.converter.model.NpsbMfsModel;
import abl.frd.qremit.converter.model.User;
import abl.frd.qremit.converter.repository.ExchangeHouseModelRepository;
import abl.frd.qremit.converter.repository.FileInfoModelRepository;
import abl.frd.qremit.converter.repository.NpsbMfsRepository;
import abl.frd.qremit.converter.repository.UserModelRepository;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

@Service
@SuppressWarnings("unchecked")
public class NpsbMfsService {
    @Autowired
    UserModelRepository userModelRepository;
    @Autowired
    FileInfoModelRepository fileInfoModelRepository;
    @Autowired
    ExchangeHouseModelRepository exchangeHouseModelRepository;
    @Autowired
    CustomQueryService customQueryService;
    @Autowired
    CommonService commonService;
    @Autowired
    NpsbMfsRepository npsbMfsRepository;
    public Map<String, Object> save(MultipartFile file, int userId, String exchangeCode, String tbl){
        Map<String, Object> resp = new HashMap<>();
        LocalDateTime currentDateTime = CommonService.getCurrentDateTime();
        try{
            FileInfoModel fileInfoModel = new FileInfoModel();
            fileInfoModel.setUserModel(userModelRepository.findByUserId(userId));
            User user = userModelRepository.findByUserId(userId);
            fileInfoModel.setExchangeCode(exchangeCode);
            fileInfoModel.setFileName(file.getOriginalFilename());
            fileInfoModel.setUploadDateTime(currentDateTime);
            Map<String, Object> npsbMfsData = csvToNpsbMfsData(file.getInputStream(), user, fileInfoModel, currentDateTime, tbl);
            List<NpsbMfsModel> npsbMfsModelList = (List<NpsbMfsModel>) npsbMfsData.get("npsbMfsModelList");
            if(npsbMfsData.containsKey("errorMessage")){
                resp.put("errorMessage", npsbMfsData.get("errorMessage"));
            }
            int isProcessed = 0;
            int npsbCount = 0;
            int mfsCount = 0;
            //int spotCashCount = 0;
            double totalAmount = 0;
            for(NpsbMfsModel npsbMfsModel: npsbMfsModelList){
                if(("6").equals(npsbMfsModel.getTypeFlag())){
                    isProcessed = 1;
                    npsbCount += 1;
                    totalAmount += npsbMfsModel.getAmount();
                }
                if(isProcessed == 1){
                    npsbMfsModel.setIsProcessed(isProcessed);
                    npsbMfsModel.setIsDownloaded(isProcessed);
                    npsbMfsModel.setDownloadDateTime(currentDateTime);
                    npsbMfsModel.setDownloadUserId(userId);
                    npsbMfsModel.setFileInfoModel(fileInfoModel);
                    npsbMfsModel.setUserModel(user);
                }
            }
            Map<String, Object> convertedData = new HashMap<>();
            convertedData.put("npsbCount", npsbCount);
            convertedData.put("mfsCount", mfsCount);
            fileInfoModel = CommonService.setCountForFileInfoModel(fileInfoModel, convertedData);
            fileInfoModel.setTotalAmount(CommonService.convertNumberFormat(totalAmount, 2));
            fileInfoModel.setIsSettlement(1);
            fileInfoModel.setNpsbMfsModelList(npsbMfsModelList);
            // SAVING TO MySql Data Table
            try{
                fileInfoModelRepository.save(fileInfoModel);                
                resp.put("fileInfoModel", fileInfoModel);
            }catch(Exception e){
                resp.put("errorMessage", e.getMessage());
            }

        }
        catch(Exception e){
            String message = "fail to store csv data: " + e.getMessage();
            resp.put("errorMessage", message);
            throw new RuntimeException(message);
        }
        return resp;
    }

    public Map<String, Object> csvToNpsbMfsData(InputStream is, User user, FileInfoModel fileInfoModel, LocalDateTime currentDateTime, String tbl){
        Map<String, Object> resp = new HashMap<>();
        Optional<NpsbMfsModel> duplicateData = Optional.empty();
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withDelimiter(',').withQuote('"').withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            String type = "";
            List<ExchangeHouseModel> exchangeHouseModelList = exchangeHouseModelRepository.findAllActiveExchangeHouseList();
            Map<String, String> nrtaCodeVsExchangeCodeMap = CommonService.getNrtaCodeVsExchangeCodeMap(exchangeHouseModelList);
            List<NpsbMfsModel> npsbMfsModelList = new ArrayList<>();
            List<ErrorDataModel> errorDataModelList = new ArrayList<>();
            String duplicateMessage = "";
            int duplicateCount = 0;
            List<String[]> uniqueKeys = new ArrayList<>();
            List<Map<String, Object>> dataList = new ArrayList<>();
            Map<String, Object> modelResp = new HashMap<>();
            int i= 0;
            for (CSVRecord csvRecord : csvRecords) {
                String trMode = csvRecord.get(12).toString();
                if(CommonService.checkNpsb(trMode)){
                    //for npsb
                    type = "6";
                }else   continue;
                String nrtaCode = csvRecord.get(0).trim();
                String exchangeCode = nrtaCodeVsExchangeCodeMap.get(nrtaCode);
                Map<String, Object> data = getCsvData(csvRecord, exchangeCode, nrtaCode, type);
                dataList.add(data);
                String transactionNo = data.get("transactionNo").toString();
                String amount = data.get("amount").toString();
                uniqueKeys = CommonService.setUniqueIndexList(transactionNo, amount, exchangeCode, uniqueKeys);
                i++;
            }
            if(dataList.isEmpty()){
                resp.put("errorMessage", "No data found for processing");
                return resp;
            }
            Map<String, Object> uniqueDataList = customQueryService.getUniqueList(uniqueKeys, tbl);
            Map<String, Object> archiveDataList = customQueryService.processArchiveUniqueList(uniqueKeys);
            modelResp = commonService.processDataToModel(dataList, fileInfoModel, user, uniqueDataList, archiveDataList, currentDateTime, duplicateData, NpsbMfsModel.class, resp, errorDataModelList, "",0,0);
            npsbMfsModelList = (List<NpsbMfsModel>) modelResp.get("modelList");
            //no need to check errorDataModelList as it is settlement
            //errorDataModelList = (List<ErrorDataModel>) modelResp.get("errorDataModelList");
            duplicateMessage = modelResp.get("duplicateMessage").toString();
            duplicateCount = (int) modelResp.get("duplicateCount");
            
            resp.put("npsbMfsModelList", npsbMfsModelList);
            
            if(!resp.containsKey("errorMessage")){
                resp.put("errorMessage", CommonService.setErrorMessage(duplicateMessage, duplicateCount, i));
            }
        } catch (IOException e) {
            String message = "fail to store csv data: " + e.getMessage();
            resp.put("errorMessage", message);
            throw new RuntimeException(message);
        }
        return resp;
    }

    public Map<String, Object> getCsvData(CSVRecord csvRecord, String exchangeCode, String nrtaCode, String type){
        String branchCode = CommonService.fixRoutingNo(csvRecord.get(8).trim());
        String bankName = ""; 
        String bankCode = ""; 
        String branchName = "";
        if(type.equals("6")){
            /*
            routing to bank details
            if(!branchCode.isEmpty())    routingMap = customQueryService.getRoutingDetailsByRoutingNo(branchCode);
            bankName = (routingMap.containsKey("bank_name")) ? routingMap.get("bank_name").toString(): "";
            bankCode = (routingMap.containsKey("bank_code")) ? routingMap.get("bank_code").toString(): "";;
            branchName = (routingMap.containsKey("branch_name")) ? routingMap.get("branch_name").toString(): "";
            //String branchCode = (routingMap.containsKey("abl_branch_code")) ? routingMap.get("abl_branch_code").toString(): "";
            */
        }
        
        LocalDateTime enteredDate = CommonService.convertStringToDate(csvRecord.get(3).trim());
        LocalDateTime paidDate = CommonService.convertStringToDate(csvRecord.get(11).trim());
        Map<String, Object> data = new HashMap<>();
        data.put("typeFlag", type);
        data.put("exchangeCode", exchangeCode);
        data.put("nrtaCode", nrtaCode);
        data.put("transactionNo", csvRecord.get(1).trim());
        data.put("amount", csvRecord.get(4).trim());
        data.put("enteredDate", enteredDate.toLocalDate().toString());
        data.put("paidDate", paidDate);
        data.put("remitterName", csvRecord.get(5).trim());
        data.put("beneficiaryName", csvRecord.get(6).trim());
        data.put("beneficiaryAccount", csvRecord.get(7).trim());
        data.put("beneficiaryMobile", csvRecord.get(10).trim());
        data.put("bankName", bankName);
        data.put("bankCode", bankCode);
        data.put("branchName", branchName);
        data.put("branchCode", branchCode);
        data.put("currency", "BDT");
        String[] fields = {"remitterMobile","beneficiaryMobile","sourceOfIncome","purposeOfRemittance"};
        for(String field: fields)   data.put(field, "");
        return data;
    }

    public List<NpsbMfsModel> getDataByTransactionNoOrBenificiaryAccount(String type, String searchValue){
        List<NpsbMfsModel> npsbMfsModelList = new ArrayList<>();
        switch (type) {
            case "1":
                npsbMfsModelList = npsbMfsRepository.findNpsbMfsModelByTransactionNo(searchValue);
                break;
            case "2":    
                npsbMfsModelList = npsbMfsRepository.findNpsbMfsModelByBeneficiaryAccount(searchValue);
                break;
        }
        return npsbMfsModelList;
    }

    public List<NpsbMfsModel> getNpsbMfsModelByTransactionNoAndIsDownloaded(String transactionNo, int isDownloaded){
        return npsbMfsRepository.findNpsbMfsModelByTransactionNoAndIsDownloaded(transactionNo, isDownloaded);
    }
    public List<NpsbMfsModel> getProcessedDataByFileId(int fileInfoModelId,int isProcessed, int isVoucherGenerated, LocalDateTime starDateTime, LocalDateTime enDateTime){
        return npsbMfsRepository.getProcessedDataByUploadDateAndFileId(fileInfoModelId, isProcessed, isVoucherGenerated, starDateTime, enDateTime);
    }
    @Transactional
    public void updateIsVoucherGeneratedBulk(List<Integer> ids, int isVoucherGenerated, LocalDateTime reportDate){
        npsbMfsRepository.updateIsVoucherGeneratedBulk(ids, isVoucherGenerated, reportDate);
    }
}
