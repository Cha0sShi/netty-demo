package protocol;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class SequenceIdGenerator {
  static  AtomicInteger id=new AtomicInteger();
    public static int nextId(){ return id.incrementAndGet(); }
}
