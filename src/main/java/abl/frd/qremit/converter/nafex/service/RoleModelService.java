package abl.frd.qremit.converter.nafex.service;

import abl.frd.qremit.converter.nafex.model.Role;
import abl.frd.qremit.converter.nafex.repository.RoleModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleModelService {
    @Autowired
    RoleModelRepository roleRepository;
    public Role findRoleByRoleName(String roleName){
        Role role = roleRepository.findByRoleName(roleName);
        return role;
    }
}
