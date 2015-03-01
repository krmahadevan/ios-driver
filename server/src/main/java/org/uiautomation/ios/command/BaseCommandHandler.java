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

package org.uiautomation.ios.command;

import com.google.gson.JsonObject;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.IOSServerManager;
import org.uiautomation.ios.ServerSideSession;
import org.uiautomation.ios.UIAModels.configuration.CommandConfiguration;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.drivers.IOSDualDriver;
import org.uiautomation.ios.drivers.RemoteIOSNativeDriver;
import org.uiautomation.ios.drivers.RemoteIOSWebDriver;
import org.uiautomation.ios.instruments.commandExecutor.UIAutomationCommandExecutor;
import org.uiautomation.ios.wkrdp.WebKitSeemsCorruptedException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public abstract class BaseCommandHandler implements Handler {

  private static final Logger log = Logger.getLogger(BaseCommandHandler.class.getName());
  private final IOSServerManager server;
  private final ServerSideSession session;
  private final IOSDualDriver driver;
  private final WebDriverLikeRequest request;
  private final List<PreHandleDecorator> preDecorators = new ArrayList<PreHandleDecorator>();
  private final List<PostHandleDecorator> postDecorators = new ArrayList<PostHandleDecorator>();

  public BaseCommandHandler(IOSServerManager server, WebDriverLikeRequest request) {
    this.server = server;
    this.request = request;

    if (request.hasVariable(":sessionId")) {
      session = server.getSession(request.getSession());
      driver = session.getDualDriver();
    } else {
      session = null;
      driver = null;
    }
  }

  public BaseCommandHandler(ServerSideSession session) {
    this.server = session.getIOSServerManager();
    this.session = session;
    driver = session.getDualDriver();
    request = null;
  }

  protected RemoteIOSWebDriver getWebDriver() {
    return getIOSDualDriver().getRemoteWebDriver();
  }

  protected RemoteIOSNativeDriver getNativeDriver() {
    return getIOSDualDriver().getNativeDriver();
  }

  protected IOSDualDriver getIOSDualDriver() {
    return getSession().getDualDriver();
  }

  public ServerSideSession getSession() {
    return session;
  }

  @Override
  public void addDecorator(PostHandleDecorator decorator) {
    postDecorators.add(decorator);

  }

  @Override
  public void addDecorator(PreHandleDecorator decorator) {
    preDecorators.add(decorator);
  }

  protected IOSServerManager getServer() {
    return server;
  }

  protected WebDriverLikeRequest getRequest() {
    return request;
  }

  public UIAutomationCommandExecutor communication() {
    return getNativeDriver().communication();
  }

  @Override
  public Response handleAndRunDecorators() throws Exception {
    for (PreHandleDecorator pre : preDecorators) {
      pre.decorate(request);
    }
    Response response = null;
    try {
      response = handle();
    } catch (WebKitSeemsCorruptedException e) {
      log.warning("WebKitSeemsCorruptedException.Attempting a restart.");
      driver.restartWebkit();
      log.warning("restart done.");
      response = handle();
    }

    for (PostHandleDecorator post : postDecorators) {
      post.decorate(response);
    }
    return response;
  }

  protected <T> T getConf(String key) {
    return getConf(key, (T) null);
  }

  protected <T> T getConf(String key, T defaultValue) {
    CommandConfiguration conf = getSession().configure(getRequest().getGenericCommand());
    T res = (T) conf.get(key);
    return res != null ? res : defaultValue;
  }

  public abstract JsonObject configurationDescription() ;

  protected JsonObject noConfigDefined() {
    JsonObject res = new JsonObject();
    res.addProperty ("No config for this command", "");
    return res;
  }

  protected Response createResponse(Object value) {
    Response r = new Response();
    r.setSessionId(getSession().getSessionId());
    r.setStatus(0);
    r.setValue(value);
    return r;
  }

}
