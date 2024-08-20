package abl.frd.qremit.converter.nafex.repository;

import abl.frd.qremit.converter.nafex.model.Role;
import abl.frd.qremit.converter.nafex.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleModelRepository extends JpaRepository<Role, Integer> {
    @Query("SELECT u FROM Role u WHERE u.roleName = :roleName")
    public Role findByRoleName(@Param("roleName") String roleName);
}
