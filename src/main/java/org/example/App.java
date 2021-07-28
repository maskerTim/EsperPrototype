package org.example;

import com.espertech.esper.common.client.configuration.Configuration;
import com.espertech.esper.common.client.module.ParseException;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.runtime.client.*;
import org.example.engine.EsperEngine;
import org.example.listener.PersonListener;
import org.example.listener.PersonListenerTest;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Esper Prototype
 * Author: Masker Tim
 */
public class App 
{
    public static void main( String[] args )
    {
        EsperEngine esperEngine = EsperEngine.getInstance();
        Configuration configuration = new Configuration();

        File f = new File("/home/martai/EsperTest/src/main/java/org/example/events/test.epl");

        esperEngine.createCompiler();
        try {
            esperEngine.compile(f, configuration);
        }catch (EPCompileException | ParseException | IOException ex){
            // handle exception here
            throw new RuntimeException(ex);
        }
        esperEngine.createRuntime(configuration);
        try {
            esperEngine.deploy();
        }catch (EPDeployException ex){
            // handle exception here
            throw new RuntimeException(ex);
        }

        List<UpdateListener> mystate = new ArrayList<>();
        List<UpdateListener> mytest = new ArrayList<>();
        mystate.add(new PersonListener());
        mytest.add(new PersonListenerTest());

        esperEngine.setStatement("my-statement", mystate);
        esperEngine.setStatement("my-test", mytest);

        esperEngine.setListener();

        JSONObject jo = new JSONObject();
        jo.put("name","Peter");
        jo.put("age",10);

        esperEngine.sendEventJson(jo.toString(), "JSONPersonEvent");

        System.out.println( "Hello World!" );
    }
}
