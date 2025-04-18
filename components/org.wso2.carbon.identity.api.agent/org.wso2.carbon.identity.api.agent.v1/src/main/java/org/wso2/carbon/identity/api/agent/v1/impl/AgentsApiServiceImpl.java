/*
 * Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.api.agent.v1.impl;

import org.wso2.carbon.identity.api.agent.v1.AgentsApiService;
import org.wso2.carbon.identity.api.agent.v1.model.AgentCreateRequest;
import org.wso2.carbon.identity.api.agent.v1.model.AgentUpdateRequest;
import org.wso2.carbon.identity.api.agent.v1.model.CredentialBase;

import javax.ws.rs.core.Response;

/**
 * Implementation of the Agents API service.
 * This class provides methods to manage agents and their credentials.
 */
public class AgentsApiServiceImpl implements AgentsApiService {

    @Override
    public Response addAgentCredential(String agentId, CredentialBase credentialBase) {

        // do some magic!
        return Response.ok().entity("magic!").build();
    }

    @Override
    public Response createAgent(AgentCreateRequest agentCreateRequest) {

        // do some magic!
        return Response.ok().entity("magic!").build();
    }

    @Override
    public Response deleteAgent(String agentId) {

        // do some magic!
        return Response.ok().entity("magic!").build();
    }

    @Override
    public Response getAgentById(String agentId) {

        // do some magic!
        return Response.ok().entity("magic!").build();
    }

    @Override
    public Response listAgents() {

        // do some magic!
        return Response.ok().entity("magic!").build();
    }

    @Override
    public Response updateAgent(String agentId, AgentUpdateRequest agentUpdateRequest) {

        // do some magic!
        return Response.ok().entity("magic!").build();
    }

    @Override
    public Response updateAgentCredential(String agentId, String credentialId, CredentialBase credentialBase) {

        // do some magic!
        return Response.ok().entity("magic!").build();
    }
}
