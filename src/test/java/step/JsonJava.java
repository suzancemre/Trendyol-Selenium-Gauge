package step;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

public class JsonJava {

    private static final Pattern pattern = Pattern.compile("body");

    private JsonObject json ;

    protected JsonJava(JsonObject jsondata){
        this.json= jsondata;
    }

    private JsonJava okuDosyaJson(Path file){
        try{

            String dosya = Files.readString(file.toAbsolutePath());
            final JsonObject jsonDosyaIcerigi = JsonParser.parseString(dosya).getAsJsonObject();

            return new JsonJava(jsonDosyaIcerigi);
        }catch(IOException e){
            throw new RuntimeException("bulunamdı");

        }

    }

    public By typeBelirle(String belirleyici){
        if(pattern.matcher(belirleyici).find())
        return By.xpath(belirleyici);
        else
           return By.cssSelector(belirleyici);

    }

    public By JsonToLocator(String element){
        String donStr = json.get(element).getAsString();
        return typeBelirle(donStr);

    }

    public WebElement locatorAra(WebElement drive,String element){
        return JsonToLocator(element).findElement(drive);

    }


}
