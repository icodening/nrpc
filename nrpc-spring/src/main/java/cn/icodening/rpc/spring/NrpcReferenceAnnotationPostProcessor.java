package cn.icodening.rpc.spring;

import cn.icodening.rpc.config.NrpcStartedEvent;
import cn.icodening.rpc.config.ReferenceConfig;
import cn.icodening.rpc.config.annotation.NrpcReference;
import cn.icodening.rpc.config.cache.ReferenceLocalCache;
import cn.icodening.rpc.core.LocalCache;
import cn.icodening.rpc.core.event.EventPublisher;
import cn.icodening.rpc.core.event.NrpcEventListener;
import cn.icodening.rpc.core.event.Subscribe;
import cn.icodening.rpc.core.extension.ExtensionLoader;
import cn.icodening.rpc.core.util.MessageManager;
import cn.icodening.rpc.core.util.ReflectUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Nrpc远程引用注解处理器 {@link cn.icodening.rpc.config.annotation.NrpcReference}
 * 装配流程
 * 1. 获取bean元数据
 * 2. 将包含 {@link cn.icodening.rpc.config.annotation.NrpcReference} 注解的字段解析
 * 3. 构建对应的 {@link ReferenceConfig} Bean
 * 4. 构建 {@link InjectionMetadata}
 * 5. 重写 {@link InjectionMetadata#inject(Object, String, PropertyValues)} 方法，挂上NrpcBootstrap启动完毕的回调钩子
 * 6. Nrpc启动完毕后通过 ExtensionLoader.getExtensionLoader(LocalCache.class).getExtension("reference") 得到远程引用缓存
 * {@link ReferenceLocalCache}  获取对应的远程引用代理
 *
 * @author icodening
 * @date 2021.04.05
 */
public class NrpcReferenceAnnotationPostProcessor extends InstantiationAwareBeanPostProcessorAdapter
        implements MergedBeanDefinitionPostProcessor, BeanFactoryAware {

    private static final Logger LOGGER = Logger.getLogger(NrpcReferenceAnnotationPostProcessor.class);

    private final Map<String, InjectionMetadata> injectionMetadataCache = new ConcurrentHashMap<>(256);

    private final Set<Class<? extends Annotation>> nrpcReferenceTypes = new LinkedHashSet<>(4);

    private final Set<Class<?>> referenceInterfaces = new LinkedHashSet<>(256);

    private ConfigurableListableBeanFactory beanFactory;

    public NrpcReferenceAnnotationPostProcessor() {
        this.nrpcReferenceTypes.add(NrpcReference.class);
    }

    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        InjectionMetadata metadata = findNrpcReferenceMetadata(beanName, beanType, null);
        metadata.checkConfigMembers(beanDefinition);
    }

    private InjectionMetadata findNrpcReferenceMetadata(String beanName, Class<?> clazz, @Nullable PropertyValues pvs) {
        String cacheKey = (StringUtils.hasLength(beanName) ? beanName : clazz.getName());
        InjectionMetadata metadata = this.injectionMetadataCache.get(cacheKey);
        if (InjectionMetadata.needsRefresh(metadata, clazz)) {
            synchronized (this.injectionMetadataCache) {
                metadata = this.injectionMetadataCache.get(cacheKey);
                if (InjectionMetadata.needsRefresh(metadata, clazz)) {
                    if (metadata != null) {
                        metadata.clear(pvs);
                    }
                    metadata = buildNrpcReferenceMetadata(clazz);
                    this.injectionMetadataCache.put(cacheKey, metadata);
                }
            }
        }
        return metadata;
    }

    private ReferenceConfig buildReferenceConfig(Class<?> clazz) {
        ReferenceConfig referenceConfig = new ReferenceConfig();
        referenceConfig.setInterfaceClass(clazz);
        referenceConfig.setServiceName(clazz.getName());
        return referenceConfig;
    }

    private InjectionMetadata buildNrpcReferenceMetadata(final Class<?> clazz) {
        Field[] declaredFields = clazz.getDeclaredFields();
        boolean found = false;
        for (Field declaredField : declaredFields) {
            for (Class<? extends Annotation> nrpcReferenceType : this.nrpcReferenceTypes) {
                Annotation annotation = AnnotationUtils.findAnnotation(declaredField, nrpcReferenceType);
                if (annotation != null) {
                    if (referenceInterfaces.add(declaredField.getType())) {
                        ReferenceConfig referenceConfig = buildReferenceConfig(declaredField.getType());
                        beanFactory.registerSingleton(declaredField.getType().getName(), referenceConfig);
                    }
                    found = true;
                    break;
                }
            }
        }
        if (!found) {
            return InjectionMetadata.EMPTY;
        }
        List<InjectionMetadata.InjectedElement> elements = new ArrayList<>();
        Class<?> targetClass = clazz;

        do {
            final List<InjectionMetadata.InjectedElement> currElements = new ArrayList<>();

            ReflectionUtils.doWithLocalFields(targetClass, field -> {
                MergedAnnotation<?> ann = findNrpcReferenceAnnotation(field);
                if (ann != null) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        //不支持静态字段
                        return;
                    }
                    currElements.add(new NrpcReferenceFieldElement(field));
                }
            });
            elements.addAll(0, currElements);
            targetClass = targetClass.getSuperclass();
        }
        while (targetClass != null && targetClass != Object.class);

        return InjectionMetadata.forElements(elements, clazz);
    }

    @Nullable
    private MergedAnnotation<?> findNrpcReferenceAnnotation(AccessibleObject ao) {
        MergedAnnotations annotations = MergedAnnotations.from(ao);
        for (Class<? extends Annotation> type : this.nrpcReferenceTypes) {
            MergedAnnotation<?> annotation = annotations.get(type);
            if (annotation.isPresent()) {
                return annotation;
            }
        }
        return null;
    }

    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) {
        InjectionMetadata metadata = findNrpcReferenceMetadata(beanName, bean.getClass(), pvs);
        try {
            metadata.inject(bean, beanName, pvs);
        } catch (BeanCreationException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new BeanCreationException(beanName, "Injection of autowired dependencies failed", ex);
        }
        return pvs;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    private static class NrpcReferenceFieldElement extends InjectionMetadata.InjectedElement {

        public NrpcReferenceFieldElement(Field field) {
            super(field, null);
        }

        @Override
        protected void inject(Object bean, @Nullable String beanName, @Nullable PropertyValues pvs) throws Throwable {
            Field field = (Field) this.member;
            String typeName = field.getType().getName();
            EventPublisher extension = ExtensionLoader.getExtensionLoader(EventPublisher.class).getExtension();
            extension.register(new NrpcEventListener<NrpcStartedEvent>() {
                @Override
                @Subscribe
                public void onEvent(NrpcStartedEvent event) {
                    LocalCache<String, Object> referenceCache = ExtensionLoader.getExtensionLoader(LocalCache.class).getExtension("reference");
                    Object value = referenceCache.get(typeName);
                    if (value == null) {
                        LOGGER.warn(MessageManager.get("no.available.service", typeName));
                        return;
                    }
                    ReflectUtil.makeAccessible(field);
                    ReflectUtil.setFieldValue(bean, field, value);
                }
            });
        }
    }
}
