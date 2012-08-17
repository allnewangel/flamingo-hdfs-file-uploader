package org.openflamingo.uploader.policy;

import org.openflamingo.uploader.jaxb.Policy;

/**
 * Policy.
 *
 * @author Edward KIM
 * @since 0.1
 */
public class PolicyFactory {

    public static PolicyExecutionStrategy getPolicyExecutionStrategy(Policy policy) {
        Policy.Ingress ingress = policy.getIngress();

        Policy.Outgress outgress = policy.getOutgress();

        return null;
    }
}
