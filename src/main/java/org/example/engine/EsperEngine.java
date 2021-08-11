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

/**
 * Esper engine function for Complex Event Processing (CEP)
 */
public class EsperEngine {
    private static EsperEngine esperEngine = null;
    private EPCompiler epCompiler = null;
    private EPRuntime epRuntime = null;
    private EPCompiled epCompiled = null;
    private EPDeployment epDeployment = null;
    private Map<String, List<UpdateListener>> statementListener = new HashMap<String, List<UpdateListener>>();

    private EsperEngine() {}

    /**
     * Singleton Pattern, get esper engine instance
     * @return Esper engine instance
     */
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

    /**
     * Create the esper compiler for compiling EPL module
     */
    public void createCompiler(){
        epCompiler = EPCompilerProvider.getCompiler();
    }

    /**
     * Compile EPL module
     * @param filepath: The path of EPL file
     * @param configuration: Configuration instance
     * @throws EPCompileException: Error for esper compilation
     * @throws IOException: Error for IO
     * @throws ParseException: Error for parsing EPL module
     */
    public void compile (File filepath, Configuration configuration) throws EPCompileException, IOException,
            ParseException {
        CompilerArguments compilerArguments = new CompilerArguments(configuration);
        Module module = epCompiler.readModule(filepath);
        epCompiled = epCompiler.compile(module, compilerArguments);
    }

    /**
     * Compile EPL module
     * @param url: The URL of EPL file
     * @param configuration: Configuration instance
     * @throws EPCompileException: Error for esper compilation
     * @throws IOException: Error for IO
     * @throws ParseException: Error for parsing EPL module
     */
    public void compile (URL url, Configuration configuration) throws IOException, ParseException,
            EPCompileException{
        CompilerArguments compilerArguments = new CompilerArguments(configuration);
        Module module = epCompiler.readModule(url);
        epCompiled = epCompiler.compile(module, compilerArguments);
    }

    /**
     * Create runtime instance
     * @param configuration: Configuration instance
     */
    public void createRuntime(Configuration configuration){
        epRuntime = EPRuntimeProvider.getDefaultRuntime(configuration);
    }

    /**
     * Deploy the compiled EPL
     * @throws EPDeployException: Error for deploying
     */
    public void deploy() throws EPDeployException {
        if(epRuntime != null && epCompiled != null){
            epDeployment = epRuntime.getDeploymentService().deploy(epCompiled);
        }
    }

    /**
     * Set the EPL statement
     * @param statement: Statement name
     * @param updateListeners: list of listeners for a statement
     */
    public void setStatement(String statement, List<UpdateListener> updateListeners){
        statementListener.put(statement, updateListeners);
    }

    /**
     * Set listeners for a statement
     * @throws NoListenersException: Error that no listener is set
     */
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

    /**
     * Send event with JSON format to esper engine
     * @param json: The message by JSON format
     * @param eventType: Event name/type
     */
    public void sendEventJson(String json, String eventType){
        epRuntime.getEventService().sendEventJson(json, eventType);
    }
}
