package com.boosting.code.auth_gateway_resolver.utilities;

import java.util.UUID;

public class Utils {
    public static String generateTrackingUUID(){
        return UUID.randomUUID().toString();
    }
}
