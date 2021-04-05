package cn.icodening.rpc.spring;

import cn.icodening.rpc.config.ServiceConfig;
import cn.icodening.rpc.config.annotation.NrpcService;
import cn.icodening.rpc.core.util.ReflectUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.rootBeanDefinition;
import static org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation;

/**
 * 解析扫描到的  {@link cn.icodening.rpc.config.annotation.NrpcService}
 * 并根据其配置实例化对应的ServiceConfig对象，放入Spring IoC容器中
 *
 * @author icodening
 * @date 2021.04.04
 */
public class NrpcServiceAnnotationPostProcessor implements BeanDefinitionRegistryPostProcessor {

    private final Set<String> packagesToScan;

    private final BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();

    public NrpcServiceAnnotationPostProcessor(String... packagesToScan) {
        this(asList(packagesToScan));
    }

    public NrpcServiceAnnotationPostProcessor(Collection<String> packagesToScan) {
        this(new LinkedHashSet<>(packagesToScan));
    }

    public NrpcServiceAnnotationPostProcessor(Set<String> packagesToScan) {
        this.packagesToScan = packagesToScan;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        //1.构建classpath bean定义扫描器
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry);
        scanner.addIncludeFilter(new AnnotationTypeFilter(NrpcService.class));

        //2.遍历扫描指定包
        for (String packageToScan : packagesToScan) {
            scanner.scan(packageToScan);
            Set<BeanDefinition> beanDefinitions = scanner.findCandidateComponents(packageToScan);
            Set<BeanDefinitionHolder> beanDefinitionHolders = new LinkedHashSet<>(beanDefinitions.size());
            //3.根据扫描到的bean定义，构建对应的Holder
            for (BeanDefinition beanDefinition : beanDefinitions) {
                String beanName = beanNameGenerator.generateBeanName(beanDefinition, registry);
                BeanDefinitionHolder beanDefinitionHolder = new BeanDefinitionHolder(beanDefinition, beanName);
                beanDefinitionHolders.add(beanDefinitionHolder);
            }
            for (BeanDefinitionHolder beanDefinitionHolder : beanDefinitionHolders) {
                //4.获取加载到的bean class
                String beanClassName = beanDefinitionHolder.getBeanDefinition().getBeanClassName();
                if (beanClassName == null) {

                    continue;
                }
                Class<?> beanClazz = ClassUtils.resolveClassName(beanClassName, Thread.currentThread().getContextClassLoader());
                //5.获取对应的注解，如果为空则跳过
                Annotation service = findMergedAnnotation(beanClazz, NrpcService.class);
                if (service == null) {
                    continue;
                }
                //6.获取bean的所有接口
                Class<?>[] allInterfaces = ReflectUtil.getAllInterfaces(beanClazz);
                String annotatedServiceBeanName = beanDefinitionHolder.getBeanName();

                //7.构建对应的ServiceConfig bean定义
                //FIXME 目前注解不可配置，仅做基本属性处理
                for (Class<?> interfaceClass : allInterfaces) {
                    BeanDefinitionBuilder builder = rootBeanDefinition(ServiceConfig.class);
                    builder.addPropertyReference("reference", annotatedServiceBeanName);
                    builder.addPropertyValue("name", interfaceClass.getName());
                    builder.addPropertyValue("serviceInterface", interfaceClass);
                    String beanName = interfaceClass.getName();
                    registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
                }
            }
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }
}
