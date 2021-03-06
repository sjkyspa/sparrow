package com.kopinions.kernel;

import com.kopinions.Address;
import com.kopinions.mm.Page.PageDirectory;
import com.kopinions.mm.VMM;
import java.util.Objects;
import java.util.Queue;

public class Proc implements Comparable<Proc> {

  public boolean scheduleNeeded() {
    return need_resched;
  }

  @Override
  public int compareTo(Proc o) {
    return Integer.compare(this.priority, o.priority);
  }

  public VMM vmm() {
    return vmm;
  }

  public void tick() {
    if (timeSlice > 0) {
      timeSlice--;
    }
    if (timeSlice == 0) {
      need_resched = true;
    }
  }

  public short pid() {
    return pid;
  }

  enum State {
    CREATED,
    READY,
    RUNNING,
    BLOCKED,
    TERMINATED,
  }

  static class Context {
    public short eip;
    public short esp;
    public short ebx;
    public short ecx;
    public short edx;
    public short esi;
    public short edi;
    public short ebp;
  }

  State state;
  short pid;
  int runs;
  VMM vmm;
  Context context;
  String name;
  boolean need_resched;
  int exitCode;
  int priority;
  Queue<Proc> processes;
  int timeSlice;


  Proc(short pid) {
    this.pid = pid;
    need_resched = false;
    state = State.READY;
    priority = 1;
    runs = 0;
    timeSlice = 1000;
  }

  void killed() {
  }

  void blocked() {
    this.state = State.BLOCKED;
  }

  void awakened() {
    this.state = State.RUNNING;
  }

  void exited() {
    PageDirectory pgdir = vmm.pgdir();
    pgdir.free(new Address(pgdir.as()));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Proc proc = (Proc) o;
    return pid == proc.pid;
  }

  @Override
  public int hashCode() {
    return Objects.hash(pid);
  }
}
