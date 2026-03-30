package wlsh.project.intervai.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import wlsh.project.intervai.common.auth.application.AccessTokenProvider;
import wlsh.project.intervai.common.auth.presentation.filter.JwtAuthenticationFilter;
import wlsh.project.intervai.common.config.SecurityConfig;

@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
public abstract class AcceptanceTest {

    @Autowired
    protected ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    protected AccessTokenProvider accessTokenProvider;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }
}
