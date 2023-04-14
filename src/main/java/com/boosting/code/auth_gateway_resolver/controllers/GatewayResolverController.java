package com.boosting.code.auth_gateway_resolver.controllers;

import com.boosting.code.Dto.ProxyResponseDto;
import com.boosting.code.Services.IProxyService;
import com.boosting.code.auth_gateway_resolver.services.HostResolverService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.boosting.code.auth_gateway_resolver.utilities.Utils.generateTrackingUUID;

@RestController
@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RequestMapping("/api/v1/data")
@RequiredArgsConstructor
public class GatewayResolverController {

    private final IProxyService proxyService;
    private final HostResolverService hostResolverService;
    @GetMapping("/**")
    public ResponseEntity<Object> getResource(HttpServletRequest request, @RequestHeader(defaultValue = "false") String needsBinary){

        boolean isBinary = needsBinary.equals("true");
        String baseUrl= hostResolverService.getHost4Resource(request.getRequestURI());
        ProxyResponseDto responseDto= proxyService.processGetRequest(request,generateTrackingUUID(),baseUrl, isBinary);
        if(responseDto.isBinary()){
            return new ResponseEntity<>(responseDto.getFileInfo().getData(),HttpStatus.OK);
        }
        return new ResponseEntity<>(responseDto.getJsonData(),HttpStatus.OK);
    }
}
