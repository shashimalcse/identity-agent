package org.wso2.carbon.identity.agent.endpoint.token;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.wso2.carbon.identity.agent.core.model.AgentTokenRequest;
import org.wso2.carbon.identity.oauth.common.OAuth2ErrorCodes;
import org.wso2.carbon.identity.oauth.common.OAuthConstants;
import org.wso2.carbon.identity.oauth.endpoint.util.EndpointUtil;
import org.wso2.carbon.identity.oauth.endpoint.util.factory.OAuth2ServiceFactory;
import org.wso2.carbon.identity.oauth2.ResponseHeader;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AccessTokenReqDTO;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AccessTokenRespDTO;
import org.wso2.carbon.identity.oauth2.token.handlers.response.OAuth2TokenResponse;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 * REST implementation of the Agent Token Endpoint.
 */
@Path("/auth/token")
public class AgentTokenEndpoint {

    public static final String BEARER = "Bearer";


    @POST
    @Path("/")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    public Response issueAccessToken(@Context HttpServletRequest request, @Context HttpServletResponse response,
                                     MultivaluedMap<String, String> paramMap) throws OAuthSystemException {

        AgentTokenRequest agentTokenRequest = new AgentTokenRequest(request);
        OAuth2AccessTokenReqDTO tokenReqDTO = buildAccessTokenReqDTO(agentTokenRequest,
                (HttpServletRequestWrapper) request, (HttpServletResponseWrapper) response);
        OAuth2AccessTokenRespDTO oauth2AccessTokenResp =
                OAuth2ServiceFactory.getOAuth2Service().issueAccessToken(tokenReqDTO);
        if (oauth2AccessTokenResp.getErrorMsg() != null) {
            return handleErrorResponse(oauth2AccessTokenResp);
        } else {
            return buildTokenResponse(oauth2AccessTokenResp);
        }
    }

    private OAuth2AccessTokenReqDTO buildAccessTokenReqDTO(AgentTokenRequest request, HttpServletRequestWrapper
            httpServletRequestWrapper, HttpServletResponseWrapper httpServletResponseWrapper) {

        OAuth2AccessTokenReqDTO tokenReqDTO = new OAuth2AccessTokenReqDTO();
        String grantType = GrantType.PASSWORD.toString();
        tokenReqDTO.setGrantType(grantType);
        tokenReqDTO.setClientId("wFMQEtCUZSJsDPn3yA3jukW5E8oa");
        tokenReqDTO.setClientSecret("TYB41oByf_PW_fgh5kpVc68AQgvbevDZwKiatM9Q6zoa");
        tokenReqDTO.setScope(request.getScopes().toArray(new String[0]));
        // Set the request wrapper so we can get remote information later.
        tokenReqDTO.setHttpServletRequestWrapper(httpServletRequestWrapper);
        tokenReqDTO.setHttpServletResponseWrapper(httpServletResponseWrapper);

        tokenReqDTO.setResourceOwnerUsername(request.getAgentId());
        tokenReqDTO.setResourceOwnerPassword(request.getAgentSecret());

        tokenReqDTO.addAuthenticationMethodReference(grantType);
        return tokenReqDTO;
    }

    private Response buildTokenResponse(OAuth2AccessTokenRespDTO oauth2AccessTokenResp) throws OAuthSystemException {

        if (StringUtils.isBlank(oauth2AccessTokenResp.getTokenType())) {
            oauth2AccessTokenResp.setTokenType(BEARER);
        }

        OAuth2TokenResponse.OAuthTokenResponseBuilder oAuthRespBuilder = OAuth2TokenResponse
                .tokenResponse(HttpServletResponse.SC_OK)
                .setAccessToken(oauth2AccessTokenResp.getAccessToken())
                .setRefreshToken(oauth2AccessTokenResp.getRefreshToken())
                .setExpiresIn(Long.toString(oauth2AccessTokenResp.getExpiresIn()))
                .setTokenType(oauth2AccessTokenResp.getTokenType());

        oAuthRespBuilder.setScope(oauth2AccessTokenResp.getAuthorizedScopes());

        if (oauth2AccessTokenResp.getIDToken() != null) {
            oAuthRespBuilder.setParam(OAuthConstants.ID_TOKEN, oauth2AccessTokenResp.getIDToken());
        }

        // Set custom parameters in token response if supported
        if (MapUtils.isNotEmpty(oauth2AccessTokenResp.getParameters())) {
            oauth2AccessTokenResp.getParameters().forEach(oAuthRespBuilder::setParam);
        }

        // Set custom parameters in token response if supported.
        if (MapUtils.isNotEmpty(oauth2AccessTokenResp.getParameterObjects())) {
            oauth2AccessTokenResp.getParameterObjects().forEach(oAuthRespBuilder::setParam);
        }

        OAuthResponse response = oAuthRespBuilder.buildJSONMessage();
        ResponseHeader[] headers = oauth2AccessTokenResp.getResponseHeaders();
        Response.ResponseBuilder respBuilder = Response
                .status(response.getResponseStatus())
                .header(OAuthConstants.HTTP_RESP_HEADER_CACHE_CONTROL,
                        OAuthConstants.HTTP_RESP_HEADER_VAL_CACHE_CONTROL_NO_STORE)
                .header(OAuthConstants.HTTP_RESP_HEADER_PRAGMA,
                        OAuthConstants.HTTP_RESP_HEADER_VAL_PRAGMA_NO_CACHE);

        if (headers != null) {
            for (ResponseHeader header : headers) {
                if (header != null) {
                    respBuilder.header(header.getKey(), header.getValue());
                }
            }
        }

        return respBuilder.entity(response.getBody()).build();
    }

    private Response handleErrorResponse(OAuth2AccessTokenRespDTO oauth2AccessTokenResp) throws OAuthSystemException {

        // if there is an auth failure, HTTP 401 Status Code should be sent back to the client.
        if (OAuth2ErrorCodes.INVALID_CLIENT.equals(oauth2AccessTokenResp.getErrorCode())) {
            return handleBasicAuthFailure(oauth2AccessTokenResp.getErrorMsg());
        } else {
            // Otherwise send back HTTP 400 Status Code
            OAuthResponse response = OAuthASResponse
                    .errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                    .setError(oauth2AccessTokenResp.getErrorCode())
                    .setErrorDescription(oauth2AccessTokenResp.getErrorMsg())
                    .buildJSONMessage();

            ResponseHeader[] headers = oauth2AccessTokenResp.getResponseHeaders();
            Response.ResponseBuilder respBuilder = Response.status(response.getResponseStatus());

            if (headers != null) {
                for (ResponseHeader header : headers) {
                    if (header != null) {
                        respBuilder.header(header.getKey(), header.getValue());
                    }
                }
            }
            return respBuilder.entity(response.getBody()).build();
        }
    }

    private Response handleBasicAuthFailure(String errorMessage) throws OAuthSystemException {

        if (StringUtils.isBlank(errorMessage)) {
            errorMessage = "Client Authentication failed.";
        }

        OAuthResponse response = OAuthASResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
                .setError(OAuth2ErrorCodes.INVALID_CLIENT)
                .setErrorDescription(errorMessage).buildJSONMessage();
        return Response.status(response.getResponseStatus())
                .header(OAuthConstants.HTTP_RESP_HEADER_AUTHENTICATE, EndpointUtil.getRealmInfo())
                .entity(response.getBody()).build();
    }
}
