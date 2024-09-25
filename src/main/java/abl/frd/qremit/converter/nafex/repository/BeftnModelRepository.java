package abl.frd.qremit.converter.nafex.repository;

import abl.frd.qremit.converter.nafex.model.BeftnModel;
import abl.frd.qremit.converter.nafex.model.CocModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BeftnModelRepository extends JpaRepository<BeftnModel, Integer> {
    @Query("SELECT n FROM BeftnModel n WHERE n.fileInfoModel.id =?1")
    List<BeftnModel> findAllBeftnModelHavingFileInfoId(int id);

    @Query("SELECT n FROM BeftnModel n WHERE n.fileInfoModel.id =?1")
    List<BeftnModel> findAllBeftnModelHavingFileInfoIdForIncentive(int id);
    @Query("SELECT n FROM BeftnModel n")
    List<BeftnModel> findAllBeftnModel();

    @Query("SELECT n FROM BeftnModel n")
    List<BeftnModel> findAllBeftnModelForIncentive();
    Integer countByIsProcessedMain(int isProcessed);
    Integer countByIsProcessedIncentive(int isProcessed);
    @Query("SELECT n FROM BeftnModel n WHERE n.isProcessedMain= :isProcessed")
    List<BeftnModel> loadUnprocessedBeftnMainData(@Param("isProcessed") int isProcessed);
    @Query("SELECT n FROM BeftnModel n WHERE n.isProcessedIncentive= :isProcessed")
    List<BeftnModel> loadUnprocessedBeftnIncentiveData(@Param("isProcessed") int isProcessed);

}
/*
n.beneficiaryName not like '%PHONE%' or n.beneficiaryName not like '%CENTER%' or n.beneficiaryName not like '%LTD%' or n.beneficiaryName not like '%BANK%' or n.beneficiaryName not like '%TELECOM%'\n" +
            "        or n.beneficiaryName not like '%TRADERS%' or n.beneficiaryName not like '%STORE%' or n.beneficiaryName not like '%CLOTH%' or n.beneficiaryName not like '%BROTHERS%' or n.beneficiaryName not like '%ENTERPRIZE%'\n" +
            "        or n.beneficiaryName not like '%ENTERPRI%' or n.beneficiaryName not like '%COSMETICS%' or n.beneficiaryName not like '%MOBILE%' or n.beneficiaryName not like '%TRAVELS%' or n.beneficiaryName not like '%TOURS%'\n" +
            "        or n.beneficiaryName not like '%NETWORK%' or n.beneficiaryName not like '%ASSETS%' or n.beneficiaryName not like '%SOLUTIONS%' or n.beneficiaryName not like '%FUND%' or n.beneficiaryName not like '%ELECTRON%'\n" +
            "        or n.beneficiaryName not like '%SECURITIES%' or n.beneficiaryName not like '%EQUIPMENT%' or n.beneficiaryName not like '%COMPENSATION%' or n.beneficiaryName not like '%DEATH%'\n" +
            "        or n.beneficiaryName not like '%GALLERY%' or n.beneficiaryName not like '%HOUSE%' or n.beneficiaryName not like '%M/S%' or n.beneficiaryName not like '%BANGLADESH%' or n.beneficiaryName not like '%BD%'\n" +
            "        or n.beneficiaryName not like '%LIMITED%' or n.beneficiaryName not like '%OVERSEAS%' or n.beneficiaryName not like '%DAIRY%' or n.beneficiaryName not like '%FARM%' or n.beneficiaryName not like '%COLLECTION%'\n" +
            "        or n.beneficiaryName not like '%BANGLADESH%' or n.beneficiaryName not like '%RICE%' or n.beneficiaryName not like '%AGENCY%' or n.beneficiaryName not like '%TEXTILE%'\n" +
            "        or n.beneficiaryName not like '%VARAITY%' or n.beneficiaryName not like '%MEDICAL%' or n.beneficiaryName not like '%HALL%' or n.beneficiaryName not like '%PHARMA%'\n" +
            "        or n.beneficiaryName not like '%OPTICAL%' or n.beneficiaryName not like '%FAIR%' or n.beneficiaryName not like '%PRIZE%' or n.beneficiaryName not like '%GENERAL%'\n" +
            "        or n.beneficiaryName not like '%HOSPITAL%' or n.beneficiaryName not like '%BITAN%' or n.beneficiaryName not like '%TRADING%'

 */

