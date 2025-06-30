package abl.frd.qremit.converter.service;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import abl.frd.qremit.converter.repository.CustomQueryRepository;

@Service
@SuppressWarnings("unchecked")
public class BBReportService {
    protected String tbl3 = "analytics_all_bank_remittance_current_year";
    @Autowired
    CustomQueryRepository customQueryRepository;
    public Object getBankWiseRemittanceCurrentYear(String field, Map<String, Object> where, String extra, boolean singleResult){
        Map<String, Object> whereData = customQueryRepository.generateSqlWhere(where, "AND", true);
        String whereClause = whereData.get("whereClause").toString();
        Map<String, Object> params = (Map<String, Object>) whereData.get("params");
        String sql = "SELECT " + field + " FROM " + tbl3 + " " + whereClause + " " + extra ;
        Object object = customQueryRepository.runNativeQuery(sql, params , singleResult, false);
        return object;
    }

    @Transactional
    public Map<String, Object> addBankWiseRemittanceCurrentYear(Map<String, Object> data, Map<String, Object> where){
        Map<String, Object> resp = new HashMap<>();
        List<Object[]> resultList = (List<Object[]>) getBankWiseRemittanceCurrentYear("*", where, "", false);
        int isUpdate = (!resultList.isEmpty())  ?   1:0;
        int affectedRows = customQueryRepository.dynamicInsertOrUpdate(tbl3, data, where, isUpdate);
        if(affectedRows > 0)    resp = CommonService.getResp(0, "Data Updated", null);
        else resp = CommonService.getResp(0, "No Data Updated", null);
        return resp;
    }
}
