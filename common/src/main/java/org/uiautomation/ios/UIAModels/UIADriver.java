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
package org.uiautomation.ios.UIAModels;

import com.google.gson.JsonObject;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.UIAModels.configuration.DriverConfiguration;
import org.uiautomation.ios.UIAModels.predicate.Criteria;
import org.uiautomation.ios.communication.WebDriverLikeCommand;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface UIADriver extends DriverConfiguration {

  // public UIATarget getLocalTarget();

  public IOSCapabilities getCapabilities() throws Exception;

  public JsonObject logElementTree(File screenshot, boolean translation) throws WebDriverException;

  public void quit();

  public void pinchClose(int x1, int y1, int x2, int y2, int duration);

  public UIAAlert getAlert() throws NoAlertPresentException;

  // public void setTimeout(String type, int timeoutInSeconds);

  // public int getTimeout(String type);

  public <T extends UIAElement> T findElement(Criteria c) throws NoSuchElementException;

  public List<UIAElement> findElements(Criteria c);

  public void tap(int x, int y);

  // public Set<String> getWindowHandles();

  public void setConfiguration(WebDriverLikeCommand command, String key, Object value);

  public Map<String, Object> getConfiguration(WebDriverLikeCommand command);

  public WebElement findElement2(By by);

}
