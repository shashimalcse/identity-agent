package org.wso2.carbon.identity.agent.core.model;

import org.apache.oltu.oauth2.common.utils.OAuthUtils;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

/**
 * AgentTokenRequest holds all agent auth token request parameters.
 */
public class AgentTokenRequest {

    private final HttpServletRequest request;

    public AgentTokenRequest(HttpServletRequest request) {
        this.request = request;
    }

    public String getParam(String name) {

        return this.request.getParameter(name);
    }

    public String getAgentId() {
        String[] credentials = OAuthUtils.decodeClientAuthenticationHeader(this.request.getHeader("Authorization"));
        return credentials != null ? credentials[0] : this.getParam("agent_id");
    }

    public String getAgentSecret() {
        String[] credentials = OAuthUtils.decodeClientAuthenticationHeader(this.request.getHeader("Authorization"));
        return credentials != null ? credentials[1] : this.getParam("agent_secret");
    }

    public Set<String> getScopes() {
        String scopes = this.getParam("scope");
        return OAuthUtils.decodeScopes(scopes);
    }

}
