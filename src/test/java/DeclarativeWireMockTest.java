import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.JvmProxyConfigurer;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import us.abstracta.jmeter.javadsl.core.TestPlanStats;

import java.time.Duration;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static us.abstracta.jmeter.javadsl.JmeterDsl.*;
import static us.abstracta.jmeter.javadsl.JmeterDsl.httpDefaults;
import static us.abstracta.jmeter.javadsl.dashboard.DashboardVisualizer.dashboardVisualizer;

@WireMockTest(proxyMode = true)
public class DeclarativeWireMockTest {

    CloseableHttpClient client;

    @BeforeEach
    void init() {
        client = HttpClientBuilder.create()
                .useSystemProperties() // This must be enabled for auto proxy config
                .build();
    }


    @Test
    void test_something_with_wiremock(WireMockRuntimeInfo wmRuntimeInfo) throws Exception {
        stubFor(get("/posts")
                .withHost(equalTo("my.service"))
                .willReturn(ok("1")));

        stubFor(get("/persons")
                .withHost(equalTo("my.service"))
                .willReturn(ok("2")));

        TestPlanStats stats = testPlan(
                httpCookies().disable(),
                httpCache().disable(),
                httpDefaults()
                        .url("http://my.service")
                        .downloadEmbeddedResources(),

                threadGroup()
                        .rampToAndHold(5, Duration.ofSeconds(5), Duration.ofSeconds(20))
                        .children(
                                httpSampler("Sample 1", "/posts"),
                                httpSampler("Sample 2", "/persons")
                        ),
                dashboardVisualizer()

        ).run();
        Assertions.assertThat(stats.overall().sampleTimePercentile99()).isLessThan(Duration.ofSeconds(1));
    }

//        assertThat(getContent("http://one.my.domain/things"), is("1"));
//        assertThat(getContent("http://two.my.domain/things"), is("2"));

}
