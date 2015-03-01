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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.IOSCapabilities;

public class SessionTests {

  private JsonObject empty = new JsonObject();
  private JsonObject simple1 = new JsonObject();
  private JsonObject array = new JsonObject();


  @BeforeClass
  public void setup() {
    simple1.addProperty ("boolean", true);
    simple1.addProperty ("string", "abc");

    array.addProperty ("boolean", true);
    array.addProperty ("string", "abc");
    JsonArray a = new JsonArray ();
    Gson gson = new Gson ();
    a.add(gson.fromJson ("a1", JsonElement.class));
    a.add(gson.fromJson ("a2", JsonElement.class));
    a.add(gson.fromJson ("a3", JsonElement.class));
    array.add("array", a);

  }

  @Test
  public void empty() {
    Map<String, Object> decoded = new IOSCapabilities(empty).getRawCapabilities();
    Assert.assertTrue(decoded.isEmpty());
  }


  @Test
  public void simple1() {
    Map<String, Object> decoded = new IOSCapabilities(simple1).getRawCapabilities();
    Assert.assertEquals(decoded.size(), simple1.entrySet ().size ());
    Assert.assertEquals(decoded.get("boolean"), true);
    Assert.assertEquals(decoded.get("string"), "abc");
  }

  @Test
  public void array() {
    Map<String, Object> decoded = new IOSCapabilities(array).getRawCapabilities();
    Assert.assertEquals(decoded.size(), array.entrySet ().size ());
    Assert.assertEquals(decoded.get("boolean"), true);
    Assert.assertEquals(decoded.get("string"), "abc");
    Assert.assertTrue(decoded.get("array") instanceof List);
    List<String> a = new ArrayList<String>((List) decoded.get("array"));
    Assert.assertEquals(a.size(), 3);
  }
}
