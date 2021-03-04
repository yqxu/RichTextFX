package org.fxmisc.richtext.demo.pdb;

import org.fxmisc.richtext.demo.pdb.commands.CMDNext;
import org.fxmisc.richtext.demo.pdb.commands.CMDStartDebug;
import org.fxmisc.richtext.demo.pdb.commands.CMDStep;

import java.util.concurrent.CompletableFuture;


public class PDBShell {

    Shell shell;

    public void setShell(Shell shell) {
        this.shell = shell;
    }

    public Shell getShell() {
        return shell;
    }

    public static String defaultDebugCmd = "python3.9 gdpdb.py";
    public static String testFile = "test.py";

    public static PDBShell starDebug(String location)  {
        PDBShell pdbShell = new PDBShell();
        pdbShell.setShell(new Shell());
        pdbShell.getShell().startWithCmd(CMDStartDebug.newInstance(location));
        return pdbShell;
    }

    public CompletableFuture<CMDStep.StepResp> step(){
        return (CompletableFuture<CMDStep.StepResp>) shell.executeCMD(CMDStep.newInstance());
    }

    public CompletableFuture<CMDNext.NextResp> next(){
        return (CompletableFuture<CMDNext.NextResp>) shell.executeCMD(CMDNext.newInstance());
    }

    public void args(){
        //TODO：
        shell.runAsync("args");
    }

    public void continueExe(){
        shell.runAsync("continue");
    }

    public void enable(){
        shell.runAsync("enable");
    }

    public void ignore(){
        shell.runAsync("ignore");
    }
    public void jump(){
        shell.runAsync("jump");
    }
    public void list(){
        shell.runAsync("list");
    }
    public void print(){
        shell.runAsync("print");
    }
    public void returnMethod(){
        shell.runAsync("return");
    }
    public void where(){
        shell.runAsync("where");
    }




    public static void main(String args[]){
        PDBShell pdbShell =  PDBShell.starDebug(testFile);
        CompletableFuture<CMDStep.StepResp> resp2 = pdbShell.step();
        System.out.println("CMD step");
        System.out.println(resp2.join().getCodeStr());

        CompletableFuture<CMDNext.NextResp> resp = pdbShell.next();
        System.out.println("CMD next");
        System.out.println(resp.join().getCodeStr());
        System.out.println(resp.join().getCodeLocation().getLineNumber());
        System.out.println(resp.join().getCodeLocation().getFilePath());

        resp = pdbShell.next();
        System.out.println("CMD next");
        System.out.println(resp.join().getCodeStr());
        System.out.println(resp.join().getCodeLocation().getLineNumber());
        System.out.println(resp.join().getCodeLocation().getFilePath());

        resp = pdbShell.next();
        System.out.println("CMD next");
        System.out.println(resp.join().getCodeStr());

        System.out.println(resp.join().getCodeLocation().getLineNumber());
        System.out.println(resp.join().getCodeLocation().getFilePath());

        resp = pdbShell.next();
        System.out.println("CMD next");
        System.out.println(resp.join().getCodeStr());

        System.out.println(resp.join().getCodeLocation().getLineNumber());
        System.out.println(resp.join().getCodeLocation().getFilePath());

        resp = pdbShell.next();
        System.out.println("CMD next");
        System.out.println(resp.join().getCodeStr());

        System.out.println(resp.join().getCodeLocation().getLineNumber());
        System.out.println(resp.join().getCodeLocation().getFilePath());

        resp = pdbShell.next();
        System.out.println("CMD next");
        System.out.println(resp.join().getCodeStr());
        System.out.println(resp.join().getCodeLocation().getLineNumber());
        System.out.println(resp.join().getCodeLocation().getFilePath());

        int respQSize = pdbShell.getShell().responseQ.size();
        int desQSize = pdbShell.getShell().deserializerQ.size();
        System.out.println(respQSize == desQSize && respQSize == 0);

    }
}
