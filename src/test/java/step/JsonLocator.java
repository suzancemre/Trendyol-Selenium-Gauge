package step;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

public class JsonLocator {
    //Xpath formatı
    //Bu bir Regex yapısı.Bir metin Xpath mi yoksa CSS mi onu anlamaya yarıyor sadece.
    private static final Pattern xpathPattern = Pattern.compile("^((body)|(\\/\\/)).*");

    //Bu JSON dosyanın içeriği
    //Elemanları biz bunda tutacağız. Daha sonra bana şu elemanı ver dediğinde buradan toplayacak.
    private final JsonObject baseJson;

    //Bu sadece verilen JSON'u kaydeden bir fonk. Diskteki dosyayı RAM'e alıyor
    protected JsonLocator(JsonObject locatorList) {
        this.baseJson = locatorList;
    }

    //Bu bir Path (Dosya yolu) okuyarak bize bir JSON döndürüyor.
    //normalde her dosyanın içeriği bir String'den ibarettir. Java'ya bunun sadece bir String olmadığını, bir JSON olduğunu anlatmak gerekiyor
    public static JsonLocator ofFile(Path file){
        try {
            //Dosyanın içeriğini oku
            String contents = Files.readString(file.toAbsolutePath());

            //Bu okunan String'i JSON'a çevir
            final JsonObject asJsonObject = JsonParser.parseString(contents).getAsJsonObject();

            //#25 i kullanarak bu JSON'u RAM'e kaydet
            return new JsonLocator(asJsonObject);
        } catch (IOException e) {
            //Eğer dosya hata verdiyse (Pathi yanlış vermişizdir vs.) kod da hata versin
            //Hata mesajında da dosyanın yolunu yazsın
            throw new RuntimeException("Bu dosya bulunamadı: " + file.toAbsolutePath().toString());
        }
    }

    //Bu fonksiyon verilen String'i (JSON valuesi) bir Locator'a çevirmeye yarar
    // Xpath mi yoksa CSS mi olduğuna karar verir
    private By returnWithType(String locator){
        //Eğer xpath ise
        if(xpathPattern.matcher(locator).find())
            //xpath olarak döndür
            return By.xpath(locator);
        //Else olmasa da olur çünkü üstteki return bitirici bir şey. Ama anlaman için koyayım.
        else
            //Değilse, css olarak döndür
            return By.cssSelector(locator);
    }

    //Bu bir bulucu fonksiyon diyelim
    //Üstteki özellikleri kullanarak, RAM'deki JSON'u okuyup, sana Locator olarak geri dönüdüryor
    //Tek işlevi üsttekileri kullanmak, kendi başına bir olayı yok
    public By locate(String key){

        if(!baseJson.has(key))
//            throw new IllegalArgumentException("key '%s' is not found on the JSON file".formatted(key));
            throw new IllegalArgumentException("'%s' isimli locator bulunamadı.");
        //JSON'da key'i arat ve Value'sini al (solu ara, sağdakini al)
        String locator = baseJson.get(key).getAsString();

        //Value'nin tipini anla ve Locator olarak döndür
        return returnWithType(locator);
    }

    //Kısayol olsun diye yapılmış bir fonksiyon.
    //Tek işlevi yukarıda bulduğun Locator'u, oluşturduğun ChromeDriver'da aramak
    public WebElement locateElement(WebDriver driver, String key){
        //bu kısım şuna eşdeğer
        /*
        By locator = locate(key);
        return driver.findElement(locator)
        */
         //İkisinin de işlevi birebir aynı. Locator'u öğeyi denetle kısmında (DOM'da) aratıyor
        return locate(key).findElement(driver);
    }

    //üstteki fonksiyonun çoklu aratan hali
    public List<WebElement> locateElements(WebDriver driver, String key){
        return locate(key).findElements(driver);
    }
}