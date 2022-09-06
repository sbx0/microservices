package lb;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.*;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.SelectedInstanceCallback;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Prefer services of the same version
 *
 * @author sbx0
 * @since 2022/3/10
 */
public class MyLoadBalancer implements ReactorServiceInstanceLoadBalancer {

    private static final Log log = LogFactory.getLog(MyLoadBalancer.class);

    private final String serviceId;

    private final ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;

    public MyLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
                          String serviceId) {
        this.serviceId = serviceId;
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
    }

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        RequestDataContext requestDataContext = (RequestDataContext) request.getContext();
        RequestData requestData = requestDataContext.getClientRequest();
        ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider
                .getIfAvailable(NoopServiceInstanceListSupplier::new);
        return supplier.get(request).next()
                .map(serviceInstances -> processInstanceResponse(supplier, serviceInstances, requestData));
    }

    private Response<ServiceInstance> processInstanceResponse(ServiceInstanceListSupplier supplier,
                                                              List<ServiceInstance> serviceInstances, RequestData requestData) {
        Response<ServiceInstance> serviceInstanceResponse = getInstanceResponse(serviceInstances, requestData);
        if (supplier instanceof SelectedInstanceCallback && serviceInstanceResponse.hasServer()) {
            ((SelectedInstanceCallback) supplier).selectedServiceInstance(serviceInstanceResponse.getServer());
        }
        return serviceInstanceResponse;
    }

    private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances, RequestData requestData) {
        if (instances.isEmpty()) {
            if (log.isWarnEnabled()) {
                log.warn("No servers available for service: " + serviceId);
            }
            return new EmptyResponse();
        }

        String instanceId = requestData.getHeaders().getFirst("instance-id");
        if (StringUtils.hasText(instanceId)) {
            Optional<ServiceInstance> first = instances.stream().filter(one -> one.getInstanceId().equals(instanceId)).findFirst();
            if (first.isPresent()) {
                return new DefaultResponse(first.get());
            } else {
                log.info("no match instance id");
            }
        }

        String region = requestData.getHeaders().getFirst("region");
        if (StringUtils.hasText(region)) {
            List<ServiceInstance> matchVersionInstances = instances.stream().filter(one -> one.getMetadata().getOrDefault("region", "local").equals(region)).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(matchVersionInstances)) {
                instances = matchVersionInstances;
            } else {
                log.info("no match region service");
            }
        }

        String version = requestData.getHeaders().getFirst("version");
        if (StringUtils.hasText(version)) {
            List<ServiceInstance> matchVersionInstances = instances.stream().filter(one -> one.getMetadata().getOrDefault("version", "0.0.0").equals(version)).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(matchVersionInstances)) {
                instances = matchVersionInstances;
            } else {
                log.info("no match version service");
            }
        }

        int index = ThreadLocalRandom.current().nextInt(instances.size());

        ServiceInstance instance = instances.get(index);

        log.info("LoadBalancer choose " + serviceId + "[" + version + "] " + instance.getHost() + ":" + instance.getPort());

        return new DefaultResponse(instance);
    }
}
