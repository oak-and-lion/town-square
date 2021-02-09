import java.util.List;

public interface ICommandWorker {
    List<BooleanString> doWork(String commandArgs);
}
