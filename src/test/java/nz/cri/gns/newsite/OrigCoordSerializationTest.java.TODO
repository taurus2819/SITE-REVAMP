package nz.cri.gns.newsite;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.http.converter.autoconfigure.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.http.converter.autoconfigure.ServerHttpMessageConvertersCustomizer;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.jackson2.autoconfigure.Jackson2AutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.boot.webmvc.autoconfigure.WebMvcAutoConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverters;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrigCoordSerializationTest {

    @ParameterizedTest
    @ValueSource(strings = {"dev", "uat", "prod"})
    @SuppressWarnings("removal")
    void mvcGetPreservesCoordinateFieldsWithApplicationProfile(String profile) {
        new WebApplicationContextRunner()
                .withInitializer(new ConfigDataApplicationContextInitializer())
                .withPropertyValues("spring.profiles.active=" + profile)
                .withConfiguration(AutoConfigurations.of(JacksonAutoConfiguration.class,
                        Jackson2AutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class,
                        WebMvcAutoConfiguration.class))
                .withBean(CoordinateController.class)
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    var mvc = MockMvcBuilders.webAppContextSetup(context).build();
                    var result = mvc.perform(get("/serialization-check").accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isOk()).andReturn();
                    var mapper = new ObjectMapper();
                    var coordinate = mapper.readTree(result.getResponse().getContentAsString()).get("origCoord");
                    assertThat(coordinate).isEqualTo(mapper.readTree(CoordinateController.COORDINATE));
                });
    }

    @RestController
    static class CoordinateController {
        static final String COORDINATE =
                "{\"epsg\":4326,\"format\":\"DD\",\"latitude\":\"-45.5675\",\"longitude\":\"172.44\"}";

        @GetMapping("/serialization-check")
        public CoordinateResponse getCoordinate() throws Exception {
            return new CoordinateResponse(new ObjectMapper().readTree(COORDINATE));
        }
    }

    record CoordinateResponse(com.fasterxml.jackson.databind.JsonNode origCoord) {
    }

    @ParameterizedTest
    @ValueSource(strings = {"jackson", "jackson2"})
    @SuppressWarnings("removal")
    void mvcGetPreservesCoordinateFieldsWithEitherMapper(String mapperName) {
        new WebApplicationContextRunner()
                .withPropertyValues("spring.http.converters.preferred-json-mapper=" + mapperName)
                .withConfiguration(AutoConfigurations.of(JacksonAutoConfiguration.class,
                        Jackson2AutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class,
                        WebMvcAutoConfiguration.class))
                .withBean(CompatibleCoordinateController.class)
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    var mvc = MockMvcBuilders.webAppContextSetup(context).build();
                    var result = mvc.perform(get("/serialization-compatible").accept(MediaType.APPLICATION_JSON))
                            .andExpect(status().isOk()).andReturn();
                    var mapper = new ObjectMapper();
                    assertThat(mapper.readTree(result.getResponse().getContentAsString()).get("origCoord"))
                            .isEqualTo(mapper.readTree(CoordinateController.COORDINATE));
                });
    }

    @RestController
    static class CompatibleCoordinateController {
        @GetMapping("/serialization-compatible")
        public CompatibleCoordinateResponse getCoordinate() throws Exception {
            return new CompatibleCoordinateResponse(new ObjectMapper().readTree(CoordinateController.COORDINATE));
        }
    }

    record CompatibleCoordinateResponse(
            @tools.jackson.databind.annotation.JsonSerialize(
                    using = nz.cri.gns.newsite.utils.Jackson2JsonNodeSerializer.class)
            com.fasterxml.jackson.databind.JsonNode origCoord) {
    }

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
