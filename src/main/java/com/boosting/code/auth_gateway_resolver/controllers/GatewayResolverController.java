package com.boosting.code.auth_gateway_resolver.controllers;

import com.boosting.code.Dto.ProxyResponseDto;
import com.boosting.code.Services.IProxyService;
import com.boosting.code.auth_gateway_resolver.services.HostResolverService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.boosting.code.auth_gateway_resolver.utilities.Utils.generateTrackingUUID;

@RestController
@RequestMapping("/api/v1/data")
@RequiredArgsConstructor
public class GatewayResolverController {

    private final IProxyService proxyService;
    private final HostResolverService hostResolverService;
    @GetMapping("/**")
    public ResponseEntity<Object> getResource(HttpServletRequest request){

        String baseUrl= hostResolverService.getHost4Resource(request.getRequestURI());
        ProxyResponseDto responseDto= proxyService.processGetRequest(request,generateTrackingUUID(),baseUrl);
        if(responseDto.isBinary()){
            return new ResponseEntity<>(responseDto.getFileInfo().getData(),HttpStatus.OK);
        }
        return new ResponseEntity<>(responseDto.getJsonData(),HttpStatus.OK);
    }
}
