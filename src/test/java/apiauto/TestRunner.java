package apiauto;

import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TestRunner {

    public static void main(String[] args) throws IOException, InterruptedException {
        TestListenerAdapter tla = new TestListenerAdapter();
        TestNG testng = new TestNG();
        List<String> suites = new ArrayList<>();
        suites.add("src/test/resources/testng.xml");
        testng.setTestSuites(suites);
        testng.addListener(tla);
        System.setProperty("allure.results.directory", "build/allure-results");
        testng.run();

        List<ITestResult> results = tla.getPassedTests();
        results.addAll(tla.getSkippedTests());
        results.addAll(tla.getFailedTests());
        for(ITestResult result: results){
            System.out.println("Method " + result.getName() + " : status " + getStatus(result.getStatus()));
        }

        generateAllureReport();

    }

    public static void generateAllureReport() throws InterruptedException, IOException {
        // Launch Allure report after tests are done and wait specified second
        ProcessBuilder allure = new ProcessBuilder("allure", "serve", "build/allure-results");
        Process p = allure.start();
        p.waitFor(60, TimeUnit.SECONDS);

        // Kill the process after report is closed
        p.destroy();
    }

    private static String getStatus(int status){
        String statusStr;
        switch(status){
            case 1:
                statusStr = "PASSED";
                break;
            case 2:
                statusStr = "FAILED";
                break;
            case 3:
                statusStr = "SKIPPED";
                break;
            default:
                statusStr = "UNKNOWN";
        }
        return statusStr;
    }

}
