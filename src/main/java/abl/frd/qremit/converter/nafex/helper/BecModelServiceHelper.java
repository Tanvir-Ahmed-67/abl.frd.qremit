package abl.frd.qremit.converter.nafex.helper;

import abl.frd.qremit.converter.nafex.model.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static abl.frd.qremit.converter.nafex.helper.BeftnModelServiceHelper.calculatePercentage;

public class BecModelServiceHelper {
    static String[] HEADERs = {"Excode","Tranno","Currency","Amount","Entered Date","Remitter","Beneficiary","Bene A/C","Bank Name","Bank Code","Branch Name","Branch Code"};

    public static List<BecModel> csvToBecModels(InputStream is) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.newFormat('|').withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            List<BecModel> becDataModelList = new ArrayList<>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            for (CSVRecord csvRecord : csvRecords) {
                BecModel becDataModel = new BecModel(
                        csvRecord.get(0), //exCode
                        csvRecord.get(1), //Tranno
                        csvRecord.get(2), //Currency
                        Double.parseDouble(csvRecord.get(3)), //Amount
                        csvRecord.get(4), //enteredDate
                        csvRecord.get(5), //remitter

                        csvRecord.get(6), // beneficiary
                        csvRecord.get(7), //beneficiaryAccount
                       
                        csvRecord.get(8), //bankName
                        csvRecord.get(9), //bankCode
                        csvRecord.get(10), //branchName
                        csvRecord.get(11), // branchCode
                        csvRecord.get(12), //beneficiaryMobile
                        csvRecord.get(13), //draweeBranchName
                        csvRecord.get(14), //draweeBranchCode
                        csvRecord.get(15), //purposeOfRemittance
                        csvRecord.get(16), //sourceOfIncome
                        csvRecord.get(17), //remitterMobile
                        "Not Processed",    // processed_flag
                        "type",             // type_flag
                        "processedBy",      // Processed_by
                        "dummy",            // processed_date
                        "extraC",
                        putOnlineFlag(csvRecord.get(7).trim()),                                 // checkT24
                        putCocFlag(csvRecord.get(7).trim()),                                    //checkCoc
                        putAccountPayeeFlag(csvRecord.get(8).trim(),csvRecord.get(7).trim()),   //checkAccPayee
                        putBeftnFlag(csvRecord.get(8).trim(), csvRecord.get(7).trim()));        //checkBeftn

                becDataModelList.add(becDataModel);
            }
            return becDataModelList;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }


    public static List<OnlineModel> generateOnlineModelList(List<BecModel> becModel){
        List<OnlineModel> onlineList = new ArrayList<>();
        for (BecModel singleModel : becModel){
            if(singleModel.getCheckT24().equals("1")){
                onlineList.add(generateOnlineModel(singleModel));
            }
        }
        return onlineList;
    }

    public static OnlineModel generateOnlineModel(BecModel becModel){
        OnlineModel onlineModel = new OnlineModel();
                    onlineModel.setAmount(becModel.getAmount());
                    onlineModel.setBeneficiaryAccount(becModel.getBeneficiaryAccount());
                    onlineModel.setBeneficiaryName(becModel.getBeneficiaryName());
                    onlineModel.setExchangeCode(becModel.getExchangeCode());
                    onlineModel.setRemitterName(becModel.getRemitterName());
                    onlineModel.setTransactionNo(becModel.getTransactionNo());
                    onlineModel.setIsProcessed("0");
                    onlineModel.setIsDownloaded("0");
                    onlineModel.setDownloadDateTime(LocalDateTime.now());
                    onlineModel.setDownloadUserId(9999);
                    onlineModel.setExtraE("dump");
        return onlineModel;
    }

    public static List<CocModel> generateCocModelList(List<BecModel> becModel){
        List<CocModel> cocList = new ArrayList<>();
        for (BecModel singleModel : becModel){
            if(singleModel.getCheckCoc().equals("1")){
                cocList.add(generateCocModel(singleModel));
            }
        }
        return cocList;
    }

    public static CocModel generateCocModel(BecModel becModel){
        CocModel cocModel = new CocModel();
        cocModel.setAmount(becModel.getAmount());
        cocModel.setBankCode(becModel.getBankCode());
        cocModel.setBankName(becModel.getBankName());
        cocModel.setBeneficiaryAccount(becModel.getBeneficiaryAccount());
        cocModel.setBeneficiaryName(becModel.getBeneficiaryName());
        cocModel.setBranchCode(becModel.getBranchCode());
        cocModel.setBranchName(becModel.getBranchName());
        cocModel.setCocCode("15");
        cocModel.setCreditMark("CRED");
        cocModel.setCurrency(becModel.getCurrency());
        cocModel.setEnteredDate(becModel.getEnteredDate());
        cocModel.setExchangeCode(becModel.getExchangeCode());
        cocModel.setIsProcessed("0");
        cocModel.setIsDownloaded("0");
        cocModel.setDownloadDateTime(LocalDateTime.now());
        cocModel.setDownloadUserId(9999);
        cocModel.setExtraE("dummy");
        cocModel.setIncentive(000.00);
        cocModel.setRemitterName(becModel.getRemitterName());
        cocModel.setTransactionNo(becModel.getTransactionNo());

        return cocModel;
    }

    public static List<AccountPayeeModel> generateAccountPayeeModelList(List<BecModel> becModel){
        List<AccountPayeeModel> accountPayeeModelList = new ArrayList<>();
        for (BecModel singleModel : becModel){
            if(singleModel.getCheckAccPayee().equals("1")){
                accountPayeeModelList.add(generateAccountPayeeModel(singleModel));
            }
        }
        return accountPayeeModelList;
    }
    public static AccountPayeeModel generateAccountPayeeModel(BecModel becModel){
        AccountPayeeModel accountPayeeModel = new AccountPayeeModel();
        accountPayeeModel.setAmount(becModel.getAmount());
        accountPayeeModel.setBankCode(becModel.getBankCode());
        accountPayeeModel.setBankName(becModel.getBankName());
        accountPayeeModel.setBeneficiaryAccount(becModel.getBeneficiaryAccount());
        accountPayeeModel.setBeneficiaryName(becModel.getBeneficiaryName());
        accountPayeeModel.setBranchCode(becModel.getBranchCode());
        accountPayeeModel.setBranchName(becModel.getBranchName());
        accountPayeeModel.setAccountPayeeCode("5");
        accountPayeeModel.setCreditMark("CRED");
        accountPayeeModel.setCurrency(becModel.getCurrency());
        accountPayeeModel.setEnteredDate(becModel.getEnteredDate());
        accountPayeeModel.setExchangeCode(becModel.getExchangeCode());
        accountPayeeModel.setIsProcessed("0");
        accountPayeeModel.setIsDownloaded("0");
        accountPayeeModel.setDownloadDateTime(LocalDateTime.now());
        accountPayeeModel.setDownloadUserId(9999);
        accountPayeeModel.setExtraE("dummy");
        accountPayeeModel.setIncentive(000.00);
        accountPayeeModel.setRemitterName(becModel.getRemitterName());
        accountPayeeModel.setTransactionNo(becModel.getTransactionNo());

        return accountPayeeModel;
    }

    public static List<BeftnModel> generateBeftnModelList(List<BecModel> becModel){
        List<BeftnModel> beftnModelList = new ArrayList<>();
        for (BecModel singleModel : becModel){
            if(singleModel.getCheckBeftn().equals("1")){
                beftnModelList.add(generateBeftnModel(singleModel));
            }
        }
        return beftnModelList;
    }
    public static BeftnModel generateBeftnModel(BecModel becModel){
        BeftnModel beftnModel = new BeftnModel();
        beftnModel.setAmount(becModel.getAmount());
        beftnModel.setBeneficiaryAccount(becModel.getBeneficiaryAccount());
        beftnModel.setBeneficiaryAccountType("SA");
        beftnModel.setBeneficiaryName(becModel.getBeneficiaryName());
        beftnModel.setExchangeCode(becModel.getExchangeCode());
        beftnModel.setIsProcessedMain("0");
        beftnModel.setIsProcessedIncentive("0");
        beftnModel.setIsIncDownloaded("0");
        beftnModel.setDownloadUserId(9999);
        beftnModel.setDownloadDateTime(LocalDateTime.now());
        beftnModel.setIncentive(calculatePercentage(becModel.getAmount()));
        beftnModel.setOrgAccountNo("160954");
        beftnModel.setOrgAccountType("CA");
        beftnModel.setOrgCustomerNo("7892");
        beftnModel.setOrgName("FRD Remittance");
        beftnModel.setRoutingNo(becModel.getBranchCode());
        beftnModel.setTransactionNo(becModel.getTransactionNo());

        return beftnModel;
    }

    public static String putCocFlag(String accountNumber){
        if(isOnlineAccoutNumberFound(accountNumber)){
            return "0";
        }
        else if(accountNumber.contains("coc") || accountNumber.contains("COC")){
            return "1";
        }
        else {
            return "0";
        }
    }
    public static boolean isCocFound(String accountNumber){
        if(accountNumber.toLowerCase().contains("coc")){
            return true;
        }
        else {
            return false;
        }
    }
    public static String getOnlineAccountNumber(String accountNumber){
        //^.*02000(\d{8})$.*
        Pattern p = Pattern.compile("^.*02000(\\d{8})$.*");
        Matcher m = p.matcher(accountNumber);
        String onlineAccountNumber=null;
        if (m.find())
        {
            onlineAccountNumber = m.group(1);
        }
        return onlineAccountNumber;
    }
    public static String putOnlineFlag(String accountNumber){
        if(isOnlineAccoutNumberFound(accountNumber)){
            return "1";
        }
        else{
            return "0";
        }
    }
    public static boolean isOnlineAccoutNumberFound(String accountNumber){
        Pattern p = Pattern.compile("^.*02000(\\d{8})$.*");
        Matcher m = p.matcher(accountNumber);
        if (m.find())
        {
            return true;
        }
        else{
            return false;
        }
    }
    public static boolean isBeftnFound(String bankName, String accountNumber){
        if(isOnlineAccoutNumberFound(accountNumber)){
            return false;
        }
        else if(isCocFound(accountNumber)){
            return false;
        }
        else if(bankName.toLowerCase().contains("agrani") || bankName.toLowerCase().contains("abl")) {
            return false;
        }
        else{
            return true;
        }
    }


    public static String putBeftnFlag(String bankName, String accountNumber){
        if(isBeftnFound(bankName, accountNumber)){
            return "1";
        }
        else{
            return "0";
        }
    }
    public static boolean isAccountPayeeFound(String bankName, String accountNumber){
        if(isOnlineAccoutNumberFound(accountNumber)){
            return false;
        }
        else if(isCocFound(accountNumber)) {
            return false;
        }
        else if(isBeftnFound(bankName,accountNumber)){
            return false;
        }
        else{
            return true;
        }
    }
    public static String putAccountPayeeFlag(String bankName, String accountNumber){
        if(isAccountPayeeFound(bankName, accountNumber)){
            return "1";
        }
        else{
            return "0";
        }
    }
}
