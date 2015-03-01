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
package org.uiautomation.ios.UIAModels.predicate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public abstract class PropertyEqualCriteria extends DecorableCriteria {

  private final String propertyName;
  private String value;

  private L10NStrategy l10nstrategy;
  private MatchingStrategy matchingStrategy;

  public PropertyEqualCriteria(String propertyName, String value) {
    this(propertyName, value, L10NStrategy.none, MatchingStrategy.exact);
  }

  public PropertyEqualCriteria(String propertyName, String value, L10NStrategy l10nStrategy,
      MatchingStrategy matchingStrategy) {
    this.propertyName = propertyName;
    this.value = value;
    this.l10nstrategy = l10nStrategy;
    this.matchingStrategy = matchingStrategy;
  }

  public JsonObject stringify() {
    JsonObject res = new JsonObject ();
    Gson gson = new GsonBuilder ().create ();
    String l10n = gson.toJson (l10nstrategy, new TypeToken<L10NStrategy> () { }.getType ());
    String match = gson.toJson (matchingStrategy, new TypeToken<MatchingStrategy> () { }.getType ());
    res.addProperty ("method", propertyName);
    res.addProperty ("expected", value);
    res.add ("l10n", gson.fromJson (l10n, JsonElement.class));
    res.add ("matching", gson.fromJson (match, JsonElement.class));
    return res;
  }

  public L10NStrategy getL10nstrategy() {
    return l10nstrategy;
  }

  public void setL10nstrategy(L10NStrategy l10nstrategy) {
    this.l10nstrategy = l10nstrategy;
  }

  public MatchingStrategy getMatchingStrategy() {
    return matchingStrategy;
  }

  public void setMatchingStrategy(MatchingStrategy matchingStrategy) {
    this.matchingStrategy = matchingStrategy;
  }

  public String getPropertyName() {
    return propertyName;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

}
