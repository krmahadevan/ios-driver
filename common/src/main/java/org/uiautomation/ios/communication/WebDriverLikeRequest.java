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
package org.uiautomation.ios.communication;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;

public class WebDriverLikeRequest {

  private String method;
  private String path;
  // TODO freynaud extract that to a dedicated object to avoid json objects
  // leaking exception
  // everywhere.
  private JsonObject payload;

  public WebDriverLikeRequest(HttpServletRequest request) throws IOException {
    method = request.getMethod();
    path = request.getPathInfo();
    String json = null;
    if (request.getInputStream() != null) {
      StringWriter w = new StringWriter();
      IOUtils.copy(request.getInputStream(), w, "UTF-8");
      json = w.toString();
    }
      JsonObject o = new JsonObject();
    if (json != null && !json.isEmpty()) {
      o = new Gson ().fromJson (json, JsonElement.class).getAsJsonObject ();

    }
    payload = o;
  }

  public WebDriverLikeRequest(String method, Path path, JsonObject payload) {
    this.method = method;
    this.path = path.getPath();
    this.payload = payload;
  }

  public WebDriverLikeRequest(String method, String path) {
    this(method, path, new JsonObject());
  }

  public WebDriverLikeRequest(String method, Path path) {
    this(method, path, new JsonObject());
  }

  public WebDriverLikeRequest(String method, String path, JsonObject payload) {
    this.method = method;
    this.path = path;
    this.payload = payload;
  }

  public WebDriverLikeRequest(String method, Path path, Map<String, ?> params) {
    this.method = method;
    this.path = path.getPath();
    TypeToken<HashMap<String, ?>> token = new TypeToken<HashMap<String, ?>> () {};
    Gson gson = new Gson ();
    String result = gson.toJson (params, token.getType ());
    this.payload = gson.fromJson (result, JsonElement.class).getAsJsonObject ();
  }

  public boolean hasPayload() {
    return payload != null && payload.entrySet ().size () != 0;
  }

  public String toString() {
    String res = method + ":" + path;
    if (hasPayload()) {
      res += "\n\tbody:" + payload;
    }
    return res;
  }

  public String toJSON() {
    return toJSON(0);
  }

  public String toJSON(int i)  {
    JsonObject o = new JsonObject();
    o.addProperty ("method", method);
    o.addProperty ("path", path);
    o.add ("payload", payload);
    Gson gson = new GsonBuilder ().setPrettyPrinting ().create ();
    return gson.toJson (o);
  }

  public String getMethod() {
    return method;
  }

  public String getPath() {
    return path;
  }

  public JsonObject getPayload() {
    return payload;
  }

  public WebDriverLikeCommand getGenericCommand() {
    return WebDriverLikeCommand.getCommand(method, path);
  }

  public String getVariableValue(String variable) {
    WebDriverLikeCommand genericCommand = getGenericCommand();
    int i = genericCommand.getIndex(variable);
    String[] pieces = path.split("/");
    return pieces[i];
  }

  public boolean hasVariable(String variable) {
    WebDriverLikeCommand genericCommand = getGenericCommand();
    boolean ok = genericCommand.path().contains(variable);
    return ok;
  }

  public String getSession() {
    return getVariableValue(":sessionId");
  }

  public boolean hasSession() {
    return hasVariable(":sessionId");
  }

}
