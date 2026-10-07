package nz.cri.gns.newsite;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.http.converter.autoconfigure.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.http.converter.autoconfigure.ServerHttpMessageConvertersCustomizer;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.jackson2.autoconfigure.Jackson2AutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverters;
import org.springframework.mock.http.MockHttpOutputMessage;

import static org.assertj.core.api.Assertions.assertThat;

class OrigCoordSerializationTest {

    @Test
    @SuppressWarnings({"unchecked", "removal"})
    void httpResponsePreservesJackson2CoordinateTree() throws Exception {
        String mapper = PropertiesLoaderUtils.loadProperties(new ClassPathResource("application.properties"))
                .getProperty("spring.http.converters.preferred-json-mapper");
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(JacksonAutoConfiguration.class,
                        Jackson2AutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class))
                .withPropertyValues("spring.http.converters.preferred-json-mapper=" + mapper)
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    var builder = HttpMessageConverters.forServer();
                    context.getBeanProvider(ServerHttpMessageConvertersCustomizer.class)
                            .orderedStream().forEach(customizer -> customizer.customize(builder));
                    var response = Map.of("origCoord", new ObjectMapper().readTree(
                            "{\"epsg\":4326,\"format\":\"DD\",\"latitude\":\"-45.5675\",\"longitude\":\"172.44\"}"));
                    for (var converter : builder.build()) {
                        if (converter.canWrite(response.getClass(), MediaType.APPLICATION_JSON)) {
                            var output = new MockHttpOutputMessage();
                            ((HttpMessageConverter<Object>) converter).write(response, MediaType.APPLICATION_JSON, output);
                            var json = new ObjectMapper().readTree(output.getBodyAsString());
                            assertThat(json.get("origCoord")).isEqualTo(response.get("origCoord"));
                            assertThat(json.get("origCoord").has("nodeType")).isFalse();
                            return;
                        }
                    }
                    throw new AssertionError("No JSON HTTP converter configured");
                });
    }
}
