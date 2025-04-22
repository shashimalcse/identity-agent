package org.wso2.carbon.identity.api.agent.v1.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.api.agent.v1.common.AgentConstants;
import org.wso2.carbon.identity.api.agent.v1.error.APIError;
import org.wso2.carbon.identity.api.agent.v1.error.ErrorResponse;
import org.wso2.carbon.identity.core.util.IdentityTenantUtil;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.common.AbstractUserStoreManager;
import org.wso2.carbon.user.core.service.RealmService;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import static org.wso2.carbon.identity.api.agent.v1.common.AgentConstants.ErrorMessage.SERVER_ERROR_RETRIEVING_USERSTORE_MANAGER;

/**
 * Service class to interact with the UserStoreManager for agent-related
 * operations.
 */
public class AgentService {

    private final RealmService realmService;

    private static final Log log = LogFactory.getLog(AgentService.class);

    public AgentService(RealmService realmService) {

        this.realmService = realmService;
    }

    /**
     * 
     * Retrieve a list of users from the UserStoreManager.
     *
     * @return List of agents.
     */
    public List<String> getAgents() {
        List<String> agents = new ArrayList<>();

        try {
            // UserStoreManager userStoreManager = realmService.getTenantUserRealm(IdentityTenantUtil
            //         .getTenantId(getTenantDomain())).getUserStoreManager();
            AbstractUserStoreManager userStoreManager = (AbstractUserStoreManager) realmService
                    .getTenantUserRealm(IdentityTenantUtil
                            .getTenantId(getTenantDomain()))
                    .getUserStoreManager();
            if (userStoreManager == null) {
                if (log.isDebugEnabled()) {
                    log.debug("Unable to retrieve userstore manager.");
                }
                throw handleError(Response.Status.INTERNAL_SERVER_ERROR, SERVER_ERROR_RETRIEVING_USERSTORE_MANAGER);
            }
            List<org.wso2.carbon.user.core.common.User> users = userStoreManager.listUsersWithID("*", -1);
                for (org.wso2.carbon.user.core.common.User user : users) {
                    agents.add(user.getUserID());
                }
            return agents;
        } catch (UserStoreException e) {
            throw handleException(e, SERVER_ERROR_RETRIEVING_USERSTORE_MANAGER);
        }
    }

    private String getTenantDomain() {

        return IdentityTenantUtil.resolveTenantDomain();
    }

    /**
     * Handle Exceptions.
     *
     * @param e         Exception
     * @param errorEnum Error message enum.
     * @return An APIError.
     */
    private APIError handleException(Exception e, AgentConstants.ErrorMessage errorEnum, String... data) {

        ErrorResponse errorResponse;
        if (data != null) {
            errorResponse = getErrorBuilder(errorEnum).build(log, e, String.format(errorEnum.getDescription(),
                    (Object[]) data));
        } else {
            errorResponse = getErrorBuilder(errorEnum).build(log, e, errorEnum.getDescription());
        }

        if (e instanceof UserStoreException) {
            return new APIError(Response.Status.INTERNAL_SERVER_ERROR, errorResponse);
        } else {
            return new APIError(Response.Status.BAD_REQUEST, errorResponse);
        }
    }

    /**
     * Handle User errors.
     *
     * @param status Http status.
     * @param error  Error .
     * @return An APIError.
     */
    private APIError handleError(Response.Status status, AgentConstants.ErrorMessage error) {

        return new APIError(status, getErrorBuilder(error).build());
    }

    /**
     * Get ErrorResponse Builder for Error enum.
     *
     * @param errorEnum Error message enum.
     * @return Error response for the given errorEnum.
     */
    private ErrorResponse.Builder getErrorBuilder(AgentConstants.ErrorMessage errorEnum) {

        return new ErrorResponse.Builder().withCode(errorEnum.getCode()).withMessage(errorEnum.getMessage())
                .withDescription(errorEnum.getDescription());
    }

}
