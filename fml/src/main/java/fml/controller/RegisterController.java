package fml.controller;

import com.holland.common.utils.Files;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/class")
public class RegisterController {

    private final Map<String, Object> injectMap = new HashMap<>();
    private final ClassLoader cl = new URLClassLoader(new URL[]{new File("." + File.separator).toURI().toURL()});

    public RegisterController() throws MalformedURLException {
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(MultipartFile multipartFile, String fullName, LifecycleEnum lifecycle) throws Exception {
        final byte[] bytes = multipartFile.getBytes();
        final String filename = multipartFile.getOriginalFilename();
        assert filename != null;

        final String[] paths = fullName.split("\\.");
        String recursionPath = "";
        for (int i = 0; i < paths.length - 1; i++) {
            recursionPath += paths[i] + File.separator;
            final File file = new File(recursionPath);
            if (!file.exists())
                //noinspection ResultOfMethodCallIgnored
                file.mkdirs();
        }
        final File file = new File(recursionPath + File.separator + filename);
        try (OutputStream outStream = new FileOutputStream(file)) {
            outStream.write(bytes);
        }

        if (lifecycle.equals(LifecycleEnum.all)) {
            final Class<?> aClass = cl.loadClass(fullName);
            final Object o = aClass.getDeclaredConstructor().newInstance();
            injectMap.put(fullName, o);
        }

        final String extension = Files.extension(filename);
        String path = file.toURI().getPath();
        if ("java".equals(extension)) {
            final String os = System.getProperty("os.name");
            if (os.toLowerCase().startsWith("win")) {
                if (path.startsWith("/")) {
                    path = path.substring(1);
                }
            }
            final Process exec = Runtime.getRuntime().exec("javac " + path);
            //noinspection StatementWithEmptyBody
            while (exec.isAlive()) {
            }
            exec.destroy();
        }

        return ResponseEntity.ok().body(path);
    }

    @ResponseBody
    @PostMapping("/invoke")
    public ResponseEntity<?> invoke(@RequestBody InvokeDTO invokeDTO) throws Exception {
        final Class<?> aClass;
        try {
            aClass = cl.loadClass(invokeDTO.fullName);
        } catch (ClassNotFoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("ClassNotFound: " + invokeDTO.fullName);
        }

        final Class<?>[] cs = new Class<?>[invokeDTO.classes.length];
        for (int i = 0; i < invokeDTO.classes.length; i++) {
            cs[i] = Class.forName(invokeDTO.classes[i]);
        }

        final Method run = aClass.getMethod(invokeDTO.method, cs);
        Object o = injectMap.get(invokeDTO.fullName);
        if (o == null)
            o = aClass.getDeclaredConstructor().newInstance();
        return ResponseEntity.ok().body(run.invoke(o, invokeDTO.args));
    }

    public static class InvokeDTO {
        public String fullName;
        public String method;
        public String[] classes;
        public Object[] args;
    }

    private enum LifecycleEnum {
        session, all;
    }
}
