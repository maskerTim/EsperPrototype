package org.example.engine;

import com.espertech.esper.common.client.EPCompiled;
import com.espertech.esper.common.client.configuration.Configuration;
import com.espertech.esper.common.client.module.Module;
import com.espertech.esper.common.client.module.ParseException;
import com.espertech.esper.compiler.client.CompilerArguments;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.compiler.client.EPCompiler;
import com.espertech.esper.compiler.client.EPCompilerProvider;
import com.espertech.esper.runtime.client.*;
import org.example.engine.exceptions.NoListenersException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EsperEngine {
    private static EsperEngine esperEngine = null;
    private EPCompiler epCompiler = null;
    private EPRuntime epRuntime = null;
    private EPCompiled epCompiled = null;
    private EPDeployment epDeployment = null;
    private Map<String, List<UpdateListener>> statementListener = new HashMap<String, List<UpdateListener>>();

    private EsperEngine() {}
    public static EsperEngine getInstance(){
        if(esperEngine == null){
            synchronized(EsperEngine.class) {
                if(esperEngine == null) {
                    esperEngine = new EsperEngine();
                }
            }
        }
        return esperEngine;
    }

    public void createCompiler(){
        epCompiler = EPCompilerProvider.getCompiler();
    }

    public void compile (File filepath, Configuration configuration) throws EPCompileException, IOException,
            ParseException {
        CompilerArguments compilerArguments = new CompilerArguments(configuration);
        Module module = epCompiler.readModule(filepath);
        epCompiled = epCompiler.compile(module, compilerArguments);
    }

    public void compile (URL url, Configuration configuration) throws IOException, ParseException,
            EPCompileException{
        CompilerArguments compilerArguments = new CompilerArguments(configuration);
        Module module = epCompiler.readModule(url);
        epCompiled = epCompiler.compile(module, compilerArguments);
    }

    public void createRuntime(Configuration configuration){
        epRuntime = EPRuntimeProvider.getDefaultRuntime(configuration);
    }

    public void deploy() throws EPDeployException {
        if(epRuntime != null && epCompiled != null){
            epDeployment = epRuntime.getDeploymentService().deploy(epCompiled);
        }
    }

    public void setStatement(String statement, List<UpdateListener> updateListeners){
        statementListener.put(statement, updateListeners);
    }

    public void setListener() throws NoListenersException{
        if(!statementListener.isEmpty()) {
            for (Map.Entry entry : statementListener.entrySet()) {
                EPStatement state = epRuntime.getDeploymentService().getStatement(epDeployment.getDeploymentId(),
                        entry.getKey().toString());
                List<UpdateListener> updateListeners = (List<UpdateListener>) entry.getValue();
                for (UpdateListener updateListener : updateListeners) {
                    state.addListener(updateListener);
                }
            }
        }else {
            throw new NoListenersException();
        }
    }

    public void sendEventJson(String json, String eventType){
        epRuntime.getEventService().sendEventJson(json, eventType);
    }
}
