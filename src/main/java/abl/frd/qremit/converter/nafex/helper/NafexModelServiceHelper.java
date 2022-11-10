package abl.frd.qremit.converter.nafex.helper;

import abl.frd.qremit.converter.nafex.model.NafexEhMstModel;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withIgnoreHeaderCase().withTrim())) {
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
                        putOnlineFlag(csvRecord.get(7).trim()), // checkT24
                        putCocFlag(csvRecord.get(7).trim()), //checkCoc
                        "0", //checkAccPayee
                        putBeftnFlag(csvRecord.get(8).trim())); //checkBeftn

                nafexDataModelList.add(nafexDataModel);
            }
            return nafexDataModelList;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }

    public static Map<String, List<NafexEhMstModel>> segregateDifferentTypesOfModel(List<NafexEhMstModel> nafexEhMstModel){
        HashMap<String, List<NafexEhMstModel>> differentTypesOfModel = new HashMap<String, List<NafexEhMstModel>>();
        List<NafexEhMstModel> onlineList = new ArrayList<>();
        List<NafexEhMstModel> cocList = new ArrayList<>();
        List<NafexEhMstModel> beftnList = new ArrayList<>();
        List<NafexEhMstModel> accountPayeeList = new ArrayList<>();
        List<NafexEhMstModel> nonProcessed = new ArrayList<>();
        for (NafexEhMstModel singleModel : nafexEhMstModel){
            if(singleModel.getCheckT24().equals("1")){
                onlineList.add(singleModel);
            } else if (singleModel.getCheckCoc().equals("1")) {
                cocList.add(singleModel);
            } else if (singleModel.getCheckBeftn().equals("1")) {
                beftnList.add(singleModel);
            }
            else if (singleModel.getCheckAccPayee().equals("1")){
                accountPayeeList.add(singleModel);
            }
            else{
                nonProcessed.add(singleModel);
            }
        }
        differentTypesOfModel.put("online", onlineList);
        differentTypesOfModel.put("coc", cocList);
        differentTypesOfModel.put("beftn", beftnList);
        differentTypesOfModel.put("accountPayee", accountPayeeList);
        differentTypesOfModel.put("nonProcessed", nonProcessed);
        return  differentTypesOfModel;
    }



    public static String putCocFlag(String accountNumber){
        if(isOnlineAccoutNumberFound(accountNumber)){
            return "0";
        }
        else{
            if(accountNumber.contains("coc") || accountNumber.contains("COC") ){
                return "1";
            }
            else {
                return "0";
            }
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
            System.out.println("T24 Account No Found -"+accountNumber);
            return true;
        }
        else{
            return false;
        }
    }
    public static boolean isCocFound(String accountNumber){
        if(accountNumber.contains("coc") || accountNumber.contains("COC") ){
            return true;
        }
        else {
            return false;
        }
    }
    public static boolean isBeftnFound(String bankName){
        if(bankName.contains("AGRANI") || bankName.contains("agrani")|| bankName.contains("Agrani") || bankName.contains("abl") || bankName.contains("Abl") || bankName.contains("ABL")){
            return false;
        }
        else{
            return true;
        }
    }


    public static String putBeftnFlag(String bankName){
        if(bankName.contains("AGRANI") || bankName.contains("agrani")|| bankName.contains("Agrani") || bankName.contains("abl") || bankName.contains("Abl") || bankName.contains("ABL")){
            return "0";
        }
        else{
            return "1";
        }
    }
}
