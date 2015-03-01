/*
 * Copyright 2012-2013 eBay Software Foundation and ios-driver committers
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.uiautomation.ios.application;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.openqa.selenium.WebDriverException;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.communication.device.DeviceType;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IOSRunningApplication {

  private static final Logger log = Logger.getLogger(IOSRunningApplication.class.getName());

  private final AppleLanguage currentLanguage;
  private final APPIOSApplication underlyingApplication;

  public IOSRunningApplication(AppleLanguage language, APPIOSApplication app) {
    this.underlyingApplication = app;
    this.currentLanguage = language;
  }

  public String getBundleId() {
    return underlyingApplication.getBundleId();
  }

  public boolean isSafari() {
    return underlyingApplication.isSafari();
  }
  
  public boolean isSimulator() {
      return underlyingApplication.isSimulator();
  }

  public String getDotAppAbsolutePath() {
    return underlyingApplication.getApplicationPath().getAbsolutePath();
  }

  // TODO will have to be synchronized, or copy the app.
  public void setDefaultDevice(DeviceType defaultDevice, boolean putDefaultFirst) {
    underlyingApplication.setDefaultDevice(defaultDevice, putDefaultFirst);
  }
  
  public void setSafariBuiltinFavorites() {
    underlyingApplication.setSafariBuiltinFavorites();
  }

  public AppleLanguage getCurrentLanguage() {
    return currentLanguage;
  }

  public String applyL10N(String locator){
    LocatorWithL10N l10n =  new LocatorWithL10N(this);
    return l10n.translate(locator);
  }

  public String applyL10NOnKey(String key){
    LocatorWithL10N l10n =  new LocatorWithL10N(this);
    return l10n.translateKey(key);
  }

  private ImmutableList<ContentResult> getPotentialMatches(String name) throws WebDriverException {
    if (underlyingApplication.getSupportedLanguages().contains(currentLanguage)) {
      return underlyingApplication.getDictionary(currentLanguage).getPotentialMatches(name);
    } else {
      return ImmutableList.of(new ContentResult(currentLanguage, name, name, name));
    }
  }

  public JsonObject getTranslations(String name) {
    JsonObject l10n = new JsonObject();
    l10n.addProperty ("matches", 0);
    if (name != null && !name.isEmpty() && !"null".equals(name)) {
      try {
        ImmutableList<ContentResult> results = getPotentialMatches(name);

        int size = results.size();
        Gson gson = new Gson ();
        if (size != 0) {
          l10n.addProperty ("matches", size);
          JsonArray keys = new JsonArray();
          for (ContentResult res : results) {
            keys.add (gson.fromJson (res.getKey (), JsonElement.class));
          }
          l10n.add ("key", keys);
        }

        JsonArray langs = new JsonArray();
        for (AppleLanguage language : underlyingApplication.getSupportedLanguages()) {
          JsonArray possibleMatches = new JsonArray();

          for (ContentResult res : results) {
            possibleMatches.add (gson.fromJson (underlyingApplication.translate(res, language), JsonElement.class));
          }
          JsonObject match = new JsonObject();
          match.add(language.toString(), possibleMatches);
          langs.add(match);

        }
        l10n.add("langs", langs);

      } catch (Exception e) {
        log.log(Level.SEVERE,"cannot find translation",e);
      }
    }
    return l10n;
  }

  public LanguageDictionary getCurrentDictionary() {
    return underlyingApplication.getDictionary(currentLanguage);
  }

  public LanguageDictionary getDictionary(String lang) {
    return underlyingApplication.getDictionary(lang);
  }

  public List<String> getSupportedLanguagesCodes() {
    return underlyingApplication.getSupportedLanguagesCodes();
  }

  public IOSCapabilities getCapabilities() {
    IOSCapabilities caps = underlyingApplication.getCapabilities();
    caps.setLanguage(currentLanguage.getIsoCode());
    return caps;
  }

  public String translate(ContentResult contentResult, AppleLanguage loc) {
    return underlyingApplication.translate(contentResult, loc);
  }

  public APPIOSApplication getUnderlyingApplication() {
    return underlyingApplication;
  }
  
  @Override
  public String toString() {
    return underlyingApplication.toString();
  }
}
