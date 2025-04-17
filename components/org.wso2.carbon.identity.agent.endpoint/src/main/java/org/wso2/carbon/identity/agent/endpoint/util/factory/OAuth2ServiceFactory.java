package org.wso2.carbon.identity.agent.endpoint.util.factory;

import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.oauth2.OAuth2Service;

/**
 * Factory class for OAuth2Service.
 */
public class OAuth2ServiceFactory {

    private static final OAuth2Service SERVICE;

    static {
        OAuth2Service oAuth2Service = (OAuth2Service) PrivilegedCarbonContext.getThreadLocalCarbonContext()
                .getOSGiService(OAuth2Service.class, null);

        if (oAuth2Service == null) {
            throw new IllegalStateException("OAuth2Service is not available from OSGI context.");
        }
        SERVICE = oAuth2Service;
    }

    public static OAuth2Service getOAuth2Service() {

        return SERVICE;
    }
}
