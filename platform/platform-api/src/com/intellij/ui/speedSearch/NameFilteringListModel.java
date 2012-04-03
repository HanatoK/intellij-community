/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * @author max
 */
package com.intellij.ui.speedSearch;

import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.text.StringUtilRt;
import com.intellij.util.Function;

import javax.swing.*;

public class NameFilteringListModel<T> extends FilteringListModel<T> {
  private final Function<T, String> myNamer;

  private int myFullMatchIndex = -1;
  private int myStartsWithIndex = -1;
  private final SpeedSearch mySpeedSearch;

  public NameFilteringListModel(JList list, final Function<T, String> namer, final Condition<String> filter,
                                SpeedSearch speedSearch) {
    super(list);
    mySpeedSearch = speedSearch;
    myNamer = namer;
    setFilter(namer != null ? new Condition<T>() {
      public boolean value(T t) {
        return filter.value(namer.fun(t));
      }
    } : null);
  }

  @Override
  protected void addToFiltered(T elt) {
    super.addToFiltered(elt);

    if (myNamer != null) {
      String filterString = StringUtilRt.toUpperCase(mySpeedSearch.getFilter());
      String candidateString = StringUtilRt.toUpperCase(myNamer.fun(elt));
      int index = getSize() - 1;

      if (myFullMatchIndex == -1 && filterString.equals(candidateString)) {
        myFullMatchIndex = index;
      }

      if (myStartsWithIndex == -1 && candidateString.startsWith(filterString)) {
        myStartsWithIndex = index;
      }
    }
  }

  @Override
  public void refilter() {
    myFullMatchIndex = -1;
    myStartsWithIndex = -1;
    super.refilter();
  }

  public int getClosestMatchIndex() {
    return myFullMatchIndex != -1 ? myFullMatchIndex : myStartsWithIndex;
  }
}
