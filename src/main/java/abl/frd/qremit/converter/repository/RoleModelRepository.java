package abl.frd.qremit.converter.repository;
import abl.frd.qremit.converter.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleModelRepository extends JpaRepository<Role, Integer> {
    @Query("SELECT u FROM Role u WHERE u.roleName = :roleName")
    public Role findByRoleName(@Param("roleName") String roleName);
}
