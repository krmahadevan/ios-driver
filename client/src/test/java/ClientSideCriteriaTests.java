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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.io.IOUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.uiautomation.ios.UIAModels.predicate.L10NStrategy;
import org.uiautomation.ios.UIAModels.predicate.MatchingStrategy;
import org.uiautomation.ios.UIAModels.predicate.NameCriteria;
import org.uiautomation.ios.client.uiamodels.impl.ClientSideCriteriaFactory;


public class ClientSideCriteriaTests {

  ClientSideCriteriaFactory factory;

  @BeforeClass
  public void setup() throws IOException {
    InputStream is = ClientSideCriteriaTests.class.getResourceAsStream("/ClientSideL10N.json");

    StringWriter writer = new StringWriter();
    IOUtils.copy(is, writer, "UTF-8");
    JsonArray json = new Gson().fromJson (writer.toString (), JsonElement.class).getAsJsonArray ();
    Map<String, String> content = new HashMap<String, String>();
    for (int i = 0; i < json.size (); i++) {
      JsonObject entry = json.get (i).getAsJsonObject ();
      Map.Entry<String, JsonElement> e =  entry.entrySet ().iterator ().next();
      content.put(e.getKey (), e.getValue ().getAsString ());
    }

    factory = new ClientSideCriteriaFactory(content);

  }

  @Test
  public void clienSideMapping() {
    NameCriteria c = factory.nameCriteria("abc", L10NStrategy.clientL10N,MatchingStrategy.exact);
    Assert.assertEquals(c.getValue(), "abc localisé.");
  }

}
