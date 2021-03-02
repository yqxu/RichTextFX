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

    public static String defaultDebugCmd = "python3.9 -m pdb";
    public static String testFile = "test.py -stdin=log";

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
        CompletableFuture<CMDStep.StepResp> resp = pdbShell.step();
        CompletableFuture fu = resp.thenRun(()->pdbShell.next());
        fu = fu.thenRun(()->pdbShell.next());
        fu = fu.thenRun(()->pdbShell.next());
        fu = fu.thenRun(()->pdbShell.next());
        fu = fu.thenRun(()->pdbShell.next());
        fu = fu.thenRun(()->pdbShell.next());
        fu = fu.thenRun(()->pdbShell.next());
        fu = fu.thenRun(()->pdbShell.next());
        fu = fu.thenRun(()->pdbShell.next());
//        shell.print();
//        System.out.println("where\n");
//        shell.where();
//        System.out.println("next\n");
//        shell.next();
//        System.out.println("continue\n");
//        shell.continueExe();

    }
}
