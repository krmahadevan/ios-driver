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
package org.uiautomation.ios.command.uiautomation;

import com.google.gson.JsonObject;
import org.openqa.selenium.remote.Response;
import org.uiautomation.ios.UIAModels.configuration.WorkingMode;
import org.uiautomation.ios.communication.WebDriverLikeRequest;
import org.uiautomation.ios.IOSServerManager;
import org.uiautomation.ios.command.BaseNativeCommandHandler;

public class GetCurrentContextNHandler extends BaseNativeCommandHandler {

  public GetCurrentContextNHandler(IOSServerManager driver, WebDriverLikeRequest request) {
    super(driver, request);

  }

  @Override
  public Response handle() throws Exception {
    WorkingMode mode = getIOSDualDriver().getWorkingMode();
    String value = mode.toString();
    if (mode == WorkingMode.Web) {
      value = WorkingMode.Web + "_" + getWebDriver().getWindowHandle();
    }
    Response resp = new Response();
    resp.setSessionId(getSession().getSessionId());
    resp.setStatus(0);
    resp.setValue(value);
    return resp;
  }

  @Override
  public JsonObject configurationDescription()  {
    return noConfigDefined();
  }
}
