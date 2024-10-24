package base;
import com.thoughtworks.gauge.AfterScenario;
import com.thoughtworks.gauge.BeforeScenario;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

public class BaseTest {

    protected static WebDriver chromeDriver;
    protected Logger logger = LoggerFactory.getLogger(getClass());



    @BeforeScenario
    public void setup() {
        WebDriverManager.chromedriver().setup();
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values.notifications", 2);
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs", prefs);
        chromeDriver = new ChromeDriver(options);
        chromeDriver.manage().window().maximize();
        
    }

   @AfterScenario
    public void quit(){
         chromeDriver.quit();
    }

}
