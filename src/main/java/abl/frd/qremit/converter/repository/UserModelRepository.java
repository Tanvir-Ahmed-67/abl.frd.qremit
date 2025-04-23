package abl.frd.qremit.converter.repository;
import abl.frd.qremit.converter.model.ExchangeHouseModel;
import abl.frd.qremit.converter.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Repository
public interface UserModelRepository extends JpaRepository<User, Integer> {
    @Query("SELECT u FROM User u WHERE u.userName = :username")
    public User findByUserName(@Param("username") String username);
    //@Query(value = "SELECT GROUP_CONCAT(c.exchange_code) AS exchange_code, GROUP_CONCAT(c.exchange_short_name) AS exchange_short_name FROM user u JOIN ex_house_list c ON FIND_IN_SET(c.exchange_code, u.exchange_code) WHERE u.user_id = :userId GROUP BY u.user_id", nativeQuery = true)
    @Query(value = "SELECT GROUP_CONCAT(c.exchange_code) AS exchange_code, GROUP_CONCAT(c.exchange_short_name) AS exchange_short_name FROM user u JOIN ex_house_list c ON FIND_IN_SET(c.exchange_code, u.exchange_code) WHERE u.user_id = :userId and c.exchange_short_name != '' GROUP BY u.user_id", nativeQuery = true)
    Map<String, String> findExchangeNamesByUserId(@Param("userId") int userId);
    @Query("SELECT u FROM User u WHERE u.userEmail = :useremail")
    public User findByUserEmail(@Param("useremail") String useremail);
    @Query("SELECT u FROM User u WHERE u.id = :userid")
    public User findByUserId(@Param("userid") int id);
    public User findByLoginId(String loginId);

    @Query("SELECT u FROM User u")
    public List<User> loadAllUsers();
    @Query("SELECT user FROM User user LEFT JOIN user.roles role WHERE role.id = 1")
    public List<User> loadUsersOnly();
    @Query("SELECT user FROM User user LEFT JOIN user.roles role WHERE role.id = 2")
    public List<User> loadAdminsOnly();
    @Query("SELECT u, r FROM User u INNER JOIN u.roles r ORDER BY u.id")
    public List<Object[]> loadAllUsersAndRoles(); 
    @Transactional
    @Modifying
    @Query("UPDATE User n SET n.userName = :userName, n.userEmail = :userEmail, n.exchangeCode = :exchangeCode, n.allowedIps = :allowedIps, n.startTime = :startTime, n.endTime = :endTime, n.activeStatus = false where n.id = :userId")
    void updateUser(int userId, String userName, String userEmail, String exchangeCode, String allowedIps, String startTime, String endTime);
    @Query("SELECT u FROM User u WHERE u.activeStatus = false")
    public List<User> loadAllInactiveUsers();
    @Transactional
    @Modifying
    @Query("UPDATE User n SET n.activeStatus = true, n.failedAttempt = 0 where n.id = :userId")
    void updateInactiveUser(int userId);
    @Transactional
    @Modifying
    @Query("UPDATE User n SET n.password = :password, n.passwordChangeRequired = :passwordChangeRequired where n.id = :userId")
    int updatePasswordForFirstTimeUserLogging(int userId, String password, boolean passwordChangeRequired);
    User getUserById(int id);
    @Query("SELECT e from ExchangeHouseModel e INNER JOIN UserExchangeMap u ON e.exchangeCode=u.exchangeCode where u.userId= :userId")
    List<ExchangeHouseModel> findExchangeHouseByUserId(@Param("userId") int userId);
    @Query("SELECT u.allowedIps FROM User u WHERE u.id = :userId")
    String getAllowedIpsForUser(@Param("userId") int userId);
    @Query("SELECT u.startTime FROM User u WHERE u.id = :userId")
    String getAllowedStartTime(@Param("userId") int userId);
    @Query("SELECT u.endTime FROM User u WHERE u.id = :userId")
    String getAllowedEndTime(@Param("userId") int userId);
    @Modifying
    @Transactional
    /*
    @Query("UPDATE User u " +
            "SET u.startTime = :startTime, u.endTime = :endTime " +
            "WHERE u.id NOT IN (" +
            "  SELECT u2.id FROM User u2 " +
            "  JOIN u2.roles r " +
            "  WHERE r.roleName IN ('ROLE_ADMIN', 'ROLE_SUPERADMIN')" +
            ")")
    */
    @Query(value = "UPDATE User u SET u.start_time = :startTime, u.end_time = :endTime WHERE u.user_id IN (SELECT r.user_id FROM user_role r WHERE r.role_id = 1)", nativeQuery = true)
    int setLoginTimeRestrictionsForAllUsers(@Param("startTime") String startTime, @Param("endTime") String endTime);
}
