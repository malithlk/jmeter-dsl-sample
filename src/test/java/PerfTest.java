import static org.assertj.core.api.Assertions.assertThat;
import static us.abstracta.jmeter.javadsl.JmeterDsl.*;
import static us.abstracta.jmeter.javadsl.dashboard.DashboardVisualizer.dashboardVisualizer;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.apache.jmeter.protocol.http.util.HTTPConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import us.abstracta.jmeter.javadsl.core.TestPlanStats;


@WireMockTest
public class PerfTest {

    protected WireMockRuntimeInfo wiremockServer;
    protected String wiremockUri;


    @BeforeEach
    public void setup(WireMockRuntimeInfo wiremock) {
        wiremockServer = wiremock;
        WireMock.stubFor(any(anyUrl()));
        wiremockUri = wiremock.getHttpBaseUrl();
    }

    @AfterEach
    public void teardown() {

    }


    @Test
    public void testPerformance() throws IOException {

        String HOST = "http://localhost:3004";
        String INFLUXDB="http://localhost:8086/write?db=jmeter_results";

        TestPlanStats stats = testPlan(
                httpCookies().disable(),
                httpCache().disable(),
                httpDefaults()
                        .url(wiremockUri)
                        .downloadEmbeddedResources(),
//                setupThreadGroup(
//                        httpSampler("http://my.service/tokens")
//                                .method(HTTPConstants.POST)
//                                .children(
//                                        jsr223PostProcessor("props.put('MY_TEST_TOKEN', prev.responseDataAsString)")
//                                )
//                ),
                threadGroup()
                        .rampToAndHold(5, Duration.ofSeconds(5), Duration.ofSeconds(20))
//                        .rampToAndHold(30, Duration.ofSeconds(10), Duration.ofSeconds(30))
//                        .rampTo(100, Duration.ofSeconds(10))
//                        .rampToAndHold(30, Duration.ofSeconds(10), Duration.ofSeconds(30))
//                        .rampTo(0, Duration.ofSeconds(5))
                        .children(
                                transaction("Sample 1",
                                        httpSampler( "/posts/1").followRedirects(true)
                                ),
                                transaction("Sample 2",
                                        httpSampler( "/persons").followRedirects(true)
                                ),
                                transaction("Sample 3",
                                        httpSampler( "/profile").followRedirects(true)
                                )
                ),
//                teardownThreadGroup(
//                        httpSampler("http://my.service/tokens/${__P(MY_TEST_TOKEN)}")
//                                .method(HTTPConstants.DELETE)
//                ),
                influxDbListener(INFLUXDB)
                //dashboardVisualizer()
                //this is just to log details of each request stats
//                jtlWriter("test" + Instant.now().toString().replace(":", "-") + ".jtl")
        ).run();
        assertThat(stats.overall().sampleTimePercentile99()).isLessThan(Duration.ofSeconds(1));
    }

}
