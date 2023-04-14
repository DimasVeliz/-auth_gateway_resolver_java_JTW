package com.boosting.code.auth_gateway_resolver.entities;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class GatewayContent {
    @Value("classpath:gatewayContent.json")
    private Resource resource;

    public Map<String,String> getAppInfo()
    {
        ObjectMapper mapper = new ObjectMapper();
        try {
            var appInfo = mapper.readValue( resource.getFile(), HashMap.class);
            return appInfo;

        } catch (IOException e) {

            e.printStackTrace();
        }
        return null;
    }

}
