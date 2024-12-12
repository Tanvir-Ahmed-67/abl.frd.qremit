package abl.frd.qremit.converter.repository;

import abl.frd.qremit.converter.model.ExchangeHouseModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Set;

@Repository
public interface ExchangeHouseModelRepository extends JpaRepository<ExchangeHouseModel, Integer> {

    @Query("SELECT n FROM ExchangeHouseModel n WHERE n.activeStatus = 0")
    List<ExchangeHouseModel> findAllInactiveExchangeHouse();
    @Query("SELECT n FROM ExchangeHouseModel n WHERE n.activeStatus = 1")
    List<ExchangeHouseModel> findAllActiveExchangeHouseList();
    @Transactional
    @Modifying
    @Query("UPDATE ExchangeHouseModel n SET n.activeStatus = :activeStatus WHERE n.Id = :id")
    void setExchangeHouseActiveStatusById(int id, int activeStatus);
    @Transactional
    @Modifying
    @Query("UPDATE ExchangeHouseModel n SET n.activeStatus = 0 WHERE n.Id = :id")
    void setExchangeHouseActiveStatusFalseById(int id);
    @Query("SELECT n FROM ExchangeHouseModel n WHERE n.Id = :id")
    ExchangeHouseModel findByExchangeId(int id);
    @Transactional
    @Modifying
    @Query("UPDATE ExchangeHouseModel n SET n.activeStatus = 0, n.exchangeName=:exchangeName, n.exchangeShortName=:exchangeShortName, n.nrtaCode=:nrtaCode WHERE n.Id = :id")
    void editExchangeHouse(String exchangeName, String exchangeShortName, String nrtaCode, int id);
    @Query("SELECT n FROM ExchangeHouseModel n WHERE n.exchangeCode = :exchangeCode")
    ExchangeHouseModel findByExchangeCode(String exchangeCode);
    ExchangeHouseModel findExchangeCodeByBaseTableName(String baseTableName);
    List<ExchangeHouseModel> findAllByExchangeCodeIn(Set<String> exchangeCodes);
    List<ExchangeHouseModel> findAllExchangeHouseByIsSettlement(int isSettlement);
    ExchangeHouseModel findExchangeHouseByIsSettlement(int isSettlement);
    ExchangeHouseModel findExchangeHouseModelByNrtaCode(String nrtaCode);
}
