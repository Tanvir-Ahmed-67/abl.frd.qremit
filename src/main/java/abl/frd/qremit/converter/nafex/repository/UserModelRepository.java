package abl.frd.qremit.converter.nafex.repository;

import abl.frd.qremit.converter.nafex.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface UserModelRepository extends JpaRepository<User, Integer> {
    @Query("SELECT u FROM User u WHERE u.userName = :username")
    public User findByUserName(@Param("username") String username);
    @Query("SELECT u FROM User u WHERE u.userEmail = :useremail")
    public User findByUserEmail(@Param("useremail") String useremail);
    @Query("SELECT u FROM User u WHERE u.id = :userid")
    public User findByUserId(@Param("userid") int userId);

    @Query("SELECT u FROM User u")
    public List<User> loadAllUsers();

}
