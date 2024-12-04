package com.dev.identity_service.service;


import com.dev.identity_service.dto.request.RoleRequest;
import com.dev.identity_service.dto.response.RoleResponse;
import com.dev.identity_service.entity.Permission;
import com.dev.identity_service.entity.Role;
import com.dev.identity_service.mapper.RoleMapper;
import com.dev.identity_service.repository.PermissionRepository;
import com.dev.identity_service.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Transactional
@Slf4j
public class RoleService
{
   RoleRepository roleRepository;
   RoleMapper roleMapper;
   PermissionRepository permissionRepository;



   public RoleResponse createRole(RoleRequest roleRequest)
   {
      Role role = roleMapper.toRole(roleRequest);

      // Fetch permissions by IDs
      Iterable<Permission> permissions = permissionRepository.findAllById(roleRequest.getPermissions());

      // Convert Iterable<Permission> to Set<Permission>
      Set<Permission> permissionSet = new HashSet<>();
      permissions.forEach(permissionSet::add);


      // Set permissions to role
      role.setPermissions(permissionSet);

      // Save role to the repository
      role = roleRepository.save(role);

      // Convert and return the saved role as a response
      return roleMapper.toRoleResponse(role);
   }

   public List<RoleResponse> getAll()
   {
      // Fetch all roles from the repository
      List<Role> roles = roleRepository.findAll();

      // Map each Role to RoleResponse using the mapper
      return roles.stream()
              .map(roleMapper::toRoleResponse)
              .toList(); // Collect the mapped responses as a List
   }

   public void delete(String roleName)
   {
      // Check if the role exists
      Role role = roleRepository.findById(roleName)
              .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName));

      // Delete the role
      roleRepository.delete(role);

      // Log the deletion
      log.info("Role deleted: {}", roleName);
   }


}
