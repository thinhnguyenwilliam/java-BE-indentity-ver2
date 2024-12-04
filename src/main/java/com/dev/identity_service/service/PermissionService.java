package com.dev.identity_service.service;


import com.dev.identity_service.dto.request.PermissionRequest;
import com.dev.identity_service.dto.response.PermissionResponse;
import com.dev.identity_service.entity.Permission;
import com.dev.identity_service.mapper.PermissionMapper;
import com.dev.identity_service.repository.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Transactional
@Slf4j
public class PermissionService
{
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;


    public PermissionResponse createPermission(PermissionRequest request)
    {
        Permission permission = permissionMapper.toPermission(request);
        permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permission);
    }


    public List<PermissionResponse> getAllPermissions()
    {
        return permissionRepository.findAll().stream()
                .map(permissionMapper::toPermissionResponse)
                .toList();
    }


    public void deletePermission(String permissionName)
    {
        permissionRepository.deleteById(permissionName);
    }
}
