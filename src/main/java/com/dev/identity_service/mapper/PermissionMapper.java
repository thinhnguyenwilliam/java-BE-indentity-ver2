package com.dev.identity_service.mapper;

import org.mapstruct.Mapper;

import com.dev.identity_service.dto.request.PermissionRequest;
import com.dev.identity_service.dto.response.PermissionResponse;
import com.dev.identity_service.entity.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);
}
