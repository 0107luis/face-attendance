package com.facial.recognition.service.impl;

import com.facial.recognition.pojo.Role;
import com.facial.recognition.repository.RoleRepository;
import com.facial.recognition.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Role createRole(Role role) {
        role.setCreatedTime(LocalDateTime.now());
        return roleRepository.save(role);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> findById(Integer roleId) {
        return roleRepository.findById(roleId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> findByRoleName(String roleName) {
        return roleRepository.findByRoleName(roleName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> findByStatus(String status) {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> findActiveRoles() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> findByRoleNameContaining(String roleName) {
        return roleRepository.findByRoleNameContaining(roleName);
    }

    @Override
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<Role> findAllPaged(org.springframework.data.domain.Pageable pageable) {
        return roleRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> findByDescriptionContaining(String description) {
        return roleRepository.findByDescriptionContaining(description);
    }

    @Override
    public Role updateRole(Integer roleId, Role role) {
        Optional<Role> existingRole = roleRepository.findById(roleId);
        if (existingRole.isPresent()) {
            Role updatedRole = existingRole.get();
            updatedRole.setRoleName(role.getRoleName());
            updatedRole.setDescription(role.getDescription());
            return roleRepository.save(updatedRole);
        }
        throw new RuntimeException("Role not found with id: " + roleId);
    }

    @Override
    public Role updateStatus(Integer roleId, String status) {
        throw new RuntimeException("Update status not supported as column does not exist.");
    }

    @Override
    public void deleteRole(Integer roleId) {
        roleRepository.deleteById(roleId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> findRoleByUserId(Integer userId) {
        return roleRepository.findRoleByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countActiveRoles() {
        return roleRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByRoleName(String roleName) {
        return roleRepository.findByRoleName(roleName).isPresent();
    }
}
