package abl.frd.qremit.converter.nafex.repository;

import abl.frd.qremit.converter.nafex.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserModelRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUserName(String userName);
}
