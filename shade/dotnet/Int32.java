package dotnet;

import system.Type;
import system.reflection.Assembly;
import system.reflection.MethodInfo;

/**
 * The type Int 32.
 */
public class Int32 {

    private static MethodInfo parseToInt32;

    private Int32(){


    }

    /**
     * Parse system . object.
     *
     * @param integer the integer
     * @return the system . object
     */
    public static system.Object parse(Object integer){
        if(parseToInt32 == null){
            Assembly assembly = Assembly.Load("mscorlib, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089");
            Type t = assembly.GetType("System.Int32");
            parseToInt32 = t.GetMethod("Parse", new system.Type[]{system.String.typeof()});
        }
        return parseToInt32.Invoke(null, new system.Object[] { new system.String(String.valueOf(integer))});
    }

}
