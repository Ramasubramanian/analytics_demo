package in.raam.analytics.app;

import in.raam.analytics.util.AppConfig;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.stereotype.Component;

/**
 * Convenience class to customize embedded web container of Spring Boot framework
 */
@Component
public class ContainerCustomizationBean implements EmbeddedServletContainerCustomizer {

    @Override
    public void customize(ConfigurableEmbeddedServletContainer container) {
        container.setPort(AppConfig.I.webContainerPort());
    }

}
