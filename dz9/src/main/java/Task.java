/**
 * Дан интерфейс
 *
 * public interface Worker {
 *
 *     void doWork();
 *
 * }
 *
 * Необходимо написать программу, выполняющую следующее:
 *
 * Программа с консоли построчно считывает код метода doWork. Код не должен требовать импорта дополнительных классов.
 * После ввода пустой строки считывание прекращается и считанные строки добавляются в тело метода public void doWork() в файле SomeClass.java.
 * Файл SomeClass.java компилируется программой (в рантайме) в файл SomeClass.class.
 * Полученный файл подгружается в программу с помощью кастомного загрузчика
 * Метод, введенный с консоли, исполняется в рантайме (вызывается у экземпляра объекта подгруженного класса)
 */
public class Task {

    static final String doWorkPath = "src/main/resources/doWork.txt";
    static final String someClassCodePath = "src/main/resources/SomeClass.txt";



    public static void main(String[] args) throws Exception {
        Worker newWorker = CodeInserter.insert(someClassCodePath, doWorkPath);
        newWorker.doWork();
    }
}
