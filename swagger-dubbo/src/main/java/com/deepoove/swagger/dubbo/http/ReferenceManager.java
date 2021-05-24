package com.deepoove.swagger.dubbo.http;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.spring.ServiceBean;
import org.apache.dubbo.config.spring.extension.SpringExtensionFactory;

public class ReferenceManager {
    
    private static Logger logger = LoggerFactory.getLogger(ReferenceManager.class);

    @SuppressWarnings("rawtypes")
    private static Collection<ServiceBean> services;

    private static Map<Class<?>, Object> interfaceMapProxy = new ConcurrentHashMap<Class<?>, Object>();
    private static Map<Class<?>, Object> interfaceMapRef = new ConcurrentHashMap<Class<?>, Object>();

    private static ReferenceManager instance;
    private static ApplicationConfig application;

    private ReferenceManager() {}

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public synchronized static ReferenceManager getInstance() {
        if (null != instance) return instance;
        instance = new ReferenceManager();
        services = new HashSet<>();
        try {
            Field field = SpringExtensionFactory.class.getDeclaredField("CONTEXTS");
            field.setAccessible(true);
            Set<ApplicationContext> contexts = (Set<ApplicationContext>)field.get(new SpringExtensionFactory());
            for (ApplicationContext context : contexts){
                services.addAll(context.getBeansOfType(ServiceBean.class).values());
            }
        } catch (Exception e) {
            logger.error("Get All Dubbo Service Error", e);
            return instance;
        }
        for (ServiceBean<?> bean : services) {
            String beanName = bean.getBeanName(); // beanName="ServiceBean:com.xfdsx.api.procurement.ProcurementService:v1.1.0"
            String path = bean.getPath(); // path="com.xfdsx.api.procurement.ProcurementService"
            String suffixPath = beanName.substring(beanName.lastIndexOf(":") + 1);
            if (path.equals("com.xfdsx.api.procurement.ProcurementService")) {
                if (suffixPath.equals("v1.1.0")) { // 只请求v1.1.0版本
                    interfaceMapRef.putIfAbsent(bean.getInterfaceClass(), bean.getRef());
                }

            }else {
                // 其他接口直接放
                interfaceMapRef.putIfAbsent(bean.getInterfaceClass(), bean.getRef());
            }
        }
        
        //
        if (!services.isEmpty()) {
			ServiceBean<?> bean = services.toArray(new ServiceBean[]{})[0];
			application = bean.getApplication();
        }
        
        return instance;
    }

    public Object getProxy(String interfaceClass) {
        Set<Entry<Class<?>, Object>> entrySet = interfaceMapProxy.entrySet();
        for (Entry<Class<?>, Object> entry : entrySet) {
            if (entry.getKey().getName().equals(interfaceClass)) { return entry.getValue(); }
        }

        for (ServiceBean<?> service : services) {
            if (interfaceClass.equals(service.getInterfaceClass().getName())) {
                ReferenceConfig<Object> reference = new ReferenceConfig<Object>();
                reference.setApplication(service.getApplication());
                reference.setRegistry(service.getRegistry());
                reference.setRegistries(service.getRegistries());
                reference.setInterface(service.getInterfaceClass());
                reference.setVersion(service.getVersion());

                reference.setGroup(service.getGroup());
                reference.setRetries(service.getRetries());
                reference.setActives(service.getActives());
                reference.setCache(service.getCache());
                // 集群方式
                reference.setCluster(service.getCluster());
                reference.setLayer(service.getLayer());
                reference.setTimeout(service.getTimeout());
                reference.setConnections(service.getConnections());
                // 负载均衡策略
                reference.setLoadbalance(service.getLoadbalance());

                interfaceMapProxy.put(service.getInterfaceClass(), reference.get());
                return reference.get();
            }
        }
        return null;
    }

    public Entry<Class<?>, Object> getRef(String interfaceClass) {
        Set<Entry<Class<?>, Object>> entrySet = interfaceMapRef.entrySet();
        for (Entry<Class<?>, Object> entry : entrySet) {
            if (entry.getKey().getName().equals(interfaceClass)) { return entry; }
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    public Collection<ServiceBean> getServices() {
        return services;
    }

    public ApplicationConfig getApplication() {
        return application;
    }

    public Map<Class<?>, Object> getInterfaceMapRef() {
        return interfaceMapRef;
    }

}
