import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class CodeInserter {

    /**
     * Считываение кода класса SomeClass
     * @param sourcePath
     * @return
     * @throws FileNotFoundException
     */
    private static String readCode(String sourcePath) throws FileNotFoundException {
        InputStream stream = new FileInputStream(sourcePath);
        String separator = System.getProperty("line.separator");
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines().collect(Collectors.joining(separator));
    }

    /**
     * Считывание кода метода doWork и добавление его в код класса SomeClass
     * @param doWorkPath
     * @param sourceCode
     * @return
     * @throws FileNotFoundException
     */
    private static String addCode(String doWorkPath, String sourceCode) throws FileNotFoundException {
        InputStream stream = new FileInputStream(doWorkPath);
        String separator = System.getProperty("line.separator");
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String code = reader.lines().collect(Collectors.joining(separator));

        StringBuffer source = new StringBuffer(sourceCode);

        String searchSubString = "public void doWork(){";
        int index = source.indexOf(searchSubString) + searchSubString.length();

        source.insert(index + 1, code);

        return source.toString();
    }

    /**
     * Сохраение класса .java
     * @param source
     * @return
     * @throws IOException
     */
    private static Path saveSource(String source) throws IOException {
        String tmpProperty = System.getProperty("java.io.tmpdir");
        Path sourcePath = Paths.get(tmpProperty, "SomeClass.java");
        Files.write(sourcePath, source.getBytes());
        return sourcePath;
    }

    /**
     * Компиляция класса SomeClass
     * @param javaFile
     * @return
     */
    private static Path compileSource(Path javaFile) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        compiler.run(null, null, null, javaFile.toFile().getAbsolutePath());
        return javaFile.getParent().resolve("SomeClass.class");
    }

    /**
     * Получение инстанса SomeClass
     * @param javaClass
     * @return
     * @throws MalformedURLException
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    private static Object runClass(Path javaClass) throws MalformedURLException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        URL classUrl = javaClass.getParent().toFile().toURI().toURL();
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{classUrl});
        Class<?> someClass = Class.forName("SomeClass", true, classLoader);

        return someClass.newInstance();
    }

    public static Worker insert(String sourcePath, String doWorkPath) throws Exception {
        String someClassSource = readCode(sourcePath);
        String source = addCode(doWorkPath, someClassSource);
        Path javaFile = saveSource(source);
        Path classFile = compileSource(javaFile);
        Object obj = runClass(classFile);
        return (Worker)obj;
    }
}
