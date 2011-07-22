package org.motechproject.server.omod.tasks;

import org.easymock.IArgumentMatcher;

import java.text.SimpleDateFormat;
import java.util.Date;

class DateMatcher implements IArgumentMatcher {
      private Date expected;
      private SimpleDateFormat dateFormat;

      DateMatcher(Date expected) {
          this.expected = expected;
          dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
      }

      public boolean matches(Object obj) {
          Date actual = (Date) obj;
          boolean matched = dateFormat.format(expected).equals(dateFormat.format(actual));
          return matched;
      }

      public void appendTo(StringBuffer buffer) {
          buffer.append(expected);
      }
  }
