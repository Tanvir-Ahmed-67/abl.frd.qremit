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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NafexModelServiceHelper {
    public static String TYPE = "text/csv";
    static String[] HEADERs = {"Excode","Tranno","Currency","Amount","Entered Date","Remitter","Beneficiary","Bene A/C","Bank Name","Bank Code","Branch Name","Branch Code"};

    public static boolean hasCSVFormat(MultipartFile file) {
        if (TYPE.equals(file.getContentType())
                || file.getContentType().equals("text/plain")) {
            return true;
        }
        return false;
    }
    public static List<NafexEhMstModel> csvToNafexModels(InputStream is) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.newFormat('|') .withIgnoreHeaderCase().withTrim())) {
            List<NafexEhMstModel> nafexDataModelList = new ArrayList<>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            for (CSVRecord csvRecord : csvRecords) {
                NafexEhMstModel nafexDataModel = new NafexEhMstModel(
                        csvRecord.get(0), //exCode
                        csvRecord.get(1), //Tranno
                        csvRecord.get(2), //Currency
                        Double.parseDouble(csvRecord.get(3)), //Amount
                        csvRecord.get(4), //enteredDate
                        csvRecord.get(5), //remitter

                        csvRecord.get(6), // beneficiary
                        csvRecord.get(7), //beneficiaryAccount
                        csvRecord.get(12), //beneficiaryMobile
                        csvRecord.get(8), //bankName
                        csvRecord.get(9), //bankCode
                        csvRecord.get(10), //branchName
                        csvRecord.get(11), // branchCode

                        csvRecord.get(13), //draweeBranchName
                        csvRecord.get(14), //draweeBranchCode
                        csvRecord.get(15), //purposeOfRemittance
                        csvRecord.get(16), //sourceOfIncome
                        csvRecord.get(17), //remitterMobile
                        "Not Processed",    // processed_flag
                        "type",             // type_flag
                        "processedBy",      // Processed_by
                        "12-23-23",         // processed_date
                        "extraC",
                        putOnlineFlag(csvRecord.get(7).trim()),                                 // checkT24
                        putCocFlag(csvRecord.get(7).trim()),                                    //checkCoc
                        putAccountPayeeFlag(csvRecord.get(8).trim(),csvRecord.get(7).trim()),   //checkAccPayee
                        putBeftnFlag(csvRecord.get(8).trim(), csvRecord.get(7).trim()));        //checkBeftn

                nafexDataModelList.add(nafexDataModel);
            }
            return nafexDataModelList;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }


    public static List<OnlineModel> generateOnlineModelList(List<NafexEhMstModel> nafexEhMstModel){
        List<OnlineModel> onlineList = new ArrayList<>();
        for (NafexEhMstModel singleModel : nafexEhMstModel){
            if(singleModel.getCheckT24().equals("1")){
                onlineList.add(generateOnlineModel(singleModel));
            }
        }
        return onlineList;
    }

    public static OnlineModel generateOnlineModel(NafexEhMstModel nafexEhMstModel){
        OnlineModel onlineModel = new OnlineModel();
                    onlineModel.setAmount(nafexEhMstModel.getAmount());
                    onlineModel.setBeneficiaryAccount(nafexEhMstModel.getBeneficiaryAccount());
                    onlineModel.setBeneficiaryName(nafexEhMstModel.getBeneficiaryName());
                    onlineModel.setExchangeCode(nafexEhMstModel.getExchangeCode());
                    onlineModel.setRemitterName(nafexEhMstModel.getRemitterName());
                    onlineModel.setTransactionNo(nafexEhMstModel.getTransactionNo());
                    onlineModel.setIsProcessed("0");
                    onlineModel.setIsDownloaded("0");
                    onlineModel.setExtraC("dump");
                    onlineModel.setExtraD("dump");
                    onlineModel.setExtraE("dump");
        return onlineModel;
    }

    public static List<CocModel> generateCocModelList(List<NafexEhMstModel> nafexEhMstModel){
        List<CocModel> cocList = new ArrayList<>();
        for (NafexEhMstModel singleModel : nafexEhMstModel){
            if(singleModel.getCheckCoc().equals("1")){
                cocList.add(generateCocModel(singleModel));
            }
        }
        return cocList;
    }

    public static CocModel generateCocModel(NafexEhMstModel nafexEhMstModel){
        CocModel cocModel = new CocModel();
        cocModel.setAmount(nafexEhMstModel.getAmount());
        cocModel.setBankCode(nafexEhMstModel.getBankCode());
        cocModel.setBankName(nafexEhMstModel.getBankName());
        cocModel.setBeneficiaryAccount(nafexEhMstModel.getBeneficiaryAccount());
        cocModel.setBeneficiaryName(nafexEhMstModel.getBeneficiaryName());
        cocModel.setBranchCode(nafexEhMstModel.getBranchCode());
        cocModel.setBranchName(nafexEhMstModel.getBranchName());
        cocModel.setCocCode("15");
        cocModel.setCreditMark("CRED");
        cocModel.setCurrency(nafexEhMstModel.getCurrency());
        cocModel.setEnteredDate(nafexEhMstModel.getEnteredDate());
        cocModel.setExchangeCode(nafexEhMstModel.getExchangeCode());
        cocModel.setIsProcessed("0");
        cocModel.setIsDownloaded("0");
        cocModel.setExtraC("dummy");
        cocModel.setExtraD("dummy");
        cocModel.setExtraE("dummy");
        cocModel.setIncentive(000.00);
        cocModel.setRemitterName(nafexEhMstModel.getRemitterName());
        cocModel.setTransactionNo(nafexEhMstModel.getTransactionNo());

        return cocModel;
    }

    public static List<AccountPayeeModel> generateAccountPayeeModelList(List<NafexEhMstModel> nafexEhMstModel){
        List<AccountPayeeModel> accountPayeeModelList = new ArrayList<>();
        for (NafexEhMstModel singleModel : nafexEhMstModel){
            if(singleModel.getCheckAccPayee().equals("1")){
                accountPayeeModelList.add(generateAccountPayeeModel(singleModel));
            }
        }
        return accountPayeeModelList;
    }
    public static AccountPayeeModel generateAccountPayeeModel(NafexEhMstModel nafexEhMstModel){
        AccountPayeeModel accountPayeeModel = new AccountPayeeModel();
        accountPayeeModel.setAmount(nafexEhMstModel.getAmount());
        accountPayeeModel.setBankCode(nafexEhMstModel.getBankCode());
        accountPayeeModel.setBankName(nafexEhMstModel.getBankName());
        accountPayeeModel.setBeneficiaryAccount(nafexEhMstModel.getBeneficiaryAccount());
        accountPayeeModel.setBeneficiaryName(nafexEhMstModel.getBeneficiaryName());
        accountPayeeModel.setBranchCode(nafexEhMstModel.getBranchCode());
        accountPayeeModel.setBranchName(nafexEhMstModel.getBranchName());
        accountPayeeModel.setAccountPayeeCode("5");
        accountPayeeModel.setCreditMark("CRED");
        accountPayeeModel.setCurrency(nafexEhMstModel.getCurrency());
        accountPayeeModel.setEnteredDate(nafexEhMstModel.getEnteredDate());
        accountPayeeModel.setExchangeCode(nafexEhMstModel.getExchangeCode());
        accountPayeeModel.setIsProcessed("0");
        accountPayeeModel.setIsDownloaded("0");
        accountPayeeModel.setExtraC("dummy");
        accountPayeeModel.setExtraD("dummy");
        accountPayeeModel.setExtraE("dummy");
        accountPayeeModel.setIncentive(000.00);
        accountPayeeModel.setRemitterName(nafexEhMstModel.getRemitterName());
        accountPayeeModel.setTransactionNo(nafexEhMstModel.getTransactionNo());

        return accountPayeeModel;
    }

    public static List<BeftnModel> generateBeftnModelList(List<NafexEhMstModel> nafexEhMstModel){
        List<BeftnModel> beftnModelList = new ArrayList<>();
        for (NafexEhMstModel singleModel : nafexEhMstModel){
            if(singleModel.getCheckBeftn().equals("1")){
                beftnModelList.add(generateBeftnModel(singleModel));
            }
        }
        return beftnModelList;
    }
    public static BeftnModel generateBeftnModel(NafexEhMstModel nafexEhMstModel){
        BeftnModel beftnModel = new BeftnModel();
        beftnModel.setAmount(nafexEhMstModel.getAmount());
        beftnModel.setBeneficiaryAccount(nafexEhMstModel.getBeneficiaryAccount());
        beftnModel.setBeneficiaryAccountType("SA");
        beftnModel.setBeneficiaryName(nafexEhMstModel.getBeneficiaryName());
        beftnModel.setExchangeCode(nafexEhMstModel.getExchangeCode());
        beftnModel.setIsProcessed("0");
        beftnModel.setIsDownloaded("0");
        beftnModel.setIsIncDownloaded("dummy");
        beftnModel.setExtraD("dummy");
        beftnModel.setExtraE("dummy");
        beftnModel.setIncentive(000.00);
        beftnModel.setOrgAccountNo("160954");
        beftnModel.setOrgAccountType("CA");
        beftnModel.setOrgCustomerNo("7892");
        beftnModel.setOrgName("FRD Remittance");
        beftnModel.setRoutingNo(nafexEhMstModel.getBranchCode());
        beftnModel.setTransactionNo(nafexEhMstModel.getTransactionNo());

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
