package com.boosting.code.auth_gateway_resolver.services;

import com.boosting.code.auth_gateway_resolver.entities.GatewayContent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HostResolverService {

    private final GatewayContent gatewayContent;

    public String getHost4Resource(String resourceName){
        return gatewayContent.getAppInfo().get(resourceName);
    }
}
