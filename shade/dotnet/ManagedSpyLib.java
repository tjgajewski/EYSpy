package dotnet;

import net.sf.jni4net.Bridge;

import java.io.File;
import java.io.IOException;

/**
 * The type Managed spy lib.
 */
public class ManagedSpyLib {

    private static Boolean isLoaded = false;

    /**
     * Init.
     */
    public static void init(){
        if(isLoaded == false) {
            try {
                Bridge.init(new File(System.getProperty("user.dir")+"\\src\\main\\resources\\JNI4Net\\jni4net.n.w64.v40-0.8.9.0.dll"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bridge.LoadAndRegisterAssemblyFrom(new File(System.getProperty("user.dir")+"\\src\\main\\resources\\JNI4Net\\ManagedSpy4Java.dll"));
            isLoaded = true;
        }
    }
}
