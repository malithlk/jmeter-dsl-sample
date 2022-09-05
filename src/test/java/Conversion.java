//import static org.assertj.core.api.Assertions.assertThat;
//import static us.abstracta.jmeter.javadsl.JmeterDsl.*;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//import org.junit.jupiter.api.Test;
//import org.junit.platform.engine.discovery.DiscoverySelectors;
//import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
//import org.junit.platform.launcher.core.LauncherFactory;
//import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
//import org.junit.platform.launcher.listeners.TestExecutionSummary;
//import us.abstracta.jmeter.javadsl.core.TestPlanStats;
//
//public class Conversion {
//
//    @Test
//    public void test() throws IOException {
//        TestPlanStats stats = testPlan(
//                httpDefaults(),
//                httpCookies()
//                        .disable(),
//                threadGroup(1, 1,
//                        transaction("TC-1",
//                                httpSampler("HR-1", "")
//                        ),
//                        transaction("TC-2",
//                                httpSampler("HR-2", "")
//                        )
//                )
//        )
//                .run();
//        assertThat(stats.overall().errorsCount()).isEqualTo(0);
//    }
//
//    /*
//     This method is only included to make test class self executable. You can remove it when
//     executing tests with maven, gradle or some other tool.
//     */
//    public static void main(String[] args) {
//        SummaryGeneratingListener summaryListener = new SummaryGeneratingListener();
//        LauncherFactory.create()
//                .execute(LauncherDiscoveryRequestBuilder.request()
//                                .selectors(DiscoverySelectors.selectClass(PerformanceTest.class))
//                                .build(),
//                        summaryListener);
//        TestExecutionSummary summary = summaryListener.getSummary();
//        summary.printFailuresTo(new PrintWriter(System.out));
//        System.exit(summary.getTotalFailureCount() > 0 ? 1 : 0);
//    }
//
//}
