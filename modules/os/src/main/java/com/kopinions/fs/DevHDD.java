package com.kopinions.fs;

import com.kopinions.core.Disk;
import com.kopinions.fs.FS.File.Operation;
import com.kopinions.fs.FS.File.Status;
import com.kopinions.kernel.Kernel;
import com.kopinions.kernel.Report;
import com.kopinions.kernel.Reporter;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class DevHDD implements FS, Report<Map<String, Object>> {

  private final Operations operations;
  private Disk disk;


  public DevHDD(Disk disk) {
    this.disk = disk;
    operations = new Operations() {

      @Override
      public Operation<byte[]> read(int size) {
        return new ReadOperation(size);
      }

      @Override
      public Operation<Void> write(byte[] data) {
        return new WriteOperation(data);
      }

      @Override
      public Operation<Void> close() {
        return new CloseOperation();
      }
    };
  }


  @Override
  public File open(int location) {
    return new File(location, operations);
  }

  @Override
  public File root() {
    return new File(Kernel.HDD_FS_ROOT, operations);
  }

  @Override
  public void report(Reporter<Map<String, Object>> reporter) {
    HashMap<String, Object> message = new HashMap<>();
    message.put("size", disk.size());
    reporter.report(message);
  }

  private class WriteOperation implements Operation<Void> {

    private byte[] data;

    public WriteOperation(byte[] data) {
      this.data = data;
    }

    @Override
    public Void applied(File file) {
      ByteBuffer wrap = ByteBuffer.wrap(data);
      short[] array = wrap.asShortBuffer().array();
      for (int i = 0; i < array.length; i++) {
        disk.write(file.location + i * 2, array[i]);
      }
      return null;
    }
  }

  private class ReadOperation implements Operation<byte[]> {

    private int size;

    public ReadOperation(int size) {
      this.size = size;
    }

    @Override
    public byte[] applied(File file) {
      ByteBuffer allocate = ByteBuffer.allocate(size);
      for (int i = 0; i < size; i += 2) {
        short read = disk.read(file.location + i);
        ByteBuffer byteBuffer = ByteBuffer.allocate(2).putShort(read);
        allocate.put(byteBuffer.slice(0, Math.min(size, 2)));
      }
      file.offset += size;
      return allocate.array();
    }
  }

  private static class CloseOperation implements Operation<Void> {

    @Override
    public Void applied(File file) {
      file.status = Status.CLOSED;
      return null;
    }
  }
}
