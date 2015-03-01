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
package org.uiautomation.ios.client.uiamodels.impl.configuration;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.uiautomation.ios.UIAModels.configuration.CommandConfiguration;
import org.uiautomation.ios.client.uiamodels.impl.RemoteIOSDriver;
import org.uiautomation.ios.communication.Path;
import org.uiautomation.ios.communication.WebDriverLikeCommand;
import org.uiautomation.ios.communication.WebDriverLikeRequest;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RemoteCommandConfiguration implements CommandConfiguration {

  private final WebDriverLikeCommand command;
  private final RemoteIOSDriver driver;
  private static final Logger log = Logger.getLogger(RemoteCommandConfiguration.class.getName());


  public RemoteCommandConfiguration(WebDriverLikeCommand command, RemoteIOSDriver driver) {
    this.command = command;
    this.driver = driver;
  }

  @Override
  public void set(String key, Object value) {
    try {
      Gson gson = new Gson ();
      String text = gson.toJson (value, new TypeToken<Object> (){}.getType ());
      JsonElement jsonElement = gson.fromJson (text, JsonElement.class);
      JsonObject payload = new JsonObject();
      payload.add (key, jsonElement);
      Path p = new Path(WebDriverLikeCommand.CONFIGURE);
      // session/:sessionId/configure/command/:command
      p.validateAndReplace(":sessionId", driver.getSessionId().toString());
      p.validateAndReplace(":command", command.name());
      WebDriverLikeRequest request = new WebDriverLikeRequest("POST", p, payload);
      //driver.execute(request);
    } catch (Exception e) {
      log.log(Level.SEVERE, "Configure failed.", e);
    }

  }

  @Override
  public Object get(String key) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Map<String, Object> getAll() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public Object opt(String key, Object defaultValue) {
    // TODO Auto-generated method stub
    return null;
  }
}
