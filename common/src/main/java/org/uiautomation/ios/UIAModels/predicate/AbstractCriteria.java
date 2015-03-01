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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.WebDriverException;

public abstract class AbstractCriteria implements Criteria {

  public static <T extends Criteria> T parse(JsonObject serialized) throws Exception {
    return parse(serialized, null);
  }

  @SuppressWarnings("unchecked")
  public static <T extends Criteria> T parse(JsonObject serialized, CriteriaDecorator decorator) {
    try {
      int nbKeys = serialized.entrySet ().size ();
      switch (nbKeys) {
      case KEYS_IN_EMPTY_CRITERIA:
        return (T) new EmptyCriteria();
      case KEYS_IN_COMPOSED_CRITERIA:
        String key = (String) serialized.entrySet ().iterator ().next().getKey ();
        CompositionType type = CompositionType.valueOf(key);
        return (T) buildComposedCriteria(serialized, type, decorator);
      case KEYS_IN_LOCATION_CRITERIA:
        int x = serialized.get("x").getAsInt ();
        int y = serialized.get("y").getAsInt ();
        return (T) buildLocationCriteria(serialized, x, y, decorator);
      case KEYS_IN_PROPERTY_CRITERIA:
        String method = serialized.get("method").getAsString ();
        String tmp = method.substring(0, 1).toUpperCase() + method.toLowerCase().substring(1) + "Criteria";
        String clazz = AbstractCriteria.class.getPackage().getName() + "." + tmp;
        Class<? extends PropertyEqualCriteria> c = (Class<? extends PropertyEqualCriteria>) Class.forName(clazz);
        return (T) buildPropertyBaseCriteria(serialized, c, decorator);
      default:
        throw new InvalidSelectorException("can't find the type : " + serialized.toString());
      }
    } catch (Exception e) {
      throw new WebDriverException(e);
    }

  }

  private static final int KEYS_IN_EMPTY_CRITERIA = 0;
  private static final int KEYS_IN_COMPOSED_CRITERIA = 1;
  private static final int KEYS_IN_PROPERTY_CRITERIA = 4;
  private static final int KEYS_IN_LOCATION_CRITERIA = 2;

  private static LocationCriteria buildLocationCriteria(JsonObject serialized, int x, int y, CriteriaDecorator decorator) {
    LocationCriteria res = new LocationCriteria(x, y);
    res.addDecorator(decorator);
    return res;
  }

  private static ComposedCriteria buildComposedCriteria(JsonObject serialized, CompositionType type,
      CriteriaDecorator decorator) throws Exception {
    JsonArray array = serialized.get (type.toString ()).getAsJsonArray ();
    if (type == CompositionType.NOT && array.size () != 1) {
      throw new InvalidSelectorException("negative criteria apply to 1 criteria only " + serialized);
    }
    List<Criteria> criterias = new ArrayList<Criteria>();

    for (int i = 0; i < array.size (); i++) {
        JsonObject c = array.get (i).getAsJsonObject ();
      Criteria crit = parse(c, decorator);
      criterias.add(crit);
    }

    Object[] args = new Object[] { criterias };
    Class<?>[] argsClass = new Class[] { List.class };

    Constructor<?> c = type.getAssociatedClass().getConstructor(argsClass);
    ComposedCriteria crit = (ComposedCriteria) c.newInstance(args);
    crit.addDecorator(decorator);
    crit.decorate();
    return crit;
  }

  private static PropertyEqualCriteria buildPropertyBaseCriteria(JsonObject serialized,
      Class<? extends PropertyEqualCriteria> clazz, CriteriaDecorator decorator) throws Exception {
    String expected = serialized.get ("expected").getAsString ();
    String matching = serialized.get ("matching").getAsString ();
    String l10n = serialized.get("l10n").getAsString ();

    Object[] args = new Object[] { expected, L10NStrategy.valueOf(l10n), MatchingStrategy.valueOf(matching) };
    Class<?>[] argsClass = new Class[] { String.class, L10NStrategy.class, MatchingStrategy.class };

    Constructor<?> c = clazz.getConstructor(argsClass);
    PropertyEqualCriteria crit = (PropertyEqualCriteria) c.newInstance(args);
    crit.addDecorator(decorator);
    crit.decorate();
    return crit;
  }
}
