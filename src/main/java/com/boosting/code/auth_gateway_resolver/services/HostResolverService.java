package com.boosting.code.auth_gateway_resolver.services;

import com.boosting.code.auth_gateway_resolver.entities.GatewayContent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HostResolverService {

    private final GatewayContent gatewayContent;
private final String GATEWAY_PREFIX="/api/v1/data";
    public String getHost4Resource(String resourcePath){
    if(resourcePath.startsWith(GATEWAY_PREFIX)){
        resourcePath = resourcePath.replace(GATEWAY_PREFIX,"");
        String resource = resourcePath.split("/")[1];
        return gatewayContent.getAppInfo().get(resource);
    }
    return null;
    }
}
