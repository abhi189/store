package com.budderfly.sites.security;

import com.budderfly.sites.client.AuthenticateClient;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@Component
public class PortalPermissionEvaluator implements PermissionEvaluator {

    public PortalPermissionEvaluator() {}

    private AuthenticateClient authenticateClient;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if ((authentication == null) || (targetDomainObject == null) || !(permission instanceof String)){
            return false;
        }

        return hasAuthority(authentication, targetDomainObject, permission);
    }

    private boolean hasAuthority(Authentication authentication, Object targetDomainObject, Object permission) {
        for (GrantedAuthority grantedAuth : authentication.getAuthorities()) {
            if (grantedAuth.getAuthority().contains(permission.toString().toUpperCase())) {
                String user = authentication.getPrincipal().toString();
                try {
                    List<String> shopsOwned = authenticateClient.getShopsOwnedByUser(user);
                    if (shopsOwned.contains(targetDomainObject)) {
                        return true;
                    }
                } catch (HystrixRuntimeException e) {
                    System.out.println(e.getCause().getMessage());
                    return false;
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    return false;
                }
            }
        }

        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if ((authentication == null) || (targetType == null) || !(permission instanceof String)) {
            return true;
        }

        return hasAuthority(authentication, targetType, permission);
    }

    @Autowired
    public void setAuthenticateClient(AuthenticateClient authenticateClient) {
        this.authenticateClient = authenticateClient;
    }
}
