package com.boosting.code.auth_gateway_resolver.controllers;

import com.boosting.code.Dto.ProtoRequest;
import com.boosting.code.Dto.ProxyResponseDto;
import com.boosting.code.Services.IProxyService;
import com.boosting.code.auth_gateway_resolver.entities.User;
import com.boosting.code.auth_gateway_resolver.repositories.IUserRepository;
import com.boosting.code.auth_gateway_resolver.services.HostResolverService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.boosting.code.auth_gateway_resolver.utilities.Utils.generateTrackingUUID;

@RestController
@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RequestMapping("/api/v1/data")
@RequiredArgsConstructor
public class GatewayResolverController {

    private final IProxyService proxyService;
    private final HostResolverService hostResolverService;
    private final IUserRepository userService;

    @GetMapping("/**")
    public ResponseEntity<Object> getResource(HttpServletRequest request, @RequestHeader(defaultValue = "false",name = "Needs-Binary") String needsBinary){

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        String username = userDetails.getUsername();
        Optional<User> user = userService.findByEmail(username);
        if(user.isEmpty())
            return ResponseEntity.badRequest().build();

        ProtoRequest protoRequest = configureProtoRequest(needsBinary,user,request);

        ProxyResponseDto responseDto= proxyService.processRequestGivenResources(protoRequest,"GET");
        return  adaptResponse(responseDto);
    }
    private ProtoRequest configureProtoRequest(String needsBinary,Optional<User> user,HttpServletRequest request){


        boolean isBinary = needsBinary.equals("true");
        String baseUrl= hostResolverService.getHost4Resource(request.getRequestURI());

        HttpHeaders customHeaders = new HttpHeaders();
        customHeaders.add("USER_UUID",user.get().getUuid());
        ProtoRequest protoRequest = ProtoRequest
                .builder()
                .headers(customHeaders)
                .baseURL(baseUrl)
                .queryString(request.getQueryString())
                .body(null)
                .uri(request.getRequestURI())
                .isBinaryClient(isBinary)
                .build();

        return protoRequest;
    }

    private ResponseEntity<Object> adaptResponse(ProxyResponseDto responseDto) {
        if(responseDto.isBinary()){
            HttpHeaders headers = responseDto.getHeaders();
            return new ResponseEntity<>(responseDto.getFileInfo().getData(),headers,HttpStatus.OK);
        }
        return new ResponseEntity<>(responseDto.getJsonData(),HttpStatus.OK);
    }
}
