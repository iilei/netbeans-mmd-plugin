/*
 * Copyright 2016 Igor Maznitsa.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.igormaznitsa.mindmap.ide.commons;

import org.junit.Test;
import static org.junit.Assert.*;

public class DnDUtilsTest {
  
  @Test
  public void testExtractHtmlLink() {
    assertNull(DnDUtils.extractHtmlLink(true, ""));
    assertNull(DnDUtils.extractHtmlLink(true, "hello"));
    assertNull(DnDUtils.extractHtmlLink(true, "http://www.google.com"));
    assertEquals("https://www.google.com", DnDUtils.extractHtmlLink(true, "<a href=\"https://www.google.com\">http://www.google.com</a>"));
    assertEquals("https://www.google.com", DnDUtils.extractHtmlLink(true, "<a href=\"https://www.google.com\"  target=\"_blank\">http://www.google.com</a>"));
    assertEquals("https://www.google.com", DnDUtils.extractHtmlLink(true, "<a target=\"_blank\" href=\"https://www.google.com\">http://www.google.com</a>"));
    assertEquals("https://www.google.com", DnDUtils.extractHtmlLink(true, "   <a target=\"_blank\" href=\"https://www.google.com\"> http://www.google.com   </a>    "));
    assertEquals("https://www.google.ee/?gfe_rd=cr&ei=81ItWL_MF8mq8wfi9bXwAQ", DnDUtils.extractHtmlLink(true, "< a   h r e f = \" h t t p s : / / w w w . g o o g l e . e e / ? g f e _ r d = c r & e i = 8 1 I t W L _ M F 8 m q 8 w f i 9 b X w A Q \" > h t t p s : / / w w w . g o o g l e . e e / ? g f e _ r d = c r & e i = 8 1 I t W L _ M F 8 m q 8 w f i 9 b X w A Q < / a > "));
  }
  
}
