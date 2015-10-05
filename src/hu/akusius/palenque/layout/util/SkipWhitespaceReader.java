package hu.akusius.palenque.layout.util;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

/**
 * A nem látható karaktereket kiszűrő {@link Reader}.
 * @author Bujdosó Ákos
 */
@SuppressWarnings("SynchronizeOnNonFinalField")
public final class SkipWhitespaceReader extends FilterReader {

  public SkipWhitespaceReader(Reader in) {
    super(in);
  }

  @Override
  public int read() throws IOException {
    synchronized (lock) {
      while (true) {
        int ch = super.read();
        if (ch == -1 || !Character.isWhitespace(ch)) {
          return ch;
        }
      }
    }
  }

  @Override
  public int read(char[] cbuf, int off, int len) throws IOException {
    synchronized (lock) {
      // Egyszerű (és lassú) megoldás
      int i;
      for (i = 0; i < len; i++) {
        int ch = read();
        if (ch == -1) {
          break;
        }
        cbuf[off + i] = (char) ch;
      }
      return i;
    }
  }

  @Override
  public long skip(long n) throws IOException {
    synchronized (lock) {
      int i;
      for (i = 0; i < n; i++) {
        if (read() == -1) {
          break;
        }
      }
      return i;
    }
  }

  @Override
  public boolean markSupported() {
    return false;
  }

  @Override
  public void mark(int readAheadLimit) throws IOException {
    throw new IOException("mark/reset not supported");
  }

  @Override
  public void reset() throws IOException {
    throw new IOException("mark/reset not supported");
  }
}
