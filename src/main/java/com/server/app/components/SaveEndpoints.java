package com.server.app.components;

import com.server.app.config.SecurityRules;
import com.server.app.services.PermissionService;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SaveEndpoints implements ApplicationListener<ApplicationEvent> {

    private final RequestMappingHandlerMapping handlerMapping;
    private final PermissionService permissionService;

    public SaveEndpoints(RequestMappingHandlerMapping handlerMapping, PermissionService permissionService) {
        this.handlerMapping = handlerMapping;
        this.permissionService = permissionService;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        handlerMapping.getHandlerMethods()
                .forEach((info, method) -> {
                    Set<String> paths = getPaths(info);
                    Set<RequestMethod> methods =
                            info.getMethodsCondition().getMethods();

                    if (methods.isEmpty()) {
                        methods = Set.of(RequestMethod.GET);
                    }

                    for (String path : paths) {
                        for (var httpMethod: methods) {
                            processEndpoint(path, httpMethod.name());
                        }
                    }
                });
    }

    private Set<String> getPaths(RequestMappingInfo info) {
        if (info.getPathPatternsCondition() != null) {
            return info.getPathPatternsCondition()
                    .getPatterns()
                    .stream()
                    .map(Object::toString)
                    .collect(Collectors.toSet());
        }

        else if (info.getPatternsCondition() != null) {
            return info.getPatternsCondition().getPatterns();
        }

        return Set.of();
    }

    private void processEndpoint(String path, String method) {

        if (SecurityRules.isIgnored(path)) {
            return;
        }

        if (SecurityRules.isPublic(method, path)) {
            return;
        }

        if (SecurityRules.isAuthOnly(method, path)) {
            return;
        }

        permissionService.createIfNotExists(path, method);
    }
}
