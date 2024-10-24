package step;

import base.BaseTest;
import com.thoughtworks.gauge.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.apache.logging.log4j.core.config.Configurator;

import java.nio.file.Path;

import java.util.*;
import java.util.NoSuchElementException;



public class BaseStep extends BaseTest {

    //Json yolunu verip bu içeriği RAM'e kaydet
    public JsonLocator jsonLocator = JsonLocator.ofFile(Path.of("src", "test", "resources", "element", "element.json"));

    static {
        // Root logger'ın seviyesini INFO olarak belirleme
        Configurator.setRootLevel(org.apache.logging.log4j.Level.INFO);
    }

    private static final Logger logger = LogManager.getLogger();
    private JsonLocator jsonLocator1;


    @Step({"Go to <url> address",
            "<url> adresine git"})
    public void goToUrl(String url) {
        chromeDriver.get(url);

        //dosyaya gerek yok
        logger.info("{} adresine gidildi", url);

    }

    @Step({"<ele> varligini <sure> saniye boyunca bekle"})
    public void elementWait(String element, int sure) {

        int dongu = 0;
        while (dongu < sure) {
            if (jsonLocator.locateElements(chromeDriver, element).isEmpty()) {
                //if (chromeDriver.findElements(By.xpath(element)).size() > 0) {
                logger.info("element bulundu");
            }
            dongu++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            Assertions.fail("element bulunamamdi");
        }
    }

    @Step({"<zaman> saniye bekle"})
    public void wait(int zaman) {

        try {
            Thread.sleep(zaman * 1000L);
            logger.info("bekleniyor..");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Step({"<element> in iceigi <expected> ile esit mi diye kontrol et"})
    public void textKontrol(String element, String expected) {
        /*
        // RAM'den elemanı bul
        final WebElement eleman = jsonLocator.locateElement(chromeDriver, element);

        //textini al
        String orjinalText = eleman.getText();
        */
        //üstteki ikisini tek satırda birleştirdim sadece:
        String orjinalText = jsonLocator.locateElement(chromeDriver, element).getText();
        //Stepteki Expected ile kıyasla
        Assertions.assertEquals(expected, orjinalText);
    }

    public String getAttribute(String element, String attributeName) {
        return jsonLocator.locateElement(chromeDriver, element).getAttribute(attributeName);
        /*
        WebElement locatorElement = chromeDriver.findElement(By.xpath(element));
        String attributeValue = locatorElement.getAttribute(attributeName);
        return attributeValue;
         */
    }

    @Step({"<element> alanina <metin> metni yaz"})
    public void writeArae(String element, String metin) {
        WebElement findelement = jsonLocator.locateElement(chromeDriver, element);
        findelement.click();
        findelement.sendKeys(metin, Keys.ENTER);
        logger.info("'" + metin + "' metni '" + element + "' alanına yazıldı");

    }

    @Step({"<element> uzerinde bekle"})
    public void elementUzerindeBekle(String element) {
        WebElement findelementDom = jsonLocator.locateElement(chromeDriver, element);
        Actions actions = new Actions(chromeDriver);
        actions.moveToElement(findelementDom).perform();
        logger.info("'" + element + "' elementi üzerinde bekleniyor");
        wait(1);
    }

    @Step({"<element> ait <degisken> bulundu"})
    public void textAl(String element, String degisken) {

        WebElement ele = jsonLocator.locateElement(chromeDriver, element);
        String text = ele.getText();
        logger.info(text + " " + degisken + " bulundu");
    }


    @Step({"<element> e tikla"})
    public void click(String element) {

        try {
            wait(3);
            jsonLocator.locateElement(chromeDriver, element).click();


            logger.info("'" + element + "' elementine tıklandı");

        } catch (TimeoutException e) {
            Assertions.fail("bulunamadi");


        }

    }

    @Step({"<key> element var mi"})
    public void elementVarligi(String key) {
        wait(1);
        try {
            WebElement arananelement = jsonLocator.locateElement(chromeDriver, key);
            wait(1);
            if (arananelement.isDisplayed())
                logger.info("element var");
            else
                logger.info("element goruntulenmiyor");
        } catch (NoSuchElementException e) {
            logger.info("element yok");

        }

    }

    @Step({"<element> elementin karakter sayisini al"})
    public void metinKaraktersayisiniAl(String element) {
        try {
            WebElement arananElement = jsonLocator.locateElement(chromeDriver, element);
            String alinanMetin = arananElement.getText();
            int alinanKarakterSayisi = alinanMetin.length();

            if (alinanKarakterSayisi < 10) {
                throw new Exception("Metin 10 karakterden az. Karakter Sayısı: " + alinanKarakterSayisi);
            } else if (alinanKarakterSayisi >= 10 && alinanKarakterSayisi <= 250) {
                logger.info("Metin 10 - 250 karakter içermektedir. Karakter Sayısı: " + alinanKarakterSayisi);
            } else {
                logger.info("Metin 10 - 250 karakter aralığında değildir. Karakter Sayısı: " + alinanKarakterSayisi);
            }
        } catch (Exception e) {
            Assertions.fail("Metin alınamadı veya bir hata oluştu: " + e.getMessage());
        }
    }

    @Step("<tab>. sekmeye odaklan")
    public void implementation1(String tab) {
        int tabNumber = Integer.parseInt(tab) - 1;
        final List<String> windowHandles = new ArrayList<>(chromeDriver.getWindowHandles());
        chromeDriver = chromeDriver.switchTo().window(windowHandles.get(tabNumber));
        System.out.println(tab + ". sekmeye gecis yapildi");
    }


       @Step({"Sayfanin basina gec"})
        public void scrollToTop() {
            JavascriptExecutor jsExecutor = (JavascriptExecutor) chromeDriver;
            jsExecutor.executeScript("window.scrollTo(0, 0);");
        }

    public void sayfasonu() {

        ((JavascriptExecutor) chromeDriver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
        wait(3);
    }

    @Step({"<metin> en cok gecen kelime"})
    public void kelimeSayisiniHesapla(String metin) {
        // Belirtilen metni içeren web elementlerini bulmak için jsonLocator kullanılıyor.
        List<WebElement> icerigiTutulanElementler = jsonLocator.locateElements(chromeDriver, metin);

        // Map kullanarak kelime sayılarını saklamak için bir yapı oluşturuluyor.
        Map<String, Integer> kelimeSayilari = new HashMap<>();
       int eskiSatirSayisi= 0;
        // Web element sayısı değiştiği sürece döngü devam ediyor.
        while (icerigiTutulanElementler.size() != eskiSatirSayisi) {
            eskiSatirSayisi = icerigiTutulanElementler.size();
            sayfasonu(); // Sayfa sonuna gitmek için bir fonksiyon çağrılıyor.
            icerigiTutulanElementler = jsonLocator.locateElements(chromeDriver, metin);
        }

        scrollToTop(); // Sayfanın en üstüne gitmek için bir fonksiyon çağrılıyor.

        // Belirtilen metni içeren web elementlerini tekrar buluyor.
        List<WebElement> arananSutunlar = jsonLocator.locateElements(chromeDriver, metin);

        // Bulunan her bir web elementi üzerinde dönülüyor.
        for (WebElement we : arananSutunlar) {
            // Web elementinin içerdiği metni boşluklara göre ayırarak dizi haline getiriyor.
            String[] sutundakiData = we.getText().split("\\s+");

            // Her bir kelimenin sayısını arttırma işlemi yapılıyor.
            for (String kelime : sutundakiData) {
                kelimeSayilari.put(kelime, kelimeSayilari.getOrDefault(kelime, 0) + 1);
            }
        }

        // En çok kullanılan kelimenin sayısını ve kelimeyi bulma işlemi yapılıyor.
        String enCokKullanilanKelime = null;
        int enCokKullanilanKelimeSayisi = 0;

        for (Map.Entry<String, Integer> entry : kelimeSayilari.entrySet()) {
            if (entry.getValue() > enCokKullanilanKelimeSayisi) {
                enCokKullanilanKelime = entry.getKey();
                enCokKullanilanKelimeSayisi = entry.getValue();

            }
        }

        // Sonuçları loglama işlemi yapılıyor.
        logger.info("En Cok Kullanılan Kelime: " + enCokKullanilanKelime);
        logger.info("Kelimenin Sayısı: " + enCokKullanilanKelimeSayisi);
    }

        }













