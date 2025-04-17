package org.wso2.carbon.identity.agent.core.internal;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * Agent core service component.
 */
@Component(
        name = "identity.agent.core.component",
        immediate = true
)
public class AgentServiceComponent {

    @Activate
    protected void activate(ComponentContext context) {

    }

    @Deactivate
    protected void deactivate(ComponentContext context) {

    }
}
